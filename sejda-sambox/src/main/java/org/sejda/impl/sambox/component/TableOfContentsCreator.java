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

import static java.lang.Math.ceil;
import static java.lang.Math.round;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.sejda.util.RequireUtils.requireArg;
import static org.sejda.util.RequireUtils.requireNotBlank;
import static org.sejda.util.RequireUtils.requireNotNullArg;

import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;

import org.sejda.model.exception.TaskIOException;
import org.sejda.model.toc.ToCPolicy;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PDPageContentStream;
import org.sejda.sambox.pdmodel.PDPageTree;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.font.PDFont;
import org.sejda.sambox.pdmodel.font.PDType1Font;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component creating a table of content
 * 
 * @author Andrea Vacondio
 */
public class TableOfContentsCreator {

    private static final Logger LOG = LoggerFactory.getLogger(TableOfContentsCreator.class);

    private static final int DEFAULT_FONT_SIZE = 14;
    private static final int DEFAULT_LINE_HEIGHT = DEFAULT_FONT_SIZE + 9;
    private static final int DEFAULT_MARGIN = 40;
    private static final String SEPARATOR = "  ";

    private final Deque<ToCItem> items = new LinkedList<>();
    private PDDocument document;
    private ToCPolicy policy;
    private PDRectangle pageSize = null;
    private float fontSize;
    private PDFont font = PDType1Font.HELVETICA;
    private float lineHeight;

    private PageTextWriter writer;

    public TableOfContentsCreator(ToCPolicy policy, PDDocument document) {
        requireNotNullArg(document, "Containing document cannot be null");
        this.document = document;
        this.writer = new PageTextWriter(document);
        this.policy = ofNullable(policy).orElse(ToCPolicy.NONE);
        recalculateFontSize();
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
     * Set addBlankPageIfOdd to true if you'd like to add an extra blank page at the end of an odd-sized ToC (1, 3, 5, etc pages).
     * (Makes it easier to do duplex printing.)
     */
    public void addToC(boolean addBlankPageIfOdd) {
        try {
            PDPageTree pagesTree = document.getPages();
            ofNullable(generateToC()).filter(l -> !l.isEmpty()).ifPresent(t -> {
                int toCPagesCount = t.size();
                t.descendingIterator().forEachRemaining(p -> {
                    if (pagesTree.getCount() > 0) {
                        pagesTree.insertBefore(p, pagesTree.get(0));
                    } else {
                        pagesTree.add(p);
                    }
                });
                if(addBlankPageIfOdd && toCPagesCount % 2 == 1) {
                    PDPage lastTocPage = pagesTree.get(toCPagesCount - 1);
                    PDPage blankPage = new PDPage(lastTocPage.getMediaBox());
                    pagesTree.insertBefore(blankPage, lastTocPage);
                }
            });
        } catch (IOException | TaskIOException e ) {
            LOG.error("An error occurred while create the ToC. Skipping ToC creation.", e);
        }
    }

    private LinkedList<PDPage> generateToC() throws TaskIOException, IOException {
        LinkedList<PDPage> pages = new LinkedList<>();
        if (shouldGenerateToC()) {
            int maxRows = (int) ((pageSize().getHeight() - (DEFAULT_MARGIN * 2)) / lineHeight);
            long indexPages = round(ceil((double) items.size() / maxRows));

            while (!items.isEmpty()) {
                int row = 0;

                float separatorWidth = stringLength(SEPARATOR);
                float separatingLineEndingX = getSeparatingLineEndingX(separatorWidth, indexPages);

                PDPage page = createPage(pages);
                try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
                    while (!items.isEmpty() && row < maxRows) {
                        ToCItem i = items.poll();
                        if (nonNull(i)) {
                            row++;
                            float y = pageSize().getHeight() - DEFAULT_MARGIN - (row * lineHeight);
                            float x = DEFAULT_MARGIN;
                            String itemText = sanitize(i.text, separatingLineEndingX, separatorWidth);
                            writeText(page, itemText, x, y);

                            String pageString = SEPARATOR + Long.toString(i.page + indexPages);
                            float x2 = getPageNumberX(separatorWidth, i.page + indexPages);
                            writeText(page, pageString, x2, y);

                            i.annotation.setRectangle(
                                    new PDRectangle(DEFAULT_MARGIN, y, pageSize().getWidth() - (2 * DEFAULT_MARGIN), fontSize));
                            page.getAnnotations().add(i.annotation);

                            // we didn't sanitize the text so it's shorter then the available space and needs a separator line
                            if (itemText.equals(i.text)) {
                                stream.moveTo(DEFAULT_MARGIN + separatorWidth + stringLength(i.text), y);
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

    private void writeText(PDPage page, String s, float x, float y) throws TaskIOException {
        writer.write(page, new Point.Float(x, y), s, font, (double) fontSize, Color.BLACK);
    }

    private String sanitize(String text, float separatingLineEndingX, float separatorWidth) throws TaskIOException {
        float maxLen = pageSize().getWidth() - DEFAULT_MARGIN - (pageSize().getWidth() - separatingLineEndingX)
                - separatorWidth;
        if (stringLength(text) > maxLen) {
            LOG.debug("Truncating ToC text to fit available space");
            int currentLength = text.length() / 2;
            while (stringLength(text.substring(0, currentLength)) > maxLen) {
                currentLength /= 2;
            }
            int currentChunk = currentLength;
            while (currentChunk > 1) {
                currentChunk /= 2;
                if (stringLength(text.substring(0, currentLength + currentChunk)) < maxLen) {
                    currentLength += currentChunk;
                }
            }
            return text.substring(0, currentLength);
        }
        return text;
    }

    private PDPage createPage(LinkedList<PDPage> pages) {
        LOG.debug("Creating new ToC page");
        PDPage page = new PDPage(pageSize());
        pages.add(page);
        return page;
    }

    private float getSeparatingLineEndingX(float separatorWidth, long indexPages) throws TaskIOException {
        return getPageNumberX(separatorWidth, items.peekLast().page + indexPages);
    }

    private float getPageNumberX(float separatorWidth, long pageNumber) throws TaskIOException {
        return pageSize().getWidth() - DEFAULT_MARGIN - separatorWidth - stringLength(Long.toString(pageNumber));
    }

    private float stringLength(String text) throws TaskIOException {
        return writer.getStringWidth(text, font, fontSize);
    }

    public boolean hasToc() {
        return !items.isEmpty();
    }

    public boolean shouldGenerateToC() {
        return policy != ToCPolicy.NONE;
    }

    public void pageSizeIfNotSet(PDRectangle pageSize) {
        if (this.pageSize == null) {
            this.pageSize = pageSize;
            recalculateFontSize();
        }
    }

    private void recalculateFontSize() {
        float scalingFactor = pageSize().getHeight() / PDRectangle.A4.getHeight();

        this.fontSize = scalingFactor * DEFAULT_FONT_SIZE;
        this.lineHeight = scalingFactor * DEFAULT_LINE_HEIGHT;
    }

    private PDRectangle pageSize() {
        return Optional.ofNullable(pageSize).orElse(PDRectangle.A4);
    }

    public float getFontSize() {
        return fontSize;
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
