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
import org.sejda.model.parameter.MergeParameters;
import org.sejda.model.toc.ToCPolicy;
import org.sejda.sambox.cos.COSArray;
import org.sejda.sambox.cos.COSInteger;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PDPageContentStream;
import org.sejda.sambox.pdmodel.PDPageTree;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.font.PDFont;
import org.sejda.sambox.pdmodel.font.PDType1Font;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDPageFitWidthDestination;
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
    private static final int DEFAULT_MARGIN = 72;
    private static final String SEPARATOR = "  ";

    private final Deque<ToCItem> items = new LinkedList<>();
    private PDDocument document;
    private PDRectangle pageSize = null;
    private float fontSize = DEFAULT_FONT_SIZE;
    private float margin = DEFAULT_MARGIN;
    private PDFont font = PDType1Font.HELVETICA;
    private float lineHeight;
    private int maxRowsPerPage;
    private int tocNumberOfPages;
    private MergeParameters params;
    private PageTextWriter writer;

    public TableOfContentsCreator(MergeParameters params, PDDocument document) {
        requireNotNullArg(document, "Containing document cannot be null");
        requireNotNullArg(params, "Parameters cannot be null");
        this.document = document;
        this.params = params;
        this.writer = new PageTextWriter(document);
        recalculateFontSize();
    }

    /**
     * Adds to the ToC the given text with the given annotation associated
     * 
     * @param text
     * @param pageNumber
     * @param page
     */
    public void appendItem(String text, long pageNumber, PDPage page) {
        requireNotBlank(text, "ToC item cannot be blank");
        requireArg(pageNumber > 0, "ToC item cannot point to a negative page");
        requireNotNullArg(page, "ToC page cannot be null");
        if (shouldGenerateToC()) {
            items.add(new ToCItem(text, pageNumber, linkAnnotationFor(page)));
        }
    }

    private PDAnnotationLink linkAnnotationFor(PDPage importedPage) {
        PDPageFitWidthDestination pageDest = new PDPageFitWidthDestination();
        pageDest.setPage(importedPage);
        PDAnnotationLink link = new PDAnnotationLink();
        link.setDestination(pageDest);
        link.setBorder(new COSArray(COSInteger.ZERO, COSInteger.ZERO, COSInteger.ZERO));
        return link;
    }

    /**
     * Generates a ToC and prepend it to the given document
     */
    public void addToC() {
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
                if (params.isBlankPageIfOdd() && toCPagesCount % 2 == 1) {
                    PDPage lastTocPage = pagesTree.get(toCPagesCount - 1);
                    PDPage blankPage = new PDPage(lastTocPage.getMediaBox());
                    pagesTree.insertAfter(blankPage, lastTocPage);
                }
            });
        } catch (IOException | TaskIOException e) {
            LOG.error("An error occurred while create the ToC. Skipping ToC creation.", e);
        }
    }

    private LinkedList<PDPage> generateToC() throws TaskIOException, IOException {
        LinkedList<PDPage> pages = new LinkedList<>();
        if (shouldGenerateToC()) {

            while (!items.isEmpty()) {
                int row = 0;

                float separatorWidth = stringLength(SEPARATOR);
                float separatingLineEndingX = getSeparatingLineEndingX(separatorWidth, tocNumberOfPages);

                PDPage page = createPage(pages);
                try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
                    while (!items.isEmpty() && row < maxRowsPerPage) {
                        ToCItem i = items.poll();
                        if (nonNull(i)) {
                            float y = pageSize().getHeight() - margin - (row * lineHeight);
                            float x = margin;
                            String itemText = sanitize(i.text, separatingLineEndingX, separatorWidth);
                            writeText(page, itemText, x, y);

                            String pageString = SEPARATOR + Long.toString(i.page + tocNumberOfPages);
                            float x2 = getPageNumberX(separatorWidth, i.page + tocNumberOfPages);
                            writeText(page, pageString, x2, y);

                            i.annotation.setRectangle(
                                    new PDRectangle(margin, y, pageSize().getWidth() - (2 * margin), fontSize));
                            page.getAnnotations().add(i.annotation);

                            // we didn't sanitize the text so it's shorter then the available space and needs a separator line
                            if (itemText.equals(i.text)) {
                                stream.moveTo(margin + separatorWidth + stringLength(i.text), y);
                                stream.lineTo(separatingLineEndingX, y);
                                stream.setLineWidth(0.5f);
                                stream.stroke();
                            }
                        }
                        row++;
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
        float maxLen = pageSize().getWidth() - margin - (pageSize().getWidth() - separatingLineEndingX)
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
        return pageSize().getWidth() - margin - separatorWidth - stringLength(Long.toString(pageNumber));
    }

    private float stringLength(String text) throws TaskIOException {
        return writer.getStringWidth(text, font, fontSize);
    }

    public boolean hasToc() {
        return !items.isEmpty();
    }

    public boolean shouldGenerateToC() {
        return params.getTableOfContentsPolicy() != ToCPolicy.NONE;
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
        this.margin = scalingFactor * DEFAULT_MARGIN;
        this.lineHeight = (float) (fontSize + (fontSize * 0.7));
        this.maxRowsPerPage = (int) ((pageSize().getHeight() - (margin * 2) + lineHeight) / lineHeight);
        if (shouldGenerateToC()) {
            tocNumberOfPages = params.getInputList().size() / maxRowsPerPage
                    + (params.getInputList().size() % maxRowsPerPage == 0 ? 0 : 1);
            if (params.isBlankPageIfOdd() && tocNumberOfPages % 2 == 1) {
                tocNumberOfPages++;
            }
        }
    }

    private PDRectangle pageSize() {
        return Optional.ofNullable(pageSize).orElse(PDRectangle.A4);
    }

    public float getFontSize() {
        return fontSize;
    }

    /**
     * @return the number of pages this toc will consist of
     */
    public long tocNumberOfPages() {
        return tocNumberOfPages;
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
