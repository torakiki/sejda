/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com).
 * 
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox;

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;

import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.impl.sambox.component.split.AbstractPdfSplitter;
import org.sejda.impl.sambox.component.split.ByTextChangesPdfSplitter;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.SplitByTextContentParameters;
import org.sejda.model.pdf.encryption.PdfAccessPermission;
import org.sejda.model.task.BaseTask;
import org.sejda.sambox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SplitByTextContentTask extends BaseTask<SplitByTextContentParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(SplitByTextContentTask.class);

    private PdfSourceOpener<PDDocumentHandler> documentLoader;
    private PDDocumentHandler sourceDocumentHandler;
    private AbstractPdfSplitter splitter;

    public void before(SplitByTextContentParameters parameters) {
        documentLoader = new DefaultPdfSourceOpener();
    }

    public void execute(SplitByTextContentParameters parameters) throws TaskException {
        PdfSource<?> source = parameters.getSource();
        LOG.debug("Opening {}", source);
        sourceDocumentHandler = source.open(documentLoader);
        sourceDocumentHandler.getPermissions().ensurePermission(PdfAccessPermission.ASSEMBLE);
        PDDocument sourceDocument = sourceDocumentHandler.getUnderlyingPDDocument();

        splitter = new ByTextChangesPdfSplitter(sourceDocument, parameters);
        LOG.debug("Starting to split by text content");
        splitter.split(getNotifiableTaskMetadata());

        LOG.debug("Input documents split and written to {}", parameters.getOutput());
    }

    public void after() {
        closeResource();
    }

    private void closeResource() {
        nullSafeCloseQuietly(sourceDocumentHandler);
        splitter = null;
    }
}
