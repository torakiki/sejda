/*
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.impl.sambox;

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

import java.awt.Point;
import java.io.File;

import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.impl.sambox.component.PageImageWriter;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.input.Source;
import org.sejda.model.parameter.image.JpegToPdfParameters;
import org.sejda.model.task.BaseTask;
import org.sejda.model.task.TaskExecutionContext;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.graphics.image.PDImageXObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SAMBox implementation of a task that converts a list of jpg sources into a PDF file
 * 
 */
public class JpegToPdfTask extends BaseTask<JpegToPdfParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(JpegToPdfTask.class);

    private int totalSteps;
    private PDDocumentHandler documentHandler = null;
    private MultipleOutputWriter outputWriter;

    @Override
    public void before(JpegToPdfParameters parameters, TaskExecutionContext executionContext) throws TaskException {
        super.before(parameters, executionContext);
        totalSteps = parameters.getSourceList().size();
        outputWriter = OutputWriters.newMultipleOutputWriter(parameters.getExistingOutputPolicy(), executionContext);
    }

    @Override
    public void execute(JpegToPdfParameters parameters) throws TaskException {
        int currentStep = 0;

        documentHandler = new PDDocumentHandler();
        documentHandler.setCreatorOnPDDocument();

        PageImageWriter imageWriter = new PageImageWriter(documentHandler.getUnderlyingPDDocument());

        for (Source<?> source : parameters.getSourceList()) {
            executionContext().assertTaskNotCancelled();

            currentStep++;

            try {
                PDImageXObject image = PageImageWriter.toPDXImageObject(source);
                PDRectangle mediaBox = PDRectangle.A4;

                if (image.getWidth() > image.getHeight() && image.getWidth() > mediaBox.getWidth()) {
                    mediaBox = new PDRectangle(mediaBox.getHeight(), mediaBox.getWidth());
                }

                PDPage page = documentHandler.addBlankPage(mediaBox);

                // full page (scaled down only)
                int width = image.getWidth();
                int height = image.getHeight();

                if (width > mediaBox.getWidth()) {
                    int targetWidth = (int) mediaBox.getWidth();
                    LOG.debug("Scaling image down to fit by width {} vs {}", width, targetWidth);

                    float ratio = (float) width / targetWidth;
                    width = targetWidth;
                    height = Math.round(height / ratio);
                }

                if (height > mediaBox.getHeight()) {
                    int targetHeight = (int) mediaBox.getHeight();
                    LOG.debug("Scaling image down to fit by height {} vs {}", height, targetHeight);

                    float ratio = (float) height / targetHeight;
                    height = targetHeight;
                    width = Math.round(width / ratio);
                }

                // centered on page
                int x = ((int) mediaBox.getWidth() - width) / 2;
                int y = ((int) mediaBox.getHeight() - height) / 2;

                imageWriter.append(page, image, new Point(x, y), width, height, null, 0);

                notifyEvent(executionContext().notifiableTaskMetadata()).stepsCompleted(currentStep).outOf(totalSteps);
            } catch (TaskIOException e) {
                executionContext().assertTaskIsLenient(e);
                notifyEvent(executionContext().notifiableTaskMetadata()).taskWarning(
                        String.format("Image %s was skipped, could not be processed", source.getName()), e);
            }
        }

        File tmpFile = createTemporaryBuffer(parameters.getOutput());
        LOG.debug("Created output on temporary buffer {}", tmpFile);

        documentHandler.setVersionOnPDDocument(parameters.getVersion());
        documentHandler.setCompress(parameters.isCompress());
        documentHandler.savePDDocument(tmpFile);

        String outName = nameGenerator(parameters.getOutputPrefix()).generate(nameRequest());
        outputWriter.addOutput(file(tmpFile).name(outName));

        nullSafeCloseQuietly(documentHandler);

        parameters.getOutput().accept(outputWriter);
        LOG.debug("Input images written to {}", parameters.getOutput());
    }

    @Override
    public void after() {
        nullSafeCloseQuietly(documentHandler);
    }

}
