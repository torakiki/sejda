/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com)
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
package org.sejda.impl.pdfbox.component;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.prefix.model.NameGenerationRequest;
import org.sejda.model.exception.TaskException;
import org.sejda.model.parameter.base.SinglePdfSourceMultipleOutputParameters;
import org.sejda.model.split.NextOutputStrategy;
import org.sejda.model.task.NotifiableTaskMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryPdfBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

public abstract class AbstractPdfSplitter<T extends SinglePdfSourceMultipleOutputParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPdfSplitter.class);

    private PDDocument document;
    private T parameters;
    private int totalPages;
    private MultipleOutputWriter outputWriter;

    public AbstractPdfSplitter(PDDocument document, T parameters) {
        this.document = document;
        this.parameters = parameters;
        this.totalPages = document.getNumberOfPages();
        this.outputWriter = OutputWriters.newMultipleOutputWriter(parameters.isOverwrite());
    }

    public void split(NotifiableTaskMetadata taskMetadata) throws TaskException {
        nextOutputStrategy().ensureIsValid();
        PdfCopier pdfCopier = null;
        try {
            int outputDocumentsCounter = 0;
            for (int page = 1; page <= totalPages; page++) {
                if (nextOutputStrategy().isOpening(page)) {
                    LOG.debug("Starting split at page {} of the original document", page);
                    outputDocumentsCounter++;
                    pdfCopier = open(page, outputDocumentsCounter);
                }
                pdfCopier.addPage(document, page);
                notifyEvent(taskMetadata).stepsCompleted(page).outOf(totalPages);
                if (nextOutputStrategy().isClosing(page) || page == totalPages) {
                    pdfCopier.saveToFile();
                    nullSafeCloseQuietly(pdfCopier);
                    LOG.debug("Ending split at page {} of the original document", page);
                }
            }
        } finally {
            nullSafeCloseQuietly(pdfCopier);
        }
        parameters.getOutput().accept(outputWriter);
    }

    private PdfCopier open(int page, int outputDocumentsCounter) throws TaskException {
        File tmpFile = createTemporaryPdfBuffer();
        LOG.debug("Created output temporary buffer {}", tmpFile);

        PdfCopier pdfCopier = new PdfCopier(document, tmpFile, parameters.getVersion());
        pdfCopier.setCompression(parameters.isCompress());

        String outName = nameGenerator(parameters.getOutputPrefix()).generate(
                enrichNameGenerationRequest(nameRequest().page(page).originalName(parameters.getSource().getName())
                        .fileNumber(outputDocumentsCounter)));
        outputWriter.addOutput(file(tmpFile).name(outName));
        return pdfCopier;
    }

    abstract NameGenerationRequest enrichNameGenerationRequest(NameGenerationRequest request);

    /**
     * @return the strategy to use to know if it's time to open a new document or close the current one.
     */
    abstract NextOutputStrategy nextOutputStrategy();
}
