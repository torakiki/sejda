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
import static org.sejda.commons.util.RequireUtils.requireArg;
import static org.sejda.commons.util.RequireUtils.requireNotBlank;
import static org.sejda.commons.util.RequireUtils.requireNotNullArg;
import static org.sejda.impl.sambox.component.OutlineUtils.pageDestinationFor;

import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.sejda.impl.sambox.util.FontUtils;
import org.sejda.model.exception.TaskException;
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
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;
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
    private MergeParameters params;
    private PageTextWriter writer;

    public TableOfContentsCreator(MergeParameters params, PDDocument document) {
        requireNotNullArg(document, "Containing document cannot be null");
        requireNotNullArg(params, "Parameters cannot be null");
        this.document = document;
        this.params = params;
        this.writer = new PageTextWriter(document);
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
        requireNotAlreadyGenerated();
        if (shouldGenerateToC()) {
            items.add(new ToCItem(text, pageNumber, linkAnnotationFor(page)));
        }
    }

    private PDAnnotationLink linkAnnotationFor(PDPage importedPage) {
        PDPageXYZDestination pageDest = pageDestinationFor(importedPage);
        PDAnnotationLink link = new PDAnnotationLink();
        link.setDestination(pageDest);
        link.setBorder(new COSArray(COSInteger.ZERO, COSInteger.ZERO, COSInteger.ZERO));
        return link;
    }

    /**
     * Generates a ToC and prepend it to the given document
     * 
     * @throws TaskException
     *             if there is an error generating the ToC
     */
    public int addToC() throws TaskException {
        return addToC(0);
    }

    /**
     * Generates a ToC and inserts it in the doc at before the given page number
     * 
     * @throws TaskException
     *             if there is an error generating the ToC
     */
    public int addToC(int beforePageNumber) throws TaskException {
        PDPageTree pagesTree = document.getPages();
        LinkedList<PDPage> toc = generateToC();
        
        toc.descendingIterator().forEachRemaining(p -> {
            if (pagesTree.getCount() > 0) {
                pagesTree.insertBefore(p, pagesTree.get(beforePageNumber));
            } else {
                pagesTree.add(p);
            }
        });
        
        return toc.size();
    }

    private LinkedList<PDPage> generatedToC;
    private LinkedList<PDPage> generateToC() throws TaskIOException {
        if(generatedToC == null) {
            generatedToC = _generateToC();
        }
        
        return generatedToC;
    }

    private LinkedList<PDPage> _generateToC() throws TaskIOException {
        // we need to know how many pages the ToC itself has
        // so we can write the page numbers of the ToC items correctly
        // but can only know how many pages the ToC has after we generate it
        
        // therefore, 1) generate ToC using a dummy estimate for the tocNumberOfPages
        int tocNumberOfPages = _generateToC(0).size();
        
        // 2) generate ToC again with correct tocNumberOfPages
        return _generateToC(tocNumberOfPages);
    }
    
    private LinkedList<PDPage> _generateToC(int tocNumberOfPages) throws TaskIOException {
        LinkedList<PDPage> pages = new LinkedList<>();
        recalculateDimensions();
        
        int maxRowsPerPage = (int) ((pageSize().getHeight() - (margin * 2) + lineHeight) / lineHeight);
        Deque<ToCItem> items = new LinkedList<>(this.items);
         
        if (shouldGenerateToC()) {
            while (!items.isEmpty()) {
                int row = 0;

                float separatorWidth = stringLength(SEPARATOR);
                float separatingLineEndingX = getSeparatingLineEndingX(separatorWidth);

                PDPage page = createPage(pages);
                try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
                    while (!items.isEmpty() && row < maxRowsPerPage) {
                        // peek, don't poll. we don't know yet if the item will fit on this page
                        // eg: long item that wraps on multiple lines, but there's no room for all of them
                        // (1 row available on page, but item wraps on 2 rows).
                        ToCItem i = items.peek();
                        if (nonNull(i)) {
                            float y = pageSize().getHeight() - margin - (row * lineHeight);
                            float x = margin;

                            List<String> lines = multipleLinesIfRequired(i.text, separatingLineEndingX, separatorWidth);
                            if (row + lines.size() > maxRowsPerPage) {
                                // does not fit on multiple lines, write on next page
                                row = maxRowsPerPage;
                                continue;
                            }
                            // fits even if on multiple lines, take out of the items thing
                            items.poll();

                            // write item on multiple lines if it's too long to fit on just one
                            // regular scenario is a single line
                            for (int j = 0; j < lines.size(); j++) {
                                String line = lines.get(j);
                                writeText(page, line, x, y);

                                if (j < lines.size() - 1) {
                                    // if we've written the item last line, don't increment the row and y coordinate
                                    // we'll continue writing on the same row the ____________ <pagenum> part.
                                    row++;
                                    y = pageSize().getHeight() - margin - (row * lineHeight);
                                }
                            }

                            long pageNumber = i.page + tocNumberOfPages;
                            String pageString = SEPARATOR + Long.toString(pageNumber);
                            float x2 = getPageNumberX(separatorWidth);
                            writeText(page, pageString, x2, y);

                            // make the item clickable and link to the page number

                            // we want a little spacing between link annotations, so they are not adjacent, to prevent mis-clicking
                            // the spacing will be applied top and bottom and is the difference between line height and font height
                            float spacing = (lineHeight - fontSize) / 2;
                            float height = lineHeight * lines.size() - 2 * spacing;
                            i.annotation.setRectangle(
                                    new PDRectangle(margin, y - spacing, pageSize().getWidth() - (2 * margin), height));
                            page.getAnnotations().add(i.annotation);

                            // draw line between item text and page number
                            // chapter 1 _____________________ 12
                            // chapter 2 _____________________ 15
                            // TODO: dots .............. instead of line _________________________
                            String lastLine = lines.get(lines.size() - 1);
                            stream.moveTo(margin + separatorWidth + stringLength(lastLine), y);
                            stream.lineTo(separatingLineEndingX, y);
                            stream.setLineWidth(0.5f);
                            stream.stroke();
                        }
                        row++;
                    }
                } catch (IOException e) {
                    throw new TaskIOException("An error occurred while create the ToC", e);
                }
            }

            if (params.isBlankPageIfOdd() && pages.size() % 2 == 1) {
                PDPage lastTocPage = pages.getLast();
                PDPage blankPage = new PDPage(lastTocPage.getMediaBox());
                pages.add(blankPage);
            }
        }
        
        return pages;
    }

    private void requireNotAlreadyGenerated() {
        if(generatedToC != null) {
            throw new IllegalStateException("ToC has already been generated");
        }
    }

    private void writeText(PDPage page, String s, float x, float y) throws TaskIOException {
        writer.write(page, new Point.Float(x, y), s, font, (double) fontSize, Color.BLACK);
    }

    private List<String> multipleLinesIfRequired(String text, float separatingLineEndingX, float separatorWidth)
            throws TaskIOException {
        float maxWidth = pageSize().getWidth() - margin - (pageSize().getWidth() - separatingLineEndingX)
                - separatorWidth;
        return FontUtils.wrapLines(text, font, fontSize, maxWidth, document);
    }

    private PDPage createPage(LinkedList<PDPage> pages) {
        LOG.debug("Creating new ToC page");
        PDPage page = new PDPage(pageSize());
        pages.add(page);
        return page;
    }

    private float getSeparatingLineEndingX(float separatorWidth) throws TaskIOException {
        return getPageNumberX(separatorWidth);
    }

    private float getPageNumberX(float separatorWidth) throws TaskIOException {
        return pageSize().getWidth() - margin - separatorWidth
                /* leave enough space for a 4 digit page number, assumes 9 to be a wide enough digit */        
                - stringLength(Long.toString(9999));
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
        requireNotAlreadyGenerated();
        if (this.pageSize == null) {
            this.pageSize = pageSize;
        }
    }

    private void recalculateDimensions() {
        float scalingFactor = pageSize().getHeight() / PDRectangle.A4.getHeight();

        this.fontSize = scalingFactor * DEFAULT_FONT_SIZE;
        this.margin = scalingFactor * DEFAULT_MARGIN;
        this.lineHeight = (float) (fontSize + (fontSize * 0.7));
    }

    private PDRectangle pageSize() {
        return Optional.ofNullable(pageSize).orElse(PDRectangle.A4);
    }

    public float getFontSize() {
        return fontSize;
    }

    PDDocument getDoc() {
        return document;
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
