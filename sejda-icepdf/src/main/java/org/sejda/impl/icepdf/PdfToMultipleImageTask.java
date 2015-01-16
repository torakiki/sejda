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
import static org.sejda.core.support.io.IOUtils.createTemporaryBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;
import static org.sejda.impl.icepdf.component.PdfToBufferedImageProvider.toBufferedImage;

import java.io.File;
import java.util.Set;

import org.icepdf.core.pobjects.Document;
import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.impl.icepdf.component.DefaultPdfSourceOpener;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.image.AbstractPdfToMultipleImageParameters;
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

    private MultipleOutputWriter outputWriter;
    private PdfSourceOpener<Document> sourceOpener = new DefaultPdfSourceOpener();
    private Document pdfDocument = null;

    @Override
    public void before(T parameters) throws TaskExecutionException {
        super.before(parameters);
        outputWriter = OutputWriters.newMultipleOutputWriter(parameters.isOverwrite());
    }

    public void execute(T parameters) throws TaskException {
        pdfDocument = parameters.getSource().open(sourceOpener);

        Set<Integer> requestedPages = parameters.getPages(pdfDocument.getNumberOfPages());
        int currentStep = 0;
        int totalSteps = requestedPages.size();
        LOG.trace("Found {} pages to convert", totalSteps);

        for (int currentPage : requestedPages) {
            currentStep++;

            File tmpFile = createTemporaryBuffer();
            LOG.debug("Created output temporary buffer {} ", tmpFile);

            getWriter().openWriteDestination(tmpFile, parameters);
            LOG.trace("Writing page {}", currentPage);
            getWriter().write(toBufferedImage(pdfDocument, zeroBased(currentPage), parameters), parameters);
            getWriter().closeDestination();

            String outName = nameGenerator(parameters.getOutputPrefix()).generate(
                    nameRequest(parameters.getOutputImageType().getExtension()).page(currentPage)
                            .originalName(parameters.getSource().getName()).fileNumber(currentStep));
            outputWriter.addOutput(file(tmpFile).name(outName));

            notifyEvent(getNotifiableTaskMetadata()).stepsCompleted(currentStep).outOf(totalSteps);
        }

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

    private int zeroBased(int oneBased) {
        return oneBased - 1;
    }
}
