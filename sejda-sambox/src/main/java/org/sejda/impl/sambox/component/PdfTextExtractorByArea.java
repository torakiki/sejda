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
package org.sejda.impl.sambox.component;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.sejda.model.exception.TaskIOException;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.text.PDFTextStripperByArea;

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

    public String extractAddedText(PDPage page, Point2D position) throws TaskIOException {
        return extractTextFromArea(page, getAddedTextAreaRectangle(page, position));
    }

    private Rectangle getAddedTextAreaRectangle(PDPage page, Point2D position) {
        PDRectangle pageSize = page.getCropBox().rotate(page.getRotation());
        int pageHeight = (int) pageSize.getHeight();
        int pageWidth = (int) pageSize.getWidth();
        int guesstimateTextHeight = 12;
        return new Rectangle((int) position.getX(), pageHeight - (int) position.getY(), pageWidth,
                guesstimateTextHeight);
    }

    private Rectangle getFooterAreaRectangle(PDPage page) {
        PDRectangle pageSize = page.getCropBox().rotate(page.getRotation());
        int pageHeight = (int) pageSize.getHeight();
        int pageWidth = (int) pageSize.getWidth();
        return new Rectangle(0, pageHeight - GUESSTIMATE_HEADER_FOOTER_HEIGHT, pageWidth,
                GUESSTIMATE_HEADER_FOOTER_HEIGHT);
    }

    private Rectangle getHeaderAreaRectangle(PDPage page) {
        PDRectangle pageSize = page.getCropBox().rotate(page.getRotation());
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
    public String extractTextFromArea(PDPage page, Rectangle2D area) throws TaskIOException {
        try {
            PDFTextStripperByArea stripper = new PDFTextStripperByArea();

            stripper.setSortByPosition(true);
            stripper.addRegion("area1", area);
            stripper.extractRegions(page);

            String result = stripper.getTextForRegion("area1");
            result = defaultIfBlank(result, "");
            result = StringUtils.strip(result);
            result = org.sejda.core.support.util.StringUtils.normalizeWhitespace(result).trim();
            return result;
        } catch (IOException e) {
            throw new TaskIOException("An error occurred extracting text from page.", e);
        }
    }

    public List<String> extractTextFromAreas(PDPage page, List<Rectangle> areas) throws TaskIOException {
        List<String> results = new ArrayList<>(areas.size());

        try {
            PDFTextStripperByArea stripper = new PDFTextStripperByArea();
            stripper.setSortByPosition(true);

            for (int i = 0; i < areas.size(); i++) {
                stripper.addRegion("area" + i, areas.get(i));
            }

            stripper.extractRegions(page);

            for (int i = 0; i < areas.size(); i++) {
                String text = stripper.getTextForRegion("area" + i);
                String result = defaultIfBlank(text, "");
                result = StringUtils.strip(result);
                result = org.sejda.core.support.util.StringUtils.normalizeWhitespace(result).trim();
                results.add(result);
            }

            return results;
        } catch (IOException e) {
            throw new TaskIOException("An error occurred extracting text from page.", e);
        }
    }
}
