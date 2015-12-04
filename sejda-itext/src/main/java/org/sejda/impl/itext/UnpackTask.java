/*
 * Created on 22/ago/2011
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
package org.sejda.impl.itext;

import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.impl.itext.util.ITextUtils.nullSafeClosePdfReader;

import org.sejda.impl.itext.component.PdfUnpacker;
import org.sejda.impl.itext.component.input.PdfSourceOpeners;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
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

    @Override
    public void before(UnpackParameters parameters) {
        unpacker = new PdfUnpacker(parameters.isOverwrite());
        sourceOpener = PdfSourceOpeners.newPartialReadOpener();
        totalSteps = parameters.getSourceList().size();
    }

    @Override
    public void execute(UnpackParameters parameters) throws TaskException {
        int currentStep = 0;

        for (PdfSource<?> source : parameters.getSourceList()) {
            stopTaskIfCancelled();
            LOG.debug("Opening {} ", source);
            reader = source.open(sourceOpener);

            unpacker.unpack(reader);

            nullSafeClosePdfReader(reader);
            notifyEvent(getNotifiableTaskMetadata()).stepsCompleted(++currentStep).outOf(totalSteps);
        }

        unpacker.write(parameters.getOutput());
        LOG.debug("Attachments unpacked and written to {}", parameters.getOutput());
    }

    @Override
    public void after() {
        nullSafeClosePdfReader(reader);
    }

}
