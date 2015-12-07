/*
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.impl.sambox;

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.support.io.IOUtils.createTemporaryPdfBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;

import java.io.File;
import java.util.Set;

import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.io.SingleOutputWriter;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.impl.sambox.component.PagesExtractor;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.ExtractPagesParameters;
import org.sejda.model.pdf.encryption.PdfAccessPermission;
import org.sejda.model.task.BaseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SAMBox implementation of the task responsible for extracting pages from a given pdf document.
 * 
 * @author Andrea Vacondio
 * 
 */
public class ExtractPagesTask extends BaseTask<ExtractPagesParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(ExtractPagesTask.class);

    private PagesExtractor extractor = null;
    private SingleOutputWriter outputWriter;
    private PdfSourceOpener<PDDocumentHandler> documentLoader;
    private PDDocumentHandler sourceDocumentHandler;

    @Override
    public void before(ExtractPagesParameters parameters) {
        documentLoader = new DefaultPdfSourceOpener();
        outputWriter = OutputWriters.newSingleOutputWriter(parameters.getExistingOutputPolicy());
    }

    @Override
    public void execute(ExtractPagesParameters parameters) throws TaskException {
        PdfSource<?> source = parameters.getSource();
        LOG.debug("Opening {}", source);
        sourceDocumentHandler = source.open(documentLoader);
        sourceDocumentHandler.getPermissions().ensurePermission(PdfAccessPermission.ASSEMBLE);

        Set<Integer> pages = parameters.getPages(sourceDocumentHandler.getNumberOfPages());
        if (pages == null || pages.isEmpty()) {
            throw new TaskExecutionException("No page has been selected for extraction.");
        }
        extractor = new PagesExtractor(sourceDocumentHandler.getUnderlyingPDDocument());
        extractor.setVersion(parameters.getVersion());
        extractor.setCompress(parameters.isCompress());

        LOG.debug("Extracting pages {}", pages);
        extractor.retain(pages, getNotifiableTaskMetadata());

        File tmpFile = createTemporaryPdfBuffer();
        LOG.debug("Created output temporary buffer {}", tmpFile);
        extractor.save(tmpFile);

        closeResource();

        outputWriter.setOutput(file(tmpFile).name(parameters.getOutputName()));
        parameters.getOutput().accept(outputWriter);
        LOG.debug("Pages extracted and written to {}", parameters.getOutput());
    }

    @Override
    public void after() {
        closeResource();
    }

    private void closeResource() {
        nullSafeCloseQuietly(sourceDocumentHandler);
        nullSafeCloseQuietly(extractor);
    }

}
