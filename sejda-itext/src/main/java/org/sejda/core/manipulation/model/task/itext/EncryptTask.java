/*
 * Created on 17/set/2010
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
package org.sejda.core.manipulation.model.task.itext;

import static org.sejda.core.manipulation.model.task.itext.util.EncryptionUtils.getAccessPermission;
import static org.sejda.core.manipulation.model.task.itext.util.EncryptionUtils.getEncryptionAlgorithm;
import static org.sejda.core.manipulation.model.task.itext.util.ITextUtils.nullSafeClosePdfReader;
import static org.sejda.core.manipulation.model.task.itext.util.ITextUtils.nullSafeClosePdfStamperHandler;
import static org.sejda.core.manipulation.model.task.itext.util.PdfReaderUtils.openReader;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.perfix.NameGenerator.nameGenerator;
import static org.sejda.core.support.perfix.model.NameGenerationRequest.nameRequest;

import java.io.File;

import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.model.input.PdfSource;
import org.sejda.core.manipulation.model.parameter.EncryptParameters;
import org.sejda.core.manipulation.model.pdf.encryption.PdfAccessPermission;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.manipulation.model.task.itext.component.PdfStamperHandler;
import org.sejda.core.support.io.MultipleOutputWriterSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.pdf.PdfEncryptor;
import com.lowagie.text.pdf.PdfReader;

/**
 * Perform encryption of the input {@link PdfSource} list using input parameters.
 * 
 * @author Andrea Vacondio
 * 
 */
public class EncryptTask implements Task<EncryptParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(EncryptTask.class);

    private PdfReader reader = null;
    private PdfStamperHandler stamperHandler = null;
    private int totalSteps;
    private int permissions = 0;
    private MultipleOutputWriterSupport outputWriter;

    public void before(EncryptParameters parameters) throws TaskException {
        outputWriter = new MultipleOutputWriterSupport();
        totalSteps = parameters.getSourceList().size() + 1;
        for (PdfAccessPermission permission : parameters.getPermissions()) {
            permissions |= getAccessPermission(permission);
        }
    }

    public void execute(EncryptParameters parameters) throws TaskException {
        int currentStep = 0;
        for (PdfSource source : parameters.getSourceList()) {
            currentStep++;
            LOG.debug("Opening {} ...", source);
            reader = openReader(source, true);

            File tmpFile = outputWriter.createTemporaryPdfBuffer();
            LOG.debug("Created output on temporary buffer {} ...", tmpFile);
            stamperHandler = new PdfStamperHandler(reader, tmpFile, parameters.getVersion());

            stamperHandler.setCompressionOnStamper(parameters.isCompressXref());
            stamperHandler.setCreatorOnStamper(reader);
            stamperHandler.setEncryptionOnStamper(getEncryptionAlgorithm(parameters.getEncryptionAlgorithm()),
                    parameters.getUserPassword(), parameters.getOwnerPassword(), permissions);

            nullSafeClosePdfReader(reader);
            nullSafeClosePdfStamperHandler(stamperHandler);

            String outName = nameGenerator(parameters.getOutputPrefix(), source.getName()).generate(nameRequest());
            outputWriter.addOutput(file(tmpFile).name(outName));

            notifyEvent().stepsCompleted(currentStep).outOf(totalSteps);
        }

        outputWriter.flushOutputs(parameters.getOutput(), parameters.isOverwrite());
        notifyEvent().stepsCompleted(++currentStep).outOf(totalSteps);

        LOG.debug("Input documents encrypted and written to {}", parameters.getOutput());
        LOG.debug("Permissions {}", PdfEncryptor.getPermissionsVerbose(permissions));
    }

    public void after() {
        nullSafeClosePdfReader(reader);
        nullSafeClosePdfStamperHandler(stamperHandler);
    }
}
