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

import java.io.File;

import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.io.SingleOutputWriter;
import org.sejda.impl.pdfbox.component.DefaultPdfSourceOpener;
import org.sejda.impl.pdfbox.component.PDDocumentHandler;
import org.sejda.impl.pdfbox.component.PdfFooterWriter;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.SetFooterParameters;
import org.sejda.model.pdf.encryption.PdfAccessPermission;
import org.sejda.model.task.BaseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PDFBox implementation of a task adding footer labels to pages
 * 
 * @author Eduard Weissmann
 * 
 */
public class SetFooterTask extends BaseTask<SetFooterParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(SetFooterTask.class);

    private PDDocumentHandler documentHandler = null;
    private SingleOutputWriter outputWriter;
    private PdfFooterWriter footerWriter = null;

    private PdfSourceOpener<PDDocumentHandler> documentLoader;

    public void before(SetFooterParameters parameters) {
        documentLoader = new DefaultPdfSourceOpener();
        outputWriter = OutputWriters.newSingleOutputWriter(parameters.isOverwrite());
    }

    public void execute(SetFooterParameters parameters) throws TaskException {
        notifyEvent(getNotifiableTaskMetadata()).progressUndetermined();

        PdfSource<?> source = parameters.getSource();
        LOG.debug("Opening {}", source);
        documentHandler = source.open(documentLoader);
        documentHandler.getPermissions().ensurePermission(PdfAccessPermission.MODIFY);
        documentHandler.setCreatorOnPDDocument();

        File tmpFile = createTemporaryPdfBuffer();
        LOG.debug("Created output on temporary buffer {}", tmpFile);

        documentHandler.setVersionOnPDDocument(parameters.getVersion());
        documentHandler.compressXrefStream(parameters.isCompressXref());

        footerWriter = new PdfFooterWriter(documentHandler);
        footerWriter.writeFooter(parameters);

        documentHandler.savePDDocument(tmpFile);

        outputWriter.setOutput(file(tmpFile).name(parameters.getOutputName()));

        nullSafeCloseQuietly(documentHandler);

        parameters.getOutput().accept(outputWriter);
        LOG.debug("Footer added and document written to {}", parameters.getOutput());
    }

    public void after() {
        nullSafeCloseQuietly(documentHandler);
    }

}
