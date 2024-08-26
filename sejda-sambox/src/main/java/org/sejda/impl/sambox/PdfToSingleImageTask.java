/*
 * Created on 27 gen 2017
 * Copyright 2015 Sober Lemur S.r.l. and Sejda BV.
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
package org.sejda.impl.sambox;

import org.sejda.core.support.io.SingleOutputWriter;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.image.AbstractPdfToSingleImageParameters;
import org.sejda.model.task.TaskExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Set;

import static org.sejda.commons.util.IOUtils.closeQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.model.util.IOUtils.createTemporaryBuffer;
import static org.sejda.core.support.io.OutputWriters.newSingleOutputWriter;

/**
 * SAMBox implementation of a task which converts a pdf document to an image format that supports multiple images into a single image.
 * 
 * @param <T>
 *            the type of parameters.
 * @author Andrea Vacondio
 * 
 */
public class PdfToSingleImageTask<T extends AbstractPdfToSingleImageParameters> extends BasePdfToImageTask<T> {

    private static final Logger LOG = LoggerFactory.getLogger(PdfToSingleImageTask.class);

    private SingleOutputWriter outputWriter;
    private PdfSourceOpener<PDDocumentHandler> sourceOpener = null;
    private PDDocumentHandler documentHandler = null;

    @Override
    public void before(T parameters, TaskExecutionContext executionContext) throws TaskException {
        super.before(parameters, executionContext);
        if (!getWriter().supportMultiImage()) {
            throw new TaskExecutionException("Selected ImageWriter doesn't support multiple images in the same file");
        }
        outputWriter = newSingleOutputWriter(parameters.getExistingOutputPolicy(), executionContext);
        sourceOpener = new DefaultPdfSourceOpener(executionContext);
    }

    @Override
    public void execute(T parameters) throws TaskException {

        File tmpFile = createTemporaryBuffer(parameters.getOutput());
        outputWriter.taskOutput(tmpFile);
        LOG.debug("Temporary output set to {}", tmpFile);

        LOG.debug("Opening {}", parameters.getSource());
        executionContext().notifiableTaskMetadata().setCurrentSource(parameters.getSource());
        documentHandler = parameters.getSource().open(sourceOpener);

        Set<Integer> requestedPages = parameters.getPages(documentHandler.getNumberOfPages());

        int numberOfPages = requestedPages.size();
        LOG.trace("Found {} pages", numberOfPages);

        int currentStep = 0;
        int totalSteps = numberOfPages;

        getWriter().openDestination(tmpFile, parameters);
        for (int page : requestedPages) {
            currentStep++;
            LOG.trace("Converting page {}", page);
            try {
                BufferedImage pageImage = documentHandler.renderImage(page, parameters.getResolutionInDpi(),
                        parameters.getOutputImageColorType());
                getWriter().write(pageImage, parameters);
            } catch (TaskException e) {
                executionContext().assertTaskIsLenient(e);
                notifyEvent(executionContext().notifiableTaskMetadata())
                        .taskWarning(String.format("Page %d was skipped, could not be converted", page), e);
            }

            notifyEvent(executionContext().notifiableTaskMetadata()).stepsCompleted(currentStep).outOf(totalSteps);
        }
        getWriter().closeDestination();
        executionContext().notifiableTaskMetadata().clearCurrentSource();

        parameters.getOutput().accept(outputWriter);
        LOG.debug("Document converted to {} and saved to {}", parameters.getOutputImageType(), parameters.getOutput());
    }

    @Override
    public void after() {
        super.after();
        closeQuietly(documentHandler);
    }

}
