/*
 * Created on 17/set/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryPdfBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;
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
    private MultipleOutputWriter outputWriter;
    private PdfSourceOpener<PdfReader> sourceOpener;

    @Override
    public void before(EncryptParameters parameters) {
        totalSteps = parameters.getSourceList().size();
        sourceOpener = PdfSourceOpeners.newPartialReadOpener();
        outputWriter = OutputWriters.newMultipleOutputWriter(parameters.isOverwrite());
        for (PdfAccessPermission permission : parameters.getPermissions()) {
            permissions |= getAccessPermission(permission);
        }
    }

    @Override
    public void execute(EncryptParameters parameters) throws TaskException {
        int currentStep = 0;
        for (PdfSource<?> source : parameters.getSourceList()) {
            currentStep++;
            LOG.debug("Opening {} ", source);
            reader = source.open(sourceOpener);

            File tmpFile = createTemporaryPdfBuffer();
            LOG.debug("Created output temporary buffer {} ", tmpFile);
            stamperHandler = new PdfStamperHandler(reader, tmpFile, parameters.getVersion());

            stamperHandler.setCompressionOnStamper(parameters.isCompress());
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

        parameters.getOutput().accept(outputWriter);
        LOG.debug("Input documents encrypted and written to {}", parameters.getOutput());
        LOG.debug("Permissions {}", PdfEncryptor.getPermissionsVerbose(permissions));
    }

    @Override
    public void after() {
        nullSafeClosePdfReader(reader);
        nullSafeCloseQuietly(stamperHandler);
    }
}
