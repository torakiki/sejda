/*
 * Created on 30/mag/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.impl.itext;

import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;
import static org.sejda.core.support.util.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.impl.itext.component.PdfRotator.applyRotation;
import static org.sejda.impl.itext.util.ITextUtils.nullSafeClosePdfReader;

import java.io.File;

import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.model.input.PdfSource;
import org.sejda.core.manipulation.model.input.PdfSourceOpener;
import org.sejda.core.manipulation.model.parameter.RotateParameters;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.support.io.MultipleOutputWriterSupport;
import org.sejda.impl.itext.component.PdfStamperHandler;
import org.sejda.impl.itext.component.input.PdfSourceOpeners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.pdf.PdfReader;

/**
 * Task performing pages rotation on a list of {@link PdfSource}.
 * 
 * @author Andrea Vacondio
 * 
 */
public class RotateTask implements Task<RotateParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(RotateTask.class);

    private PdfReader reader = null;
    private PdfStamperHandler stamperHandler = null;
    private int totalSteps;
    private MultipleOutputWriterSupport outputWriter;
    private PdfSourceOpener<PdfReader> sourceOpener;

    public void before(RotateParameters parameters) {
        outputWriter = new MultipleOutputWriterSupport();
        totalSteps = parameters.getSourceList().size();
        sourceOpener = PdfSourceOpeners.newFullReadOpener();
    }

    public void execute(RotateParameters parameters) throws TaskException {
        int currentStep = 0;

        for (PdfSource source : parameters.getSourceList()) {
            LOG.debug("Opening {} ", source);
            reader = source.open(sourceOpener);

            applyRotation(parameters.getRotation()).to(reader);

            File tmpFile = outputWriter.createTemporaryPdfBuffer();
            LOG.debug("Created output temporary buffer {} ", tmpFile);
            stamperHandler = new PdfStamperHandler(reader, tmpFile, parameters.getVersion());

            stamperHandler.setCompressionOnStamper(parameters.isCompressXref());
            stamperHandler.setCreatorOnStamper(reader);

            nullSafeClosePdfReader(reader);
            nullSafeCloseQuietly(stamperHandler);

            String outName = nameGenerator(parameters.getOutputPrefix()).generate(
                    nameRequest().originalName(source.getName()));
            outputWriter.addOutput(file(tmpFile).name(outName));

            notifyEvent().stepsCompleted(++currentStep).outOf(totalSteps);
        }

        outputWriter.flushOutputs(parameters.getOutput(), parameters.isOverwrite());
        LOG.debug("Input documents rotated and written to {}", parameters.getOutput());
    }

    public void after() {
        nullSafeClosePdfReader(reader);
        nullSafeCloseQuietly(stamperHandler);
    }

}
