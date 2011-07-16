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
package org.sejda.core.manipulation.model.task.pdfbox;

import static org.sejda.core.manipulation.model.task.pdfbox.util.PDDocumentIOUtil.closePDDocumentQuitely;
import static org.sejda.core.manipulation.model.task.pdfbox.util.PDDocumentIOUtil.savePDDocument;
import static org.sejda.core.manipulation.model.task.pdfbox.util.PDDocumentUtil.compressXrefStream;
import static org.sejda.core.manipulation.model.task.pdfbox.util.PDDocumentUtil.setCreatorOnPDDocument;
import static org.sejda.core.manipulation.model.task.pdfbox.util.PDDocumentUtil.setVersionOnPDDocument;
import static org.sejda.core.support.io.model.FileOutput.file;

import java.io.File;
import java.util.Map.Entry;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.model.input.PdfSource;
import org.sejda.core.manipulation.model.input.PdfSourceOpener;
import org.sejda.core.manipulation.model.parameter.SetMetadataParameters;
import org.sejda.core.manipulation.model.pdf.PdfMetadataKey;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.manipulation.model.task.pdfbox.component.PDDocumentLoader;
import org.sejda.core.support.io.SingleOutputWriterSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task setting metadata on an input {@link PdfSource}.
 * 
 * @author Nero Couvalli
 * 
 */
public class SetMetadataTask implements Task<SetMetadataParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(SetMetadataTask.class);

    private PDDocument document = null;
    private SingleOutputWriterSupport outputWriter;
    private PdfSourceOpener<PDDocument> documentLoader;

    public void before(SetMetadataParameters parameters) {
        outputWriter = new SingleOutputWriterSupport();
        documentLoader = new PDDocumentLoader();
    }

    public void execute(SetMetadataParameters parameters) throws TaskException {
        PdfSource source = parameters.getSource();
        LOG.debug("Opening {} ...", source);
        document = source.open(documentLoader);
        File tmpFile = outputWriter.createTemporaryPdfBuffer();
        LOG.debug("Created output on temporary buffer {} ...", tmpFile);

        LOG.debug("Setting metadata on temporary document.");
        PDDocumentInformation actualMeta = document.getDocumentInformation();
        for (Entry<PdfMetadataKey, String> meta : parameters.entrySet()) {
            actualMeta.setCustomMetadataValue(meta.getKey().getKey(), meta.getValue());
        }

        setVersionOnPDDocument(document, parameters.getVersion());

        compressXrefStream(document);
        setCreatorOnPDDocument(document);
        savePDDocument(document, tmpFile);
        closePDDocumentQuitely(document);

        outputWriter.flushSingleOutput(file(tmpFile).name(source.getName()), parameters.getOutput(),
                parameters.isOverwrite());

        LOG.debug("Metadata set on {}", parameters.getOutput());

    }

    public void after() {
        closePDDocumentQuitely(document);
    }

}