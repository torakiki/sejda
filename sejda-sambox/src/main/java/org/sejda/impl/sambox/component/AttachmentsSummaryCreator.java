/*
 * Created on 20 gen 2017
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
import static org.sejda.util.RequireUtils.requireNotBlank;
import static org.sejda.util.RequireUtils.requireNotNullArg;

import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;

import org.sejda.model.exception.TaskIOException;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PDPageContentStream;
import org.sejda.sambox.pdmodel.PDPageTree;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.font.PDFont;
import org.sejda.sambox.pdmodel.font.PDType1Font;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotationFileAttachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component creating a table of content showing attachments annotations
 * 
 * @author Andrea Vacondio
 *
 */
public class AttachmentsSummaryCreator {
    private static final Logger LOG = LoggerFactory.getLogger(AttachmentsSummaryCreator.class);

    private static final int DEFAULT_FONT_SIZE = 14;
    private static final int DEFAULT_LINE_HEIGHT = DEFAULT_FONT_SIZE + 9;
    private static final int DEFAULT_MARGIN = 40;
    private static final String SEPARATOR = "  ";
    private static final PDFont FONT = PDType1Font.HELVETICA;
    private static final PDRectangle PAGE_SIZE = PDRectangle.A4;

    private final Deque<ToCItem> items = new LinkedList<>();
    private PDDocument document;
    private PageTextWriter writer;

    public AttachmentsSummaryCreator(PDDocument document) {
        requireNotNullArg(document, "Containing document cannot be null");
        this.document = document;
        this.writer = new PageTextWriter(document);
    }

    /**
     * Adds to the ToC the given text with the given annotation associated
     * 
     * @param attachmentName
     * @param annotation
     */
    public void appendItem(String attachmentName, PDAnnotationFileAttachment annotation) {
        requireNotBlank(attachmentName, "Attachment name cannot be blank");
        requireNotNullArg(annotation, "ToC annotation cannot be null");
        items.add(new ToCItem(attachmentName, annotation));
    }

    /**
     * Generates a ToC and prepend it to the given document
     */
    public void addToC() {
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
        } catch (IOException | TaskIOException e) {
            LOG.error("An error occurred while create the ToC. Skipping ToC creation.", e);
        }
    }

    private LinkedList<PDPage> generateToC() throws TaskIOException, IOException {
        LinkedList<PDPage> pages = new LinkedList<>();
        int maxRows = (int) ((PAGE_SIZE.getHeight() - (DEFAULT_MARGIN * 2)) / DEFAULT_LINE_HEIGHT);

        while (!items.isEmpty()) {
            int row = 0;

            float separatorWidth = stringLength(SEPARATOR);

            PDPage page = createPage(pages);
            try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
                while (!items.isEmpty() && row < maxRows) {
                    ToCItem i = items.poll();
                    if (nonNull(i)) {
                        row++;
                        float y = PAGE_SIZE.getHeight() - DEFAULT_MARGIN - (row * DEFAULT_LINE_HEIGHT);
                        float x = DEFAULT_MARGIN;
                        String itemText = sanitize(i.text, separatorWidth);
                        writeText(page, itemText, x, y);

                        float textLenght = stringLength(itemText);
                        writeText(page, SEPARATOR, DEFAULT_MARGIN + textLenght, y);

                        i.annotation.setRectangle(new PDRectangle(DEFAULT_MARGIN + textLenght + separatorWidth, y,
                                DEFAULT_FONT_SIZE, DEFAULT_FONT_SIZE));
                        page.getAnnotations().add(i.annotation);

                    }
                }
            }
        }
        return pages;
    }

    private void writeText(PDPage page, String s, float x, float y) throws TaskIOException {
        writer.write(page, new Point.Float(x, y), s, FONT, (double) DEFAULT_FONT_SIZE, Color.BLACK);
    }

    private String sanitize(String text, float separatorWidth) throws TaskIOException {
        // page width - margin (right and left) - annotation space - separator space
        float maxLen = PAGE_SIZE.getWidth() - (2 * DEFAULT_MARGIN) - DEFAULT_FONT_SIZE - separatorWidth;
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
        PDPage page = new PDPage(PAGE_SIZE);
        pages.add(page);
        return page;
    }

    private float stringLength(String text) throws TaskIOException {
        return writer.getStringWidth(text, FONT, DEFAULT_FONT_SIZE);
    }

    public boolean hasToc() {
        return !items.isEmpty();
    }

    private static class ToCItem {
        public final String text;
        public final PDAnnotationFileAttachment annotation;

        public ToCItem(String text, PDAnnotationFileAttachment annotation) {
            this.text = text;
            this.annotation = annotation;
        }
    }
}
