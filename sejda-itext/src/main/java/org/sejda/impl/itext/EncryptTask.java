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
package org.sejda.impl.itext;

import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryPdfBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;
import static org.sejda.core.support.util.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.impl.itext.util.EncryptionUtils.getAccessPermission;
import static org.sejda.impl.itext.util.EncryptionUtils.getEncryptionAlgorithm;
import static org.sejda.impl.itext.util.ITextUtils.nullSafeClosePdfReader;

import java.io.File;

import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.impl.itext.component.PdfStamperHandler;
import org.sejda.impl.itext.component.input.PdfSourceOpeners;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.EncryptParameters;
import org.sejda.model.pdf.encryption.PdfAccessPermission;
import org.sejda.model.task.BaseTask;
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
public class EncryptTask extends BaseTask<EncryptParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(EncryptTask.class);

    private PdfReader reader = null;
    private PdfStamperHandler stamperHandler = null;
    private int totalSteps;
    private int permissions = 0;
    private MultipleOutputWriter outputWriter = OutputWriters.newMultipleOutputWriter();
    private PdfSourceOpener<PdfReader> sourceOpener;

    public void before(EncryptParameters parameters) {
        totalSteps = parameters.getSourceList().size();
        sourceOpener = PdfSourceOpeners.newPartialReadOpener();
        for (PdfAccessPermission permission : parameters.getPermissions()) {
            permissions |= getAccessPermission(permission);
        }
    }

    public void execute(EncryptParameters parameters) throws TaskException {
        int currentStep = 0;
        for (PdfSource source : parameters.getSourceList()) {
            currentStep++;
            LOG.debug("Opening {} ", source);
            reader = source.open(sourceOpener);

            File tmpFile = createTemporaryPdfBuffer();
            LOG.debug("Created output temporary buffer {} ", tmpFile);
            stamperHandler = new PdfStamperHandler(reader, tmpFile, parameters.getVersion());

            stamperHandler.setCompressionOnStamper(parameters.isCompressXref());
            stamperHandler.setCreatorOnStamper(reader);
            stamperHandler.setEncryptionOnStamper(getEncryptionAlgorithm(parameters.getEncryptionAlgorithm()),
                    parameters.getUserPassword(), parameters.getOwnerPassword(), permissions);

            nullSafeClosePdfReader(reader);
            nullSafeCloseQuietly(stamperHandler);

            String outName = nameGenerator(parameters.getOutputPrefix()).generate(
                    nameRequest().originalName(source.getName()).fileNumber(currentStep));
            outputWriter.addOutput(file(tmpFile).name(outName));

            notifyEvent(getNotifiableTaskMetadata()).stepsCompleted(currentStep).outOf(totalSteps);
        }

        outputWriter.flushOutputs(parameters.getOutput(), parameters.isOverwrite());
        LOG.debug("Input documents encrypted and written to {}", parameters.getOutput());
        LOG.debug("Permissions {}", PdfEncryptor.getPermissionsVerbose(permissions));
    }

    public void after() {
        nullSafeClosePdfReader(reader);
        nullSafeCloseQuietly(stamperHandler);
    }
}
