/*
 * Created on 16 feb 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox.component;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.sejda.impl.sambox.util.FontUtils.fontOrFallback;
import static org.sejda.util.RequireUtils.requireArg;
import static org.sejda.util.RequireUtils.requireIOCondition;
import static org.sejda.util.RequireUtils.requireNotBlank;
import static org.sejda.util.RequireUtils.requireNotNullArg;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;

import org.sejda.impl.sambox.util.FontUtils;
import org.sejda.model.toc.ToCPolicy;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PDPageContentStream;
import org.sejda.sambox.pdmodel.PDPageTree;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.font.PDFont;
import org.sejda.sambox.pdmodel.font.PDType1Font;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
import org.sejda.sambox.util.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component creating a table of content
 * 
 * @author Andrea Vacondio
 */
public class TableOfContentsCreator {

    private static final Logger LOG = LoggerFactory.getLogger(TableOfContentsCreator.class);

    private static final int FONT_SIZE = 14;
    private static final int LINE_HEIGHT = FONT_SIZE + 9;
    private static final PDRectangle PAGE_SIZE = PDRectangle.A4;
    private static final int MARGIN = 40;
    private static final float FONT_SCALE = (float) FONT_SIZE / 1000;
    private static final String SEPARATOR = "  ";

    private final Deque<ToCItem> items = new LinkedList<>();
    private PDDocument document;
    private ToCPolicy policy;

    public TableOfContentsCreator(ToCPolicy policy, PDDocument document) {
        requireNotNullArg(document, "Containing document cannot be null");
        this.document = document;
        this.policy = policy;
    }

    /**
     * Adds to the ToC the given text with the given annotation associated
     * 
     * @param text
     * @param page
     * @param annotation
     */
    public void appendItem(String text, long page, PDAnnotation annotation) {
        requireNotBlank(text, "ToC item cannot be blank");
        requireArg(page > 0, "ToC item cannot point to a negative page");
        requireNotNullArg(annotation, "ToC annotation cannot be null");
        if (shouldGenerateToC()) {
            items.add(new ToCItem(text, page, annotation));
        }
    }

    /**
     * Generates a ToC and prepend it to the given document
     * 
     * @param document
     */
    public void addToC() {
        requireNotNullArg(document, "Cannot add a ToC to a null document");
        try {
            PDPageTree pagesTree = document.getPages();
            ofNullable(generateToC()).filter(l -> !l.isEmpty()).ifPresent(t -> {
                t.descendingIterator().forEachRemaining(p -> {
                    if (pagesTree.getCount() > 0) {
                        pagesTree.insertBefore(p, pagesTree.get(0));
                    } else {
                        pagesTree.add(p);
                    }
                });
            });
        } catch (IOException e) {
            LOG.error("An error occured while create the ToC. Skipping ToC creation.", e);
        }
    }

    private LinkedList<PDPage> generateToC() throws IOException {
        LinkedList<PDPage> pages = new LinkedList<>();
        if (shouldGenerateToC()) {
            PDFont font = PDType1Font.HELVETICA;
            int maxRows = (int) (PAGE_SIZE.getHeight() - (MARGIN * 2)) / LINE_HEIGHT;
            while (!items.isEmpty()) {
                int row = 0;
                float separatorWidth = stringLength(font, SEPARATOR);
                float separatingLineEndingX = getSeparatingLineEndingX(separatorWidth, font);
                PDPage page = createPage(pages);
                try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
                    while (!items.isEmpty() && row < maxRows) {
                        ToCItem i = items.poll();
                        if (nonNull(i)) {
                            row++;
                            font = fontOrFallback(i.text, font, () -> FontUtils.findFontFor(document, i.text));
                            requireIOCondition(nonNull(font), "Unable to find suitable font for " + i.text);
                            float y = PAGE_SIZE.getHeight() - MARGIN - (row * LINE_HEIGHT);
                            stream.beginText();
                            stream.setFont(font, FONT_SIZE);
                            stream.setTextMatrix(new Matrix(AffineTransform.getTranslateInstance(MARGIN, y)));
                            String itemText = sanitize(i.text, font, separatingLineEndingX, separatorWidth);
                            stream.showText(itemText);

                            String pageString = SEPARATOR + Long.toString(i.page);
                            stream.setTextMatrix(new Matrix(AffineTransform.getTranslateInstance(
                                    getPageNumberX(separatorWidth, PDType1Font.HELVETICA, i), y)));
                            stream.setFont(PDType1Font.HELVETICA, FONT_SIZE);
                            stream.showText(pageString);
                            stream.endText();
                            i.annotation
                                    .setRectangle(new PDRectangle(MARGIN, y, PAGE_SIZE.getWidth() - MARGIN, FONT_SIZE));
                            page.getAnnotations().add(i.annotation);
                            // we didn't sanitieze the text so it's shorter then the available space and needs a separator line
                            if (itemText.equals(i.text)) {
                                stream.moveTo(MARGIN + separatorWidth + stringLength(font, i.text), y);
                                stream.lineTo(separatingLineEndingX, y);
                                stream.setLineWidth(0.5f);
                                stream.stroke();
                            }
                        }
                    }
                }
            }
        }
        return pages;
    }

    private String sanitize(String text, PDFont font, float separatingLineEndingX, float separatorWidth)
            throws IOException {
        float maxLen = PAGE_SIZE.getWidth() - MARGIN - (PAGE_SIZE.getWidth() - separatingLineEndingX) - separatorWidth;
        if (stringLength(font, text) > maxLen) {
            LOG.debug("Truncating ToC text to fit available space");
            int currentLength = text.length() / 2;
            while (stringLength(font, text.substring(0, currentLength)) > maxLen) {
                currentLength /= 2;
            }
            int currentChunk = currentLength;
            while (currentChunk > 1) {
                currentChunk /= 2;
                if (stringLength(font, text.substring(0, currentLength + currentChunk)) < maxLen) {
                    currentLength += currentChunk;
                }
            }
            return text.substring(0, currentLength);
        }
        return text;
    }

    private PDPage createPage(LinkedList<PDPage> pages) {
        LOG.debug("Creating new ToC page");
        PDPage page = new PDPage(PAGE_SIZE);
        pages.add(page);
        return page;
    }

    private float getSeparatingLineEndingX(float separatorWidth, PDFont font) throws IOException {
        return getPageNumberX(separatorWidth, font, items.peekLast());
    }

    private float getPageNumberX(float separatorWidth, PDFont font, ToCItem i) throws IOException {
        return PAGE_SIZE.getWidth() - MARGIN - separatorWidth - stringLength(font, Long.toString(i.page));
    }

    private float stringLength(PDFont font, String text) throws IOException {
        return font.getStringWidth(text) * FONT_SCALE;
    }

    public boolean hasToc() {
        return !items.isEmpty();
    }

    public boolean shouldGenerateToC() {
        return policy != ToCPolicy.NONE;
    }

    private static class ToCItem {
        public final String text;
        public final long page;
        public final PDAnnotation annotation;

        public ToCItem(String text, long page, PDAnnotation annotation) {
            this.text = text;
            this.page = page;
            this.annotation = annotation;
        }
    }
}
