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
import static org.sejda.core.manipulation.model.task.pdfbox.util.PDDocumentIOUtil.loadPDDocument;

import java.util.Map.Entry;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.model.input.PdfSource;
import org.sejda.core.manipulation.model.parameter.SetMetadataParameters;
import org.sejda.core.manipulation.model.pdf.PdfMetadataKey;
import org.sejda.core.manipulation.model.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nero Couvalli
 *
 */
public class SetMetadataTask implements Task<SetMetadataParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(SetMetadataTask.class);

    private PDDocument document = null;

    public void before(SetMetadataParameters parameters) {
    }

    public void execute(SetMetadataParameters parameters) throws TaskException {
        PdfSource source = parameters.getSource();
        LOG.debug("Opening {} ...", source);
        document = loadPDDocument(source);

        LOG.debug("Setting metadata on temporary document.");

        PDDocumentInformation actualMeta = document.getDocumentInformation();
        for (Entry<PdfMetadataKey, String> meta : parameters.entrySet()) {
            actualMeta.setCustomMetadataValue(meta.getKey().getKey(), meta.getValue());
        }

        closePDDocumentQuitely(document);

        LOG.debug("Metadata set on {}", parameters.getOutput());

    }

    public void after() {
        closePDDocumentQuitely(document);
    }

}