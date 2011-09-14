/*
 * Created on 02/jul/2011
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
package org.sejda.impl.itext.component.split;

import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;
import static org.sejda.impl.itext.component.PdfCopiers.nullSafeClosePdfCopy;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.sejda.core.exception.TaskException;
import org.sejda.core.exception.TaskExecutionException;
import org.sejda.core.manipulation.model.outline.OutlineSubsetProvider;
import org.sejda.core.manipulation.model.parameter.base.SinglePdfSourceMultipleOutputParameters;
import org.sejda.core.manipulation.model.pdf.PdfVersion;
import org.sejda.core.support.io.MultipleOutputWriterSupport;
import org.sejda.core.support.prefix.model.NameGenerationRequest;
import org.sejda.impl.itext.component.ITextOutlineSubsetProvider;
import org.sejda.impl.itext.component.PdfCopier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.pdf.PdfReader;

/**
 * Abstract component providing a skeletal implementation of the split execution.
 * 
 * @author Andrea Vacondio
 * @param <T>
 *            the type of parameters the splitter needs to have all the information necessary to perform the split.
 */
abstract class AbstractPdfSplitter<T extends SinglePdfSourceMultipleOutputParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPdfSplitter.class);

    private PdfReader reader;
    private T parameters;
    private int totalPages;
    private OutlineSubsetProvider<Map<String, Object>> outlineSubsetProvider;
    private MultipleOutputWriterSupport outputWriter = new MultipleOutputWriterSupport();

    /**
     * Creates a new splitter using the given reader.
     * 
     * @param reader
     */
    AbstractPdfSplitter(PdfReader reader) {
        this.reader = reader;
        this.totalPages = reader.getNumberOfPages();
        this.outlineSubsetProvider = new ITextOutlineSubsetProvider(reader);
    }

    int getTotalNumberOfPages() {
        return totalPages;
    }

    public void split() throws TaskException {
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
                pdfCopier.addPage(reader, page);
                notifyEvent().stepsCompleted(page).outOf(totalPages);
                if (nextOutputStrategy().isClosing(page) || page == totalPages) {
                    LOG.debug("Adding bookmarks to the temporary buffer");
                    pdfCopier.setOutline(new ArrayList<Map<String, Object>>(outlineSubsetProvider
                            .getOutlineUntillPage(page)));
                    closeCopier(pdfCopier);
                    LOG.debug("Ending split at page {} of the original document", page);
                }
            }
        } finally {
            closeCopier(pdfCopier);
        }
        outputWriter.flushOutputs(parameters.getOutput(), parameters.isOverwrite());
    }

    private PdfCopier open(int page, int outputDocumentsCounter) throws TaskException {
        File tmpFile = outputWriter.createTemporaryPdfBuffer();
        LOG.debug("Created output temporary buffer {}", tmpFile);

        PdfCopier pdfCopier = openCopier(reader, tmpFile, parameters.getVersion());
        pdfCopier.setCompression(parameters.isCompressXref());

        String outName = nameGenerator(parameters.getOutputPrefix()).generate(
                enrichNameGenerationRequest(nameRequest().page(page).originalName(parameters.getSource().getName())
                        .fileNumber(outputDocumentsCounter)));
        outputWriter.addOutput(file(tmpFile).name(outName));
        outlineSubsetProvider.startPage(page);
        return pdfCopier;
    }

    /**
     * @param request
     * @return the input request enriched by an splitter extending class with specific values.
     */
    abstract NameGenerationRequest enrichNameGenerationRequest(NameGenerationRequest request);

    abstract PdfCopier openCopier(PdfReader reader, File outputFile, PdfVersion version) throws TaskException;

    private void closeCopier(PdfCopier pdfCopier) {
        nullSafeClosePdfCopy(pdfCopier);
    }

    /**
     * @return the strategy to use to know if it's time to open a new document or close the current one.
     */
    abstract NextOutputStrategy nextOutputStrategy();

    /**
     * Sets the parameters to use during the split process. Parameters are mandatory to be able to perform the split.
     * 
     * @param parameters
     */
    void setParameters(T parameters) {
        this.parameters = parameters;
    }

    /**
     * Strategy used by the {@link AbstractPdfSplitter} to know when it's time to close the ongoing output and open a new one.
     * 
     * @author Andrea Vacondio
     * 
     */
    interface NextOutputStrategy {

        /**
         * Ensures that the strategy implementation is in a valid state.
         * 
         * @throws TaskExecutionException
         *             if not in a valid state.
         */
        void ensureIsValid() throws TaskExecutionException;

        /**
         * @param page
         *            the current processing page
         * @return true if the splitter should open a new output, false otherwise.
         */
        boolean isOpening(Integer page);

        /**
         * @param page
         *            the current processing page
         * @return true if the splitter should close the current output, false otherwise.
         */
        boolean isClosing(Integer page);
    }
}
