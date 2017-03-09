/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com)
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox;

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryBuffer;
import static org.sejda.core.support.io.OutputWriters.newMultipleOutputWriter;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Set;

import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.image.AbstractPdfToMultipleImageParameters;
import org.sejda.model.task.TaskExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SAMBox implementation of a task which converts a pdf document to a collection of images, one image per page.
 * 
 * @param <T>
 *            the type of parameters.
 * @author Andrea Vacondio
 * 
 */
public class PdfToMultipleImageTask<T extends AbstractPdfToMultipleImageParameters> extends BasePdfToImageTask<T> {

    private static final Logger LOG = LoggerFactory.getLogger(PdfToMultipleImageTask.class);

    private MultipleOutputWriter outputWriter;
    private PdfSourceOpener<PDDocumentHandler> sourceOpener = new DefaultPdfSourceOpener();
    private PDDocumentHandler documentHandler = null;

    @Override
    public void before(T parameters, TaskExecutionContext executionContext) throws TaskException {
        super.before(parameters, executionContext);
        outputWriter = newMultipleOutputWriter(parameters.getExistingOutputPolicy(), executionContext);
    }

    @Override
    public void execute(T parameters) throws TaskException {
        int currentStep = 0;
        int currentFileNumber = 0;
        int totalSteps = parameters.getSourceList().size();

        for (PdfSource<?> source : parameters.getSourceList()) {
            executionContext().assertTaskNotCancelled();
            currentStep++;
            try {
                LOG.debug("Opening {}", source);
                documentHandler = source.open(sourceOpener);

                Set<Integer> requestedPages = parameters.getPages(documentHandler.getNumberOfPages());
                if (!requestedPages.isEmpty()) {
                    LOG.trace("Found {} pages to convert", totalSteps);

                    for (int currentPage : requestedPages) {

                        File tmpFile = createTemporaryBuffer();
                        LOG.debug("Created output temporary buffer {} ", tmpFile);

                        try {
                            LOG.trace("Converting page {}", currentPage);
                            BufferedImage pageImage = documentHandler.renderImage(currentPage,
                                    parameters.getResolutionInDpi(), parameters.getOutputImageColorType());

                            getWriter().openDestination(tmpFile, parameters);
                            getWriter().write(pageImage, parameters);
                            getWriter().closeDestination();

                            String outName = nameGenerator(parameters.getOutputPrefix()).generate(
                                    nameRequest(parameters.getOutputImageType().getExtension()).page(currentPage)
                                            .originalName(source.getName()).fileNumber(currentFileNumber));
                            outputWriter.addOutput(file(tmpFile).name(outName));
                        } catch (TaskException e) {
                            executionContext().assertTaskIsLenient(e);
                            notifyEvent(executionContext().notifiableTaskMetadata()).taskWarning(
                                    String.format("Page %d was skipped, could not be converted", currentPage), e);
                        }
                    }
                } else {
                    throw new TaskException("No pages converted");
                }
            } finally {
                nullSafeCloseQuietly(documentHandler);
            }
            notifyEvent(executionContext().notifiableTaskMetadata()).stepsCompleted(currentStep).outOf(totalSteps);
        }

        parameters.getOutput().accept(outputWriter);
        LOG.debug("Documents converted to {} and saved to {}", parameters.getOutputImageType(), parameters.getOutput());
    }

    @Override
    public void after() {
        super.after();
        nullSafeCloseQuietly(documentHandler);
    }
}
