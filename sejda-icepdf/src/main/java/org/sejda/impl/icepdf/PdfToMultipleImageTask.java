/*
 * Created on 26/set/2011
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
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;
import static org.sejda.impl.icepdf.component.PdfToBufferedImageProvider.toBufferedImage;

import java.io.File;

import org.icepdf.core.pobjects.Document;
import org.sejda.core.Sejda;
import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.model.input.PdfSourceOpener;
import org.sejda.core.manipulation.model.parameter.image.AbstractPdfToMultipleImageParameters;
import org.sejda.core.support.io.MultipleOutputWriterSupport;
import org.sejda.impl.icepdf.component.DefaultPdfSourceOpener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ICEpdf implementation of a task which converts a pdf document to a collection of images, one image per page.
 * 
 * @param <T>
 *            the type of parameters.
 * @author Andrea Vacondio
 * 
 */
public class PdfToMultipleImageTask<T extends AbstractPdfToMultipleImageParameters> extends BasePdfToImageTask<T> {

    private static final Logger LOG = LoggerFactory.getLogger(PdfToSingleImageTask.class);

    private MultipleOutputWriterSupport outputWriter = new MultipleOutputWriterSupport();;
    private PdfSourceOpener<Document> sourceOpener = new DefaultPdfSourceOpener();
    private Document pdfDocument = null;

    public void execute(T parameters) throws TaskException {
        pdfDocument = parameters.getSource().open(sourceOpener);

        int numberOfPages = pdfDocument.getNumberOfPages();
        LOG.trace("Found {} pages", numberOfPages);

        for (int zeroBasedPageNumber = 0; zeroBasedPageNumber < pdfDocument.getNumberOfPages(); zeroBasedPageNumber++) {
            File tmpFile = outputWriter.createTemporaryBuffer();
            LOG.debug("Created output temporary buffer {} ", tmpFile);

            getWriter().openWriteDestination(tmpFile, parameters);
            LOG.trace("Writing page {}", zeroBasedPageNumber + 1);
            getWriter().write(toBufferedImage(pdfDocument, zeroBasedPageNumber, parameters), parameters);
            getWriter().closeDestination();

            String outName = nameGenerator(parameters.getOutputPrefix()).generate(
                    nameRequest(Sejda.TIF_EXTENSION).page(zeroBasedPageNumber)
                            .originalName(parameters.getSource().getName()).fileNumber(zeroBasedPageNumber + 1));
            outputWriter.addOutput(file(tmpFile).name(outName));

            notifyEvent().stepsCompleted(zeroBasedPageNumber + 1).outOf(numberOfPages);
        }

        outputWriter.flushOutputs(parameters.getOutput(), parameters.isOverwrite());
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
