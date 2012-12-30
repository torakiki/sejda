/*
 * Created on 13/nov/2012
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.sejda.impl.pdfbox.component;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.sejda.impl.pdfbox.util.FontUtils.getStandardType1Font;

import java.io.Closeable;
import java.io.IOException;
import java.math.BigDecimal;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component providing footer related functionalities.
 * 
 * @author Andrea Vacondio
 * 
 */
public class PdfHeaderFooterWriter implements Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(PdfHeaderFooterWriter.class);

    // TODO define as a params member
    private static final Float DEFAULT_MARGIN = 30F;

    private PDDocumentHandler documentHandler;

    /**
     * @param documentHandler
     *            the document handler holding the document where we want to write the footer
     */
    public PdfHeaderFooterWriter(PDDocumentHandler documentHandler) {
        this.documentHandler = documentHandler;
    }

    public void writeFooter(SetHeaderFooterParameters parameters) throws TaskIOException {
        PDFont font = defaultIfNull(getStandardType1Font(parameters.getFont()), PDType1Font.HELVETICA);
        BigDecimal fontSize = defaultIfNull(parameters.getFontSize(), BigDecimal.TEN);
        HorizontalAlign horAlignment = defaultIfNull(parameters.getHorizontalAlign(), HorizontalAlign.CENTER);
        VerticalAlign verAlignment = defaultIfNull(parameters.getVerticalAlign(), VerticalAlign.BOTTOM);
        SortedSet<Integer> pages = parameters.getPageRange().getPages(documentHandler.getNumberOfPages());
        LOG.debug("Found {} pages to apply header or footer", pages.size());
        Integer labelPageNumber = parameters.getNumbering().getLogicalPageNumber();
        for (Integer pageNumber : pages) {
            String label = parameters.styledLabelFor(labelPageNumber);
            PDPage page = documentHandler.getPage(pageNumber);
            PDRectangle pageSize = page.findCropBox();
            try {
                float stringWidth = font.getStringWidth(label) * fontSize.floatValue() / 1000f;
                float xPosition = horAlignment.position(pageSize.getWidth(), stringWidth, DEFAULT_MARGIN);
                float yPosition = verAlignment.position(pageSize.getHeight(), DEFAULT_MARGIN);
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
