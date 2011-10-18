/*
 * Created on 13/set/2011
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

import static org.sejda.core.support.io.IOUtils.createTemporaryPdfBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.util.ComponentsUtility.nullSafeCloseQuietly;

import java.io.File;
import java.util.Set;

import org.sejda.core.exception.TaskException;
import org.sejda.core.exception.TaskExecutionException;
import org.sejda.core.manipulation.model.input.PdfSource;
import org.sejda.core.manipulation.model.input.PdfSourceOpener;
import org.sejda.core.manipulation.model.parameter.ExtractPagesParameters;
import org.sejda.core.manipulation.model.pdf.encryption.PdfAccessPermission;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.io.SingleOutputWriter;
import org.sejda.impl.pdfbox.component.DefaultPdfSourceOpener;
import org.sejda.impl.pdfbox.component.PDDocumentHandler;
import org.sejda.impl.pdfbox.component.PagesExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PDFBox implementation of the task responsible for extracting pages from a given pdf document.
 * 
 * @author Andrea Vacondio
 * 
 */
public class ExtractPagesTask implements Task<ExtractPagesParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(ExtractPagesTask.class);

    private PagesExtractor extractor = null;
    private SingleOutputWriter outputWriter = OutputWriters.newSingleOutputWriter();
    private PdfSourceOpener<PDDocumentHandler> documentLoader;
    private PDDocumentHandler sourceDocumentHandler;

    public void before(ExtractPagesParameters parameters) {
        documentLoader = new DefaultPdfSourceOpener();
    }

    public void execute(ExtractPagesParameters parameters) throws TaskException {
        PdfSource source = parameters.getSource();
        LOG.debug("Opening {}", source);
        sourceDocumentHandler = source.open(documentLoader);
        sourceDocumentHandler.getPermissions().ensurePermission(PdfAccessPermission.ASSEMBLE);

        Set<Integer> pages = parameters.getPages(sourceDocumentHandler.getNumberOfPages());
        if (pages == null || pages.isEmpty()) {
            throw new TaskExecutionException("No page has been selected for extraction.");
        }
        extractor = new PagesExtractor(sourceDocumentHandler);
        LOG.debug("Extracting pages {}", pages);
        extractor.extractPages(pages);
        extractor.setVersionOnPDDocument(parameters.getVersion());
        extractor.compressXrefStream(parameters.isCompressXref());

        File tmpFile = createTemporaryPdfBuffer();
        LOG.debug("Created output temporary buffer {}", tmpFile);
        extractor.saveDecryptedPDDocument(tmpFile);

        closeResource();

        outputWriter.flushSingleOutput(file(tmpFile).name(parameters.getOutputName()), parameters.getOutput(),
                parameters.isOverwrite());
        LOG.debug("Pages extracted and written to {}", parameters.getOutput());
    }

    public void after() {
        closeResource();
    }

    private void closeResource() {
        nullSafeCloseQuietly(sourceDocumentHandler);
        nullSafeCloseQuietly(extractor);
    }

}
