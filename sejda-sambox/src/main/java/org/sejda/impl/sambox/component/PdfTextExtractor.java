/*
 * Created on 24/ago/2011
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

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.text.PDFTextStripper;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskExecutionException;

/**
 * Component responsible for extracting text from an input pdf document.
 * 
 * @author Andrea Vacondio
 * 
 */
public class PdfTextExtractor implements Closeable {

    private PDFTextStripper textStripper = null;
    private Writer outputWriter;
    private String encoding;

    public PdfTextExtractor(String encoding) throws TaskException {
        this(encoding, 1, Integer.MAX_VALUE);
    }

    public PdfTextExtractor(String encoding, int startPageOneBased, int endPageIncluding) throws TaskException {
        try {
            this.encoding = encoding;
            textStripper = new PDFTextStripper();
            textStripper.setStartPage(startPageOneBased);
            textStripper.setEndPage(endPageIncluding);
        } catch (IOException e) {
            throw new TaskException("Unable to create text extractor.", e);
        }
    }

    /**
     * Extract text from the input document writing it to the given output file.
     * 
     * @param document
     * @param output
     * @throws TaskException
     */
    public void extract(PDDocument document, File output) throws TaskException {
        if (document == null) {
            throw new TaskException("Unable to extract text from a null document.");
        }
        if (output == null || !output.isFile() || !output.canWrite()) {
            throw new TaskException(String.format("Cannot write extracted text to a the given output file '%s'.",
                    output));
        }
        try {
            outputWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), encoding));
            textStripper.writeText(document, outputWriter);
        } catch (IOException e) {
            throw new TaskExecutionException("An error occurred extracting text from a pdf source.", e);
        }
    }

    @Override
    public void close() {
        IOUtils.closeQuietly(outputWriter);
    }
}
