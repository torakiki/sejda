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

import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;
import static org.sejda.core.support.util.ComponentsUtility.nullSafeCloseQuietly;

import java.io.File;

import org.sejda.core.Sejda;
import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.model.input.PdfSource;
import org.sejda.core.manipulation.model.input.PdfSourceOpener;
import org.sejda.core.manipulation.model.parameter.ExtractTextParameters;
import org.sejda.core.manipulation.model.pdf.encryption.PdfAccessPermission;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.support.io.MultipleOutputWriterSupport;
import org.sejda.impl.pdfbox.component.DefaultPdfSourceOpener;
import org.sejda.impl.pdfbox.component.PDDocumentHandler;
import org.sejda.impl.pdfbox.component.PdfTextExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task extracting text from a collection of {@link PdfSource}
 * 
 * @author Andrea Vacondio
 * 
 */
public class ExtractTextTask implements Task<ExtractTextParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(ExtractTextTask.class);

    private int totalSteps;
    private PDDocumentHandler documentHandler = null;
    private MultipleOutputWriterSupport outputWriter;
    private PdfSourceOpener<PDDocumentHandler> documentLoader;
    private PdfTextExtractor textExtractor;

    public void before(ExtractTextParameters parameters) throws TaskException {
        outputWriter = new MultipleOutputWriterSupport();
        totalSteps = parameters.getSourceList().size();
        documentLoader = new DefaultPdfSourceOpener();
        textExtractor = new PdfTextExtractor(parameters.getTextEncoding());
    }

    public void execute(ExtractTextParameters parameters) throws TaskException {
        int currentStep = 0;
        for (PdfSource source : parameters.getSourceList()) {
            LOG.debug("Opening {}", source);
            documentHandler = source.open(documentLoader);
            documentHandler.getPermissions().ensurePermission(PdfAccessPermission.COPY_AND_EXTRACT);
            documentHandler.setCreatorOnPDDocument();

            File tmpFile = outputWriter.createTemporaryBuffer();
            LOG.debug("Created output on temporary buffer {}", tmpFile);

            textExtractor.extract(documentHandler.getUnderlyingPDDocument(), tmpFile);
            String outName = nameGenerator(parameters.getOutputPrefix()).generate(
                    nameRequest(Sejda.TXT_EXTENSION).originalName(source.getName()));
            outputWriter.addOutput(file(tmpFile).name(outName));

            closeResources();

            notifyEvent().stepsCompleted(++currentStep).outOf(totalSteps);
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
