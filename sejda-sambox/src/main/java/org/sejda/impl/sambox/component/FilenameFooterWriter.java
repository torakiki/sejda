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

import org.sejda.model.HorizontalAlign;
import org.sejda.model.VerticalAlign;
import org.sejda.model.exception.TaskIOException;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component that writes the given name as left footer of the given page
 * 
 * @author Andrea Vacondio
 *
 */
public class FilenameFooterWriter {
    private static final Logger LOG = LoggerFactory.getLogger(FilenameFooterWriter.class);

    private boolean addFooter = false;
    private PageTextWriter writer;

    public FilenameFooterWriter(boolean addFooter, PDDocument document) {
        this.writer = new PageTextWriter(document);
        this.addFooter = addFooter;
    }

    public void addFooter(PDPage page, String fileName, long pageNumber) {
        if (addFooter) {
            try {
                writer.write(page, HorizontalAlign.LEFT, VerticalAlign.BOTTOM, fileName, PDType1Font.HELVETICA, 10d,
                        Color.BLACK);
                writer.write(page, HorizontalAlign.RIGHT, VerticalAlign.BOTTOM, Long.toString(pageNumber),
                        PDType1Font.HELVETICA, 10d, Color.BLACK);
            } catch (TaskIOException e) {
                LOG.warn("Unable to write the page footer", e);
            }
        }
    }
}
