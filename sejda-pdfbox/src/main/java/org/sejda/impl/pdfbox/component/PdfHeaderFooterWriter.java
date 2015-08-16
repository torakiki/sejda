/*
 * Created on 13/nov/2012
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * This file is part of the Sejda source code
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.pdfbox.component;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.sejda.impl.pdfbox.util.FontUtils.getStandardType1Font;

import java.io.Closeable;
import java.io.IOException;
import java.util.SortedSet;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.sejda.model.HorizontalAlign;
import org.sejda.model.VerticalAlign;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.parameter.SetHeaderFooterParameters;
import org.sejda.model.pdf.TextStampPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component adding header or footer to PDF doc.
 * 
 * @author Andrea Vacondio
 * 
 */
public class PdfHeaderFooterWriter implements Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(PdfHeaderFooterWriter.class);

    // TODO define as a params member
    private static final Float DEFAULT_MARGIN = 30F;

    private PDDocumentHandler documentHandler;
    private int totalPages;

    /**
     * @param documentHandler
     *            the document handler holding the document where we want to write the footer
     */
    public PdfHeaderFooterWriter(PDDocumentHandler documentHandler) {
        this.documentHandler = documentHandler;
        this.totalPages = documentHandler.getNumberOfPages();
    }

    public void write(SetHeaderFooterParameters parameters, int currentFileCounter) throws TaskIOException {
        PDFont font = defaultIfNull(getStandardType1Font(parameters.getFont()), PDType1Font.HELVETICA);
        Double fontSize = defaultIfNull(parameters.getFontSize(), 10d);

        HorizontalAlign hAlign = defaultIfNull(parameters.getHorizontalAlign(), HorizontalAlign.CENTER);
        VerticalAlign vAlign = defaultIfNull(parameters.getVerticalAlign(), VerticalAlign.BOTTOM);
        String what = vAlign == VerticalAlign.BOTTOM ? "footer" : "header";

        SortedSet<Integer> pages = parameters.getPageRange().getPages(documentHandler.getNumberOfPages());
        Integer labelPageNumber = parameters.getPageCountStartFrom();

        for (int pageNumber : pages) {
            // if user didn't override it, use document actual page numbering
            if(labelPageNumber == null) {
                labelPageNumber = pageNumber;
            }

            String batesSeq = null;
            if(parameters.getBatesSequence() != null) {
                batesSeq = parameters.getBatesSequence().next();
            }

            String label = new TextStampPattern().withPage(labelPageNumber, totalPages)
                                                 .withBatesSequence(batesSeq)
                                                 .withFileSequence(String.valueOf(currentFileCounter))
                                                 .build(parameters.getPattern());

            LOG.debug("Applying {} {} to document page {}", what, label, pageNumber);

            PDPage page = documentHandler.getPage(pageNumber);
            PDRectangle pageSize = page.findCropBox();

            try {
                float stringWidth = font.getStringWidth(label) * fontSize.floatValue() / 1000f;
                float xPosition = hAlign.position(pageSize.getWidth(), stringWidth, DEFAULT_MARGIN);
                float yPosition = vAlign.position(pageSize.getHeight(), DEFAULT_MARGIN);

                PDPageContentStream contentStream = new PDPageContentStream(documentHandler.getUnderlyingPDDocument(),
                        page, true, true);
                contentStream.beginText();
                contentStream.setFont(font, fontSize.floatValue());
                contentStream.moveTextPositionByAmount(xPosition, yPosition);
                contentStream.drawString(label);
                contentStream.endText();
                contentStream.close();
            } catch (IOException e) {
                throw new TaskIOException("An error occurred writing the header or footer of the page.", e);
            }

            labelPageNumber++;
        }
    }

    public void close() {
        IOUtils.closeQuietly(documentHandler);
    }
}
