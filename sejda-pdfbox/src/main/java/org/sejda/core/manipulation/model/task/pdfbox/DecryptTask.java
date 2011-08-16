/*
 * Created on 02/nov/2010
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
package org.sejda.core.manipulation.model.task.pdfbox;

import static org.sejda.core.manipulation.model.task.pdfbox.util.PDDocumentIOUtil.closePDDocumentQuitely;
import static org.sejda.core.manipulation.model.task.pdfbox.util.PDDocumentIOUtil.saveDecryptedPDDocument;
import static org.sejda.core.manipulation.model.task.pdfbox.util.PDDocumentUtil.compressXrefStream;
import static org.sejda.core.manipulation.model.task.pdfbox.util.PDDocumentUtil.ensureOwnerPermissions;
import static org.sejda.core.manipulation.model.task.pdfbox.util.PDDocumentUtil.setCreatorOnPDDocument;
import static org.sejda.core.manipulation.model.task.pdfbox.util.PDDocumentUtil.setVersionOnPDDocument;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

import java.io.File;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.model.input.PdfSource;
import org.sejda.core.manipulation.model.input.PdfSourceOpener;
import org.sejda.core.manipulation.model.parameter.DecryptParameters;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.manipulation.model.task.pdfbox.component.DefaultPdfSourceOpener;
import org.sejda.core.support.io.MultipleOutputWriterSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task performing decrypt of a list of encrypted {@link PdfSource}
 * 
 * @author Andrea Vacondio
 * 
 */
public class DecryptTask implements Task<DecryptParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(DecryptTask.class);

    private int totalSteps;
    private PDDocument document = null;
    private MultipleOutputWriterSupport outputWriter;

    private PdfSourceOpener<PDDocument> documentLoader;

    public void before(DecryptParameters parameters) {
        outputWriter = new MultipleOutputWriterSupport();
        totalSteps = parameters.getSourceList().size();
        documentLoader = new DefaultPdfSourceOpener();
    }

    public void execute(DecryptParameters parameters) throws TaskException {
        int currentStep = 0;
        for (PdfSource source : parameters.getSourceList()) {
            LOG.debug("Opening {} ...", source);
            document = source.open(documentLoader);
            ensureOwnerPermissions(document);

            File tmpFile = outputWriter.createTemporaryPdfBuffer();
            LOG.debug("Creating output on temporary buffer {} ...", tmpFile);
            setVersionOnPDDocument(document, parameters.getVersion());

            compressXrefStream(document);
            setCreatorOnPDDocument(document);
            saveDecryptedPDDocument(document, tmpFile);

            String outName = nameGenerator(parameters.getOutputPrefix()).generate(
                    nameRequest().originalName(source.getName()));
            outputWriter.addOutput(file(tmpFile).name(outName));

            closePDDocumentQuitely(document);

            notifyEvent().stepsCompleted(++currentStep).outOf(totalSteps);
        }

        outputWriter.flushOutputs(parameters.getOutput(), parameters.isOverwrite());
        notifyEvent().stepsCompleted(++currentStep).outOf(totalSteps);

        LOG.debug("Input documents decrypted and written to {}", parameters.getOutput());
    }

    public void after() {
        closePDDocumentQuitely(document);
    }

}
