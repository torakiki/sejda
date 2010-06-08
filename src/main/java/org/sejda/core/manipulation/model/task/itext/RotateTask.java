/*
 * Created on 30/mag/2010
 * Copyright (C) 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.sejda.core.manipulation.model.task.itext;

import static org.sejda.core.manipulation.model.task.itext.component.PdfRotationHandler.applyRotation;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.OutputDestination.destination;
import static org.sejda.core.support.io.OutputHandler.createTemporaryPdfBuffer;
import static org.sejda.core.support.io.OutputHandler.write;

import java.io.File;

import org.apache.log4j.Logger;
import org.sejda.core.exception.TaskException;
import org.sejda.core.exception.TaskExecutionException;
import org.sejda.core.manipulation.model.Task;
import org.sejda.core.manipulation.model.input.PdfSource;
import org.sejda.core.manipulation.model.output.OutputType;
import org.sejda.core.manipulation.model.output.PdfFileOutput;
import org.sejda.core.manipulation.model.output.PdfStreamOutput;
import org.sejda.core.manipulation.model.parameter.RotationParameter;
import org.sejda.core.manipulation.model.task.itext.component.PdfReaderHandler;
import org.sejda.core.manipulation.model.task.itext.component.PdfStamperHandler;

import com.itextpdf.text.pdf.PdfReader;

/**
 * Task performing pages rotation
 * 
 * @author Andrea Vacondio
 * 
 */
public class RotateTask implements Task<RotationParameter> {

    private static final Logger LOG = Logger.getLogger(RotateTask.class.getPackage().getName());

    private PdfReader reader = null;
    private PdfStamperHandler stamperHandler = null;
    private PdfReaderHandler readerHandler = null;

    public void before(RotationParameter parameters) throws TaskExecutionException {
        readerHandler = new PdfReaderHandler();
    }

    public void execute(RotationParameter parameters) throws TaskException {
        int currentStep = 0;
        int totalSteps = parameters.getInputList().size();
        for (PdfSource source : parameters.getInputList()) {
            currentStep++;
            LOG.debug(String.format("Opening %s ...", source));
            reader = readerHandler.openReader(source, true);

            applyRotation(parameters.getRotation()).to(reader);

            File tmpFile = createTemporaryPdfBuffer();
            LOG.debug(String.format("Creating output on temporary buffer %s ...", tmpFile));
            stamperHandler = new PdfStamperHandler(reader, tmpFile, parameters.getVersion());

            stamperHandler.setCompressionOnStamper(parameters.isCompress());
            stamperHandler.setCreatorOnStamper(reader);

            readerHandler.closePdfReader(reader);
            stamperHandler.closePdfStamper();
            
            //TODO prefix handling
            if (OutputType.STREAM_OUTPUT.equals(parameters.getOutput().getOutputType())) {
                write(tmpFile).overwriting(parameters.isOverwrite()).to(
                        destination((PdfStreamOutput) parameters.getOutput()));
            } else {
                write(tmpFile).overwriting(parameters.isOverwrite()).to(
                        destination((PdfFileOutput) parameters.getOutput()).withPrefix(parameters.getOutputPrefix()));
            }
            LOG.debug(String.format("Input rotated and written to %s", parameters.getOutput()));
            notifyEvent().stepsCompleted(currentStep).outOf(totalSteps);
        }

    }

    public void after() {
        readerHandler.closePdfReader(reader);
        stamperHandler.closePdfStamper();
    }

}
