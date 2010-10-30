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
import static org.sejda.core.manipulation.model.task.itext.util.ITextUtils.closePdfReader;
import static org.sejda.core.manipulation.model.task.itext.util.ITextUtils.closePdfStamperHandlerQuietly;
import static org.sejda.core.manipulation.model.task.itext.util.PdfReaderUtils.openReader;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.perfix.NameGenerator.nameGenerator;
import static org.sejda.core.support.perfix.model.NameGenerationRequest.nameRequest;

import java.io.File;

import org.sejda.core.exception.TaskException;
import org.sejda.core.exception.TaskExecutionException;
import org.sejda.core.manipulation.model.input.PdfSource;
import org.sejda.core.manipulation.model.parameter.RotateParameters;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.manipulation.model.task.itext.component.PdfStamperHandler;
import org.sejda.core.support.io.MultipleOutputWriterSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.pdf.PdfReader;

/**
 * Task performing pages rotation on a list of {@link PdfSource}.
 * 
 * @author Andrea Vacondio
 * 
 */
public class RotateTask extends MultipleOutputWriterSupport implements Task<RotateParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(RotateTask.class);

    private PdfReader reader = null;
    private PdfStamperHandler stamperHandler = null;
    private int totalSteps;

    public void before(RotateParameters parameters) throws TaskExecutionException {
        totalSteps = parameters.getSourceList().size() + 1;
    }

    public void execute(RotateParameters parameters) throws TaskException {
        int currentStep = 0;

        for (PdfSource source : parameters.getSourceList()) {
            currentStep++;
            LOG.debug("Opening {} ...", source);
            reader = openReader(source, true);

            applyRotation(parameters.getRotation()).to(reader);

            File tmpFile = createTemporaryPdfBuffer();
            LOG.debug("Creating output on temporary buffer {} ...", tmpFile);
            stamperHandler = new PdfStamperHandler(reader, tmpFile, parameters.getVersion());

            stamperHandler.setCompressionOnStamper(parameters.isCompress());
            stamperHandler.setCreatorOnStamper(reader);

            closePdfReader(reader);
            closePdfStamperHandlerQuietly(stamperHandler);

            String outName = nameGenerator(parameters.getOutputPrefix(), source.getName()).generate(nameRequest());
            addOutput(file(tmpFile).name(outName));

            notifyEvent().stepsCompleted(currentStep).outOf(totalSteps);
        }

        flushOutputs(parameters.getOutput(), parameters.isOverwrite());
        notifyEvent().stepsCompleted(++currentStep).outOf(totalSteps);

        LOG.debug("Input documents rotated and written to {}", parameters.getOutput());
    }

    public void after() {
        closePdfReader(reader);
        closePdfStamperHandlerQuietly(stamperHandler);
    }

}
