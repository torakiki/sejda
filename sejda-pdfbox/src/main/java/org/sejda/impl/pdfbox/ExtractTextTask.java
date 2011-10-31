/*
 * Created on 24/ago/2011
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
package org.sejda.impl.pdfbox;

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

import java.io.File;

import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.impl.pdfbox.component.DefaultPdfSourceOpener;
import org.sejda.impl.pdfbox.component.PDDocumentHandler;
import org.sejda.impl.pdfbox.component.PdfTextExtractor;
import org.sejda.model.SejdaFileExtensions;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.ExtractTextParameters;
import org.sejda.model.pdf.encryption.PdfAccessPermission;
import org.sejda.model.task.BaseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PDFBox implementation of a task extracting text from a list of {@link PdfSource}
 * 
 * @author Andrea Vacondio
 * 
 */
public class ExtractTextTask extends BaseTask<ExtractTextParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(ExtractTextTask.class);

    private int totalSteps;
    private PDDocumentHandler documentHandler = null;
    private MultipleOutputWriter outputWriter = OutputWriters.newMultipleOutputWriter();
    private PdfSourceOpener<PDDocumentHandler> documentLoader;
    private PdfTextExtractor textExtractor;

    public void before(ExtractTextParameters parameters) throws TaskException {
        totalSteps = parameters.getSourceList().size();
        documentLoader = new DefaultPdfSourceOpener();
        textExtractor = new PdfTextExtractor(parameters.getTextEncoding());
    }

    public void execute(ExtractTextParameters parameters) throws TaskException {
        int currentStep = 0;
        for (PdfSource source : parameters.getSourceList()) {
            currentStep++;
            LOG.debug("Opening {}", source);
            documentHandler = source.open(documentLoader);
            documentHandler.getPermissions().ensurePermission(PdfAccessPermission.COPY_AND_EXTRACT);
            documentHandler.setCreatorOnPDDocument();

            File tmpFile = createTemporaryBuffer();
            LOG.debug("Created output on temporary buffer {}", tmpFile);

            textExtractor.extract(documentHandler.getUnderlyingPDDocument(), tmpFile);
            String outName = nameGenerator(parameters.getOutputPrefix()).generate(
                    nameRequest(SejdaFileExtensions.TXT_EXTENSION).originalName(source.getName()).fileNumber(
                            currentStep));
            outputWriter.addOutput(file(tmpFile).name(outName));

            closeResources();

            notifyEvent(getNotifiableTaskMetadata()).stepsCompleted(currentStep).outOf(totalSteps);
        }

        outputWriter.flushOutputs(parameters.getOutput(), parameters.isOverwrite());
        LOG.debug("Text extracted from input documents and written to {}", parameters.getOutput());

    }

    public void after() {
        closeResources();
    }

    private void closeResources() {
        nullSafeCloseQuietly(documentHandler);
        nullSafeCloseQuietly(textExtractor);
    }
}
