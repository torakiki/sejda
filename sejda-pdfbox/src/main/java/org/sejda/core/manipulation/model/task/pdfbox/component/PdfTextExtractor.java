/*
 * Created on 24/ago/2011
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
package org.sejda.core.manipulation.model.task.pdfbox.component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.sejda.core.exception.TaskException;
import org.sejda.core.exception.TaskExecutionException;

/**
 * Component responsible for extracting text from an input pdf document.
 * 
 * @author Andrea Vacondio
 * 
 */
public class PdfTextExtractor {

    private PDFTextStripper textStripper = null;
    private Writer outputWriter;

    public PdfTextExtractor(String encoding) throws TaskException {
        try {
            textStripper = new PDFTextStripper(encoding);
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
            outputWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
            textStripper.writeText(document, outputWriter);
        } catch (IOException e) {
            throw new TaskExecutionException("An error occurred extracting text from a pdf source.", e);
        }
    }

    private void close() {
        IOUtils.closeQuietly(outputWriter);
    }

    public static void nullSafeClose(PdfTextExtractor extractor) {
        if (extractor != null) {
            extractor.close();
        }
    }
}
