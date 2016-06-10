/*
 * Created on 26/set/2011
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
package org.sejda.impl.icepdf;

import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryBuffer;
import static org.sejda.core.support.io.OutputWriters.newMultipleOutputWriter;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;
import static org.sejda.impl.icepdf.component.PdfToBufferedImageProvider.toBufferedImage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Set;

import org.icepdf.core.pobjects.Document;
import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.impl.icepdf.component.DefaultPdfSourceOpener;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.image.AbstractPdfToMultipleImageParameters;
import org.sejda.model.task.TaskExecutionContext;
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

    private static final Logger LOG = LoggerFactory.getLogger(PdfToMultipleImageTask.class);

    private MultipleOutputWriter outputWriter;
    private PdfSourceOpener<Document> sourceOpener = new DefaultPdfSourceOpener();
    private Document pdfDocument = null;

    @Override
    public void before(T parameters, TaskExecutionContext executionContext) throws TaskException {
        super.before(parameters, executionContext);
        outputWriter = newMultipleOutputWriter(parameters.getExistingOutputPolicy(), executionContext);
    }

    @Override
    public void execute(T parameters) throws TaskException {
        pdfDocument = parameters.getSource().open(sourceOpener);

        Set<Integer> requestedPages = parameters.getPages(pdfDocument.getNumberOfPages());
        if (requestedPages == null || requestedPages.isEmpty()) {
            throw new TaskExecutionException("No page has been selected for conversion.");
        }

        int currentStep = 0;
        int totalSteps = requestedPages.size();
        LOG.trace("Found {} pages to convert", totalSteps);

        for (int currentPage : requestedPages) {
            currentStep++;
            executionContext().assertTaskNotCancelled();

            BufferedImage pageImage = toBufferedImage(pdfDocument, zeroBased(currentPage), parameters);
            if (pageImage == null) {
                LOG.debug("Failed to convert page {} to image", currentPage);
                continue;
            }

            File tmpFile = createTemporaryBuffer();
            LOG.debug("Created output temporary buffer {} ", tmpFile);

            getWriter().openWriteDestination(tmpFile, parameters);
            LOG.trace("Writing page {}", currentPage);
            getWriter().write(pageImage, parameters);
            getWriter().closeDestination();

            String outName = nameGenerator(parameters.getOutputPrefix())
                    .generate(nameRequest(parameters.getOutputImageType().getExtension()).page(currentPage)
                            .originalName(parameters.getSource().getName()).fileNumber(currentStep));
            outputWriter.addOutput(file(tmpFile).name(outName));

            notifyEvent(executionContext().notifiableTaskMetadata()).stepsCompleted(currentStep).outOf(totalSteps);
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
