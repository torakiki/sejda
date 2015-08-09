/*
 * Created on Aug 1, 2015
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com).
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

    public void before(PdfToJpegParameters parameters) throws TaskException {
        outputWriter = OutputWriters.newMultipleOutputWriter(parameters.isOverwrite());
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

    public void after() {
        nullSafeCloseQuietly(documentHandler);
        nullSafeCloseQuietly(writer);
    }
}
