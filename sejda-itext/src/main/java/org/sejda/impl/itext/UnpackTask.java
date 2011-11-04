/*
 * Created on 22/ago/2011
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
package org.sejda.impl.itext;

import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.impl.itext.util.ITextUtils.nullSafeClosePdfReader;

import org.sejda.impl.itext.component.PdfUnpacker;
import org.sejda.impl.itext.component.input.PdfSourceOpeners;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.AbstractPdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.UnpackParameters;
import org.sejda.model.task.BaseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.pdf.PdfReader;

/**
 * iText implementation of a task that unpacks files attached to a collection of input documents.
 * 
 * @author Andrea Vacondio
 * 
 */
public class UnpackTask extends BaseTask<UnpackParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(UnpackTask.class);

    private PdfReader reader = null;
    private PdfUnpacker unpacker;
    private PdfSourceOpener<PdfReader> sourceOpener;
    private int totalSteps;

    public void before(UnpackParameters parameters) {
        unpacker = new PdfUnpacker();
        sourceOpener = PdfSourceOpeners.newPartialReadOpener();
        totalSteps = parameters.getSourceList().size();
    }

    public void execute(UnpackParameters parameters) throws TaskException {
        int currentStep = 0;

        for (AbstractPdfSource source : parameters.getSourceList()) {
            LOG.debug("Opening {} ", source);
            reader = source.open(sourceOpener);

            unpacker.unpack(reader);

            nullSafeClosePdfReader(reader);
            notifyEvent(getNotifiableTaskMetadata()).stepsCompleted(++currentStep).outOf(totalSteps);
        }

        unpacker.flushOutputs(parameters.getOutput(), parameters.isOverwrite());
        LOG.debug("Attachments unpacked and written to {}", parameters.getOutput());
    }

    public void after() {
        nullSafeClosePdfReader(reader);
    }

}
