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
import static org.sejda.core.support.io.IOUtils.createTemporaryBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.impl.icepdf.component.PdfToBufferedImageProvider.toBufferedImage;

import java.io.File;

import org.icepdf.core.pobjects.Document;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.io.SingleOutputWriter;
import org.sejda.impl.icepdf.component.DefaultPdfSourceOpener;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.image.AbstractPdfToSingleImageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ICEpdf implementation of a task which converts a pdf document to an image format that supports multiple images into a single image.
 * 
 * @param <T>
 *            the type of parameters.
 * @author Andrea Vacondio
 * 
 */
public class PdfToSingleImageTask<T extends AbstractPdfToSingleImageParameters> extends BasePdfToImageTask<T> {

    private static final Logger LOG = LoggerFactory.getLogger(PdfToSingleImageTask.class);

    private SingleOutputWriter outputWriter;
    private PdfSourceOpener<Document> sourceOpener = new DefaultPdfSourceOpener();
    private Document pdfDocument = null;

    @Override
    public void before(T parameters) throws TaskExecutionException {
        super.before(parameters);
        if (!getWriter().supportMultiImage()) {
            throw new TaskExecutionException("Selected ImageWriter doesn't support multiple images in the same file");
        }
        outputWriter = OutputWriters.newSingleOutputWriter(parameters.isOverwrite());
    }

    public void execute(T parameters) throws TaskException {

        File tmpFile = createTemporaryBuffer();
        LOG.debug("Created output temporary buffer {} ", tmpFile);

        pdfDocument = parameters.getSource().open(sourceOpener);

        int numberOfPages = pdfDocument.getNumberOfPages();
        LOG.trace("Found {} pages", numberOfPages);

        getWriter().openWriteDestination(tmpFile, parameters);
        for (int zeroBasedPageNumber = 0; zeroBasedPageNumber < pdfDocument.getNumberOfPages(); zeroBasedPageNumber++) {
            LOG.trace("Writing page {}", zeroBasedPageNumber + 1);
            getWriter().write(toBufferedImage(pdfDocument, zeroBasedPageNumber, parameters), parameters);
            notifyEvent(getNotifiableTaskMetadata()).stepsCompleted(zeroBasedPageNumber + 1).outOf(numberOfPages);
        }
        getWriter().closeDestination();

        outputWriter.setOutput(file(tmpFile).name(parameters.getOutputName()));
        parameters.getOutput().accept(outputWriter);
        LOG.debug("Document converted to {} and saved to {}", parameters.getOutputImageType(), parameters.getOutput());
    }

    @Override
    public void after() {
        super.after();
        if (pdfDocument != null) {
            pdfDocument.dispose();
        }
    }
}
