/*
 * Created on 12/nov/2012
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
