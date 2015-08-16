/*
 * Created on 12/nov/2012
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

import java.awt.Rectangle;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.util.PDFTextStripperByArea;
import org.sejda.model.exception.TaskIOException;

/**
 * Stateless component responsible for extracting text from a given area of a document page
 * 
 * @author Andrea Vacondio
 * 
 */
public class PdfTextExtractorByArea {

    private static final int GUESSTIMATE_HEADER_FOOTER_HEIGHT = 50;

    /**
     * @param page
     * @return the extracted text from the footer of the document, assuming a footer height of 50
     * @throws TaskIOException
     */
    public String extractFooterText(PDPage page) throws TaskIOException {
        return extractTextFromArea(page, getFooterAreaRectangle(page));
    }

    public String extractHeaderText(PDPage page) throws TaskIOException {
        return extractTextFromArea(page, getHeaderAreaRectangle(page));
    }

    private Rectangle getFooterAreaRectangle(PDPage page) {
        PDRectangle pageSize = page.findCropBox();
        int pageHeight = (int) pageSize.getHeight();
        int pageWidth = (int) pageSize.getWidth();
        return new Rectangle(0, pageHeight - GUESSTIMATE_HEADER_FOOTER_HEIGHT, pageWidth, GUESSTIMATE_HEADER_FOOTER_HEIGHT);
    }

    private Rectangle getHeaderAreaRectangle(PDPage page) {
        PDRectangle pageSize = page.findCropBox();
        int pageWidth = (int) pageSize.getWidth();
        return new Rectangle(0, 0, pageWidth, GUESSTIMATE_HEADER_FOOTER_HEIGHT);
    }

    /**
     * Extracts the text found in a specific page bound to a specific rectangle area Eg: extract footer text from a certain page
     * 
     * @param page
     *            the page to extract the text from
     * @param area
     *            the rectangular area to extract
     * @return the extracted text
     * @throws TaskIOException
     */
    public String extractTextFromArea(PDPage page, Rectangle area) throws TaskIOException {
        try {
            PDFTextStripperByArea stripper = new PDFTextStripperByArea("UTF8");

            stripper.setSortByPosition(true);
            stripper.addRegion("area1", area);
            stripper.extractRegions(page);

            return stripper.getTextForRegion("area1");
        } catch (IOException e) {
            throw new TaskIOException("An error occurred extracting text from page.", e);
        }
    }
}
