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

import java.io.File;

import org.apache.commons.lang3.mutable.MutableInt;
import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.impl.sambox.component.image.ImagesToPdfDocumentConverter;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.input.Source;
import org.sejda.model.parameter.image.JpegToPdfParameters;
import org.sejda.model.task.BaseTask;
import org.sejda.model.task.TaskExecutionContext;
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
        final MutableInt currentStep = new MutableInt(0);

        ImagesToPdfDocumentConverter converter = new ImagesToPdfDocumentConverter() {
            @Override
            public void beforeImage(Source<?> source) throws TaskException {
                executionContext().assertTaskNotCancelled();
                currentStep.increment();
            }

            @Override
            public void afterImage(PDImageXObject image) {
                notifyEvent(executionContext().notifiableTaskMetadata()).stepsCompleted(currentStep.getValue()).outOf(totalSteps);
            }

            @Override
            public void failedImage(Source<?> source, TaskIOException e) throws TaskException{
                executionContext().assertTaskIsLenient(e);
                notifyEvent(executionContext().notifiableTaskMetadata()).taskWarning(
                        String.format("Image %s was skipped, could not be processed", source.getName()), e);
            }
        };

        documentHandler = converter.convert(parameters.getSourceList());

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
