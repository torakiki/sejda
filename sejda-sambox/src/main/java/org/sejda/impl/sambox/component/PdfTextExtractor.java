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

import static java.util.Objects.nonNull;

import java.awt.geom.Rectangle2D;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;

import org.apache.commons.io.IOUtils;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component responsible for extracting text from an input pdf document.
 * 
 * @author Andrea Vacondio
 * 
 */
public class PdfTextExtractor implements Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(PdfTextExtractor.class);

    private PdfTextExtractorByArea textStripper = null;
    private Writer outputWriter;

    public PdfTextExtractor(String encoding, File output) throws TaskException {
        textStripper = new PdfTextExtractorByArea();
        if (output == null || !output.isFile() || !output.canWrite()) {
            throw new TaskException(
                    String.format("Cannot write extracted text to a the given output file '%s'", output));
        }
        try {
            outputWriter = Files.newBufferedWriter(output.toPath(), Charset.forName(encoding));
        } catch (IOException e) {
            throw new TaskExecutionException("An error occurred creating a file writer", e);
        }
    }

    /**
     * Extract text from the input page writing it to the given output file.
     * 
     * @param page
     */
    public void extract(PDPage page) {
        if (nonNull(page) && page.hasContents()) {
            try {
                outputWriter.write(textStripper.extractTextFromArea(page,
                        new Rectangle2D.Float(0, 0, page.getCropBox().getWidth(), page.getCropBox().getHeight())));
                outputWriter.write(System.lineSeparator());
                outputWriter.flush();
            } catch (IOException | TaskIOException e) {
                LOG.warn("Skipping page, an error occurred extracting text.", e);
            }
        } else {
            LOG.warn("Skipping null or no content page");
        }
    }

    /**
     * Extract text from the input document writing it to the given output file.
     * 
     * @param document
     */
    public void extract(PDDocument document) {
        if (nonNull(document)) {
            document.getPages().forEach(this::extract);
        } else {
            LOG.warn("Unable to extract text from a null document.");
        }
    }

    @Override
    public void close() {
        IOUtils.closeQuietly(outputWriter);
    }

}
