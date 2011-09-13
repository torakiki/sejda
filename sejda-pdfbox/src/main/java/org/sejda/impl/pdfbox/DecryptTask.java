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
package org.sejda.impl.pdfbox;

import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;
import static org.sejda.impl.pdfbox.component.PDDocumentHandler.nullSafeClose;

import java.io.File;

import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.model.input.PdfSource;
import org.sejda.core.manipulation.model.input.PdfSourceOpener;
import org.sejda.core.manipulation.model.parameter.DecryptParameters;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.support.io.MultipleOutputWriterSupport;
import org.sejda.impl.pdfbox.component.DefaultPdfSourceOpener;
import org.sejda.impl.pdfbox.component.PDDocumentHandler;
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
    private PDDocumentHandler documentHandler = null;
    private MultipleOutputWriterSupport outputWriter;
    private PdfSourceOpener<PDDocumentHandler> documentLoader;

    public void before(DecryptParameters parameters) {
        outputWriter = new MultipleOutputWriterSupport();
        totalSteps = parameters.getSourceList().size();
        documentLoader = new DefaultPdfSourceOpener();
    }

    public void execute(DecryptParameters parameters) throws TaskException {
        int currentStep = 0;
        for (PdfSource source : parameters.getSourceList()) {
            LOG.debug("Opening {}", source);
            documentHandler = source.open(documentLoader);
            documentHandler.getPermissions().ensureOwnerPermissions();
            documentHandler.setCreatorOnPDDocument();

            File tmpFile = outputWriter.createTemporaryPdfBuffer();
            LOG.debug("Created output on temporary buffer {}", tmpFile);

            documentHandler.setVersionOnPDDocument(parameters.getVersion());
            documentHandler.compressXrefStream(parameters.isCompressXref());
            documentHandler.saveDecryptedPDDocument(tmpFile);

            String outName = nameGenerator(parameters.getOutputPrefix()).generate(
                    nameRequest().originalName(source.getName()));
            outputWriter.addOutput(file(tmpFile).name(outName));

            nullSafeClose(documentHandler);

            notifyEvent().stepsCompleted(++currentStep).outOf(totalSteps);
        }

        outputWriter.flushOutputs(parameters.getOutput(), parameters.isOverwrite());
        LOG.debug("Input documents decrypted and written to {}", parameters.getOutput());
    }

    public void after() {
        nullSafeClose(documentHandler);
    }

}
