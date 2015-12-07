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
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Set;

import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.writer.context.ImageWriterContext;
import org.sejda.core.writer.model.ImageWriter;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.image.PdfToJpegParameters;
import org.sejda.model.task.BaseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PdfToMultipleImageTask extends BaseTask<PdfToJpegParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(PdfToMultipleImageTask.class);

    private MultipleOutputWriter outputWriter;
    private PdfSourceOpener<PDDocumentHandler> sourceOpener = new DefaultPdfSourceOpener();
    private PDDocumentHandler documentHandler = null;
    private ImageWriter<PdfToJpegParameters> writer;

    @Override
    public void before(PdfToJpegParameters parameters) throws TaskException {
        outputWriter = OutputWriters.newMultipleOutputWriter(parameters.getExistingOutputPolicy());
        writer = ImageWriterContext.getContext().getImageWriterFactory().createImageWriter(parameters);
        if (writer == null) {
            LOG.info("Unable to create an ImageWriter using the provided factory, falling back on default factory.");
            writer = ImageWriterContext.getContext().getDefaultImageWriterFactory().createImageWriter(parameters);
        }
        if (writer == null) {
            throw new TaskExecutionException(String.format("No suitable ImageWriter found for %s.", parameters));
        }
        LOG.trace("Found image writer {}", writer);
    }

    @Override
    public void execute(PdfToJpegParameters parameters) throws TaskException {
        documentHandler = parameters.getSource().open(sourceOpener);

        Set<Integer> requestedPages = parameters.getPages(documentHandler.getNumberOfPages());
        if (requestedPages == null || requestedPages.isEmpty()) {
            throw new TaskExecutionException("No page has been selected for conversion.");
        }

        int currentStep = 0;
        int totalSteps = requestedPages.size();
        LOG.trace("Found {} pages to convert", totalSteps);

        for (int currentPage : requestedPages) {
            currentStep++;

            File tmpFile = createTemporaryBuffer();
            LOG.debug("Created output temporary buffer {} ", tmpFile);
            LOG.trace("Writing page {}", currentPage);

            BufferedImage pageImage = documentHandler.renderImage(currentPage, parameters.getResolutionInDpi());

            writer.openWriteDestination(tmpFile, parameters);
            LOG.trace("Writing page {}", currentPage);
            writer.write(pageImage, parameters);
            writer.closeDestination();

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
        nullSafeCloseQuietly(documentHandler);
        nullSafeCloseQuietly(writer);
    }
}
