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

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryPdfBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

import java.io.File;

import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.impl.pdfbox.component.DefaultPdfSourceOpener;
import org.sejda.impl.pdfbox.component.PDDocumentHandler;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.DecryptParameters;
import org.sejda.model.task.BaseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PDFBox implementation of a task performing decrypt of a list of encrypted {@link PdfSource}
 * 
 * @author Andrea Vacondio
 * 
 */
public class DecryptTask extends BaseTask<DecryptParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(DecryptTask.class);

    private int totalSteps;
    private PDDocumentHandler documentHandler = null;
    private MultipleOutputWriter outputWriter = OutputWriters.newMultipleOutputWriter();

    private PdfSourceOpener<PDDocumentHandler> documentLoader;

    public void before(DecryptParameters parameters) {
        totalSteps = parameters.getSourceList().size();
        documentLoader = new DefaultPdfSourceOpener();
    }

    public void execute(DecryptParameters parameters) throws TaskException {
        int currentStep = 0;
        for (PdfSource<?> source : parameters.getSourceList()) {
            currentStep++;
            LOG.debug("Opening {}", source);
            documentHandler = source.open(documentLoader);
            documentHandler.getPermissions().ensureOwnerPermissions();
            documentHandler.setCreatorOnPDDocument();

            File tmpFile = createTemporaryPdfBuffer();
            LOG.debug("Created output on temporary buffer {}", tmpFile);

            documentHandler.setVersionOnPDDocument(parameters.getVersion());
            documentHandler.compressXrefStream(parameters.isCompressXref());
            documentHandler.saveDecryptedPDDocument(tmpFile);

            String outName = nameGenerator(parameters.getOutputPrefix()).generate(
                    nameRequest().originalName(source.getName()).fileNumber(currentStep));
            outputWriter.addOutput(file(tmpFile).name(outName));

            nullSafeCloseQuietly(documentHandler);

            notifyEvent(getNotifiableTaskMetadata()).stepsCompleted(currentStep).outOf(totalSteps);
        }

        outputWriter.flushOutputs(parameters.getOutput(), parameters.isOverwrite());
        LOG.debug("Input documents decrypted and written to {}", parameters.getOutput());
    }

    public void after() {
        nullSafeCloseQuietly(documentHandler);
    }

}
