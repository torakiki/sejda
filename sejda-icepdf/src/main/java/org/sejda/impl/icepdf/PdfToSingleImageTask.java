/*
 * Created on 16/set/2011
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
package org.sejda.impl.icepdf;

import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.util.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.impl.icepdf.component.PdfToBufferedImageProvider.toBufferedImage;

import java.io.File;

import org.icepdf.core.pobjects.Document;
import org.sejda.core.exception.TaskException;
import org.sejda.core.exception.TaskExecutionException;
import org.sejda.core.manipulation.model.input.PdfSourceOpener;
import org.sejda.core.manipulation.model.parameter.image.AbstractPdfToSingleImageParameters;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.support.io.SingleOutputWriterSupport;
import org.sejda.core.writer.model.ImageWriter;
import org.sejda.core.writer.model.ImageWriterContext;
import org.sejda.impl.icepdf.component.DefaultPdfSourceOpener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ICEpdf implamentation of a task which converts a pdf document to an image format that supports multiple images into a single image.
 * 
 * @param <T>
 *            the type of parameters.
 * @author Andrea Vacondio
 * 
 */
public class PdfToSingleImageTask<T extends AbstractPdfToSingleImageParameters> implements Task<T> {

    private static final Logger LOG = LoggerFactory.getLogger(PdfToSingleImageTask.class);

    private Document pdfDocument = null;
    private SingleOutputWriterSupport outputWriter;
    private PdfSourceOpener<Document> sourceOpener = new DefaultPdfSourceOpener();
    private ImageWriter<T> writer;

    public void before(T parameters) throws TaskExecutionException {
        outputWriter = new SingleOutputWriterSupport();
        writer = ImageWriterContext.getContext().getImageWriterFactory().createImageWriter(parameters);
        if (writer == null) {
            LOG.info("Unable to create an ImageWriter using the provided factory, falling back on default factory.");
            writer = ImageWriterContext.getContext().getDefaultImageWriterFactory().createImageWriter(parameters);
        }
        if (writer == null || !writer.supportMultiImage()) {
            throw new TaskExecutionException("No suitable ImageWriter found.");
        }
        LOG.trace("Found image writer {}", writer);
    }

    public void execute(T parameters) throws TaskException {

        File tmpFile = outputWriter.createTemporaryBuffer();
        LOG.debug("Created output temporary buffer {} ", tmpFile);

        pdfDocument = parameters.getSource().open(sourceOpener);

        int numberOfPages = pdfDocument.getNumberOfPages();
        LOG.trace("Found {} pages", numberOfPages);

        writer.openWriteDestination(tmpFile, parameters);
        for (int zeroBasedPageNumber = 0; zeroBasedPageNumber < pdfDocument.getNumberOfPages(); zeroBasedPageNumber++) {
            LOG.trace("Writing page {}", zeroBasedPageNumber + 1);
            writer.write(toBufferedImage(pdfDocument, zeroBasedPageNumber, parameters), parameters);
            notifyEvent().stepsCompleted(zeroBasedPageNumber + 1).outOf(numberOfPages);
        }
        writer.closeDestination();

        outputWriter.flushSingleOutput(file(tmpFile).name(parameters.getOutputName()), parameters.getOutput(),
                parameters.isOverwrite());

        LOG.debug("Document converted to {} and saved to {}", parameters.getOutputImageType(), parameters.getOutput());
    }

    public void after() {
        nullSafeCloseQuietly(writer);
        if (pdfDocument != null) {
            pdfDocument.dispose();
        }
    }
}
