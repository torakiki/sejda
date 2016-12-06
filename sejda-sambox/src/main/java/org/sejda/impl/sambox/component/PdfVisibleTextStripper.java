/*
 * Created on 03 dic 2016
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

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.sejda.model.exception.TaskIOException;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDStream;
import org.sejda.sambox.text.PDFTextStripper;
import org.sejda.sambox.text.TextPosition;

/**
 * A custom text stripper that extracts only visible text and unload the decoded page stream once used
 * 
 * @author Andrea Vacondio
 *
 */
public class PdfVisibleTextStripper extends PDFTextStripper implements Closeable {

    public PdfVisibleTextStripper(Writer outputWriter) throws IOException {
        setShouldSeparateByBeads(false);
        this.output = outputWriter;
    }

    @Override
    protected void processTextPosition(TextPosition text) {
        if (text.isVisible()) {
            super.processTextPosition(text);
        }
    }

    public void extract(PDPage page) throws TaskIOException {
        try {
            setSortByPosition(true);
            setStartPage(getCurrentPageNo());
            setEndPage(getCurrentPageNo());
            if (page.hasContents()) {
                processPage(page);
            }
        } catch (IOException e) {
            throw new TaskIOException("An error occurred extracting text from page.", e);
        }
    }

    @Override
    protected void endPage(PDPage page) {
        Iterator<PDStream> iter = page.getContentStreams();
        while (iter.hasNext()) {
            iter.next().getCOSObject().unDecode();
        }
    }

    @Override
    public void close() {
        IOUtils.closeQuietly(this.output);
    }

}
