/*
 * Created on Jul 11, 2011
 * Copyright 2011 by Nero Couvalli (angelthepunisher@gmail.com).
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
import static org.sejda.core.support.io.IOUtils.createTemporaryPdfBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;

import java.io.File;
import java.util.Map.Entry;

import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.io.SingleOutputWriter;
import org.sejda.impl.pdfbox.component.DefaultPdfSourceOpener;
import org.sejda.impl.pdfbox.component.PDDocumentHandler;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.SetMetadataParameters;
import org.sejda.model.pdf.PdfMetadataKey;
import org.sejda.model.task.BaseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PDFBox implementation of a task setting metadata on an input {@link PdfSource}.
 * 
 * @author Nero Couvalli
 * 
 */
public class SetMetadataTask extends BaseTask<SetMetadataParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(SetMetadataTask.class);

    private PDDocumentHandler documentHandler = null;
    private SingleOutputWriter outputWriter;
    private PdfSourceOpener<PDDocumentHandler> documentLoader;

    public void before(SetMetadataParameters parameters) {
        documentLoader = new DefaultPdfSourceOpener();
        outputWriter = OutputWriters.newSingleOutputWriter(parameters.isOverwrite());
    }

    public void execute(SetMetadataParameters parameters) throws TaskException {
        PdfSource<?> source = parameters.getSource();
        LOG.debug("Opening {}", source);
        documentHandler = source.open(documentLoader);
        documentHandler.setCreatorOnPDDocument();

        File tmpFile = createTemporaryPdfBuffer();
        LOG.debug("Created output temporary buffer {}", tmpFile);

        LOG.debug("Setting metadata on temporary document.");
        PDDocumentInformation actualMeta = documentHandler.getUnderlyingPDDocument().getDocumentInformation();
        for (Entry<PdfMetadataKey, String> meta : parameters.entrySet()) {
            LOG.trace("'{}' -> '{}'", meta.getKey().getKey(), meta.getValue());
            actualMeta.setCustomMetadataValue(meta.getKey().getKey(), meta.getValue());
        }

        documentHandler.setVersionOnPDDocument(parameters.getVersion());
        documentHandler.compressXrefStream(parameters.isCompressXref());
        documentHandler.savePDDocument(tmpFile);
        nullSafeCloseQuietly(documentHandler);

        outputWriter.setOutput(file(tmpFile).name(parameters.getOutputName()));
        parameters.getOutput().accept(outputWriter);

        LOG.debug("Metadata set on {}", parameters.getOutput());

    }

    public void after() {
        nullSafeCloseQuietly(documentHandler);
    }

}