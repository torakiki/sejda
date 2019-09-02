/*
 * Created on 07 mar 2016
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

import java.awt.Color;
import java.io.IOException;

import org.sejda.impl.sambox.util.FontUtils;
import org.sejda.model.HorizontalAlign;
import org.sejda.model.VerticalAlign;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.font.PDFont;
import org.sejda.sambox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component that writes the given name as left footer of the given page
 *
 * @author Andrea Vacondio
 */
public class FilenameFooterWriter {
    private static final Logger LOG = LoggerFactory.getLogger(FilenameFooterWriter.class);

    private boolean addFooter = false;
    private PageTextWriter writer;
    private PDDocument document;

    private static PDFont FONT = PDType1Font.HELVETICA;
    private static double FONT_SIZE = 10;

    public FilenameFooterWriter(boolean addFooter, PDDocument document) {
        this.writer = new PageTextWriter(document);
        this.document = document;
        this.addFooter = addFooter;
    }

    public void addFooter(PDPage page, String fileName, long pageNumber) throws TaskException {
        if (addFooter) {
            try {
                String truncatedFilename = truncateIfRequired(fileName, maxWidth(page, pageNumber));
                writer.write(page, HorizontalAlign.LEFT, VerticalAlign.BOTTOM, truncatedFilename, FONT, FONT_SIZE,
                        Color.BLACK);
                writer.write(page, HorizontalAlign.RIGHT, VerticalAlign.BOTTOM, Long.toString(pageNumber),
                        FONT, FONT_SIZE, Color.BLACK);
            } catch (TaskIOException | IOException e) {
                throw new TaskException("Unable to write the page footer", e);
            }
        }
    }

    private double maxWidth(PDPage page, long pageNumber) throws IOException {
        PDRectangle pageSize = page.getMediaBox().rotate(page.getRotation());
        return pageSize.getWidth() - 2 * PageTextWriter.DEFAULT_MARGIN - FontUtils.getSimpleStringWidth(Long.toString(pageNumber), FONT, FONT_SIZE);
    }

    private double stringWidth(String text) throws TaskIOException {
        return writer.getStringWidth(text, FONT, (float) FONT_SIZE);
    }

    private String truncateIfRequired(String original, double maxWidth) throws TaskIOException {
        // check if all characters are supported by the fonts available
        // replace any bad characters with #
        // TODO FIXME? it doesn't replace every single char with a #, it replaces every contiguous text with # i.e. "հայերէն" -> "#" and not "#######"
        String text = FontUtils.replaceUnsupportedCharacters(original, document, "#");

        if (stringWidth(text) <= maxWidth) {
            return text;
        }

        LOG.debug("Page filename footer needs truncating to fit available space");

        int currentLength = text.length() / 2;
        while (stringWidth(text.substring(0, currentLength)) > maxWidth) {
            currentLength /= 2;
        }

        int currentChunk = currentLength;
        while (currentChunk > 1) {
            currentChunk /= 2;
            if (stringWidth(text.substring(0, currentLength + currentChunk)) < maxWidth) {
                currentLength += currentChunk;
            }
        }
        return text.substring(0, currentLength);
    }
}
