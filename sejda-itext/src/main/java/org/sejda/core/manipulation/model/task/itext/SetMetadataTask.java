/*
 * Created on 09/lug/2010
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
package org.sejda.core.manipulation.model.task.itext;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;

import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.model.input.PdfSource;
import org.sejda.core.manipulation.model.parameter.SetMetadataParameters;
import org.sejda.core.manipulation.model.pdf.PdfMetadataKey;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.manipulation.model.task.itext.component.PdfStamperHandler;
import org.sejda.core.support.io.SingleOutputWriterSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.pdf.PdfReader;

import static org.sejda.core.manipulation.model.task.itext.util.ITextUtils.nullSafeClosePdfReader;
import static org.sejda.core.manipulation.model.task.itext.util.ITextUtils.nullSafeClosePdfStamperHandler;
import static org.sejda.core.manipulation.model.task.itext.util.PdfReaderUtils.openReader;

import static org.sejda.core.support.io.model.FileOutput.file;

/**
 * Task setting metadata on an input {@link PdfSource}.
 * 
 * @author Andrea Vacondio
 * 
 */
public class SetMetadataTask extends SingleOutputWriterSupport implements Task<SetMetadataParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(SetMetadataTask.class);

    private PdfReader reader = null;
    private PdfStamperHandler stamperHandler = null;

    public void before(SetMetadataParameters parameters) throws TaskException {
        // nothing to do
    }

    public void execute(SetMetadataParameters parameters) throws TaskException {
        PdfSource source = parameters.getSource();
        LOG.debug("Opening {} ...", source);
        reader = openReader(source);

        File tmpFile = createTemporaryPdfBuffer();
        LOG.debug("Created output on temporary buffer {} ...", tmpFile);
        stamperHandler = new PdfStamperHandler(reader, tmpFile, parameters.getVersion());

        stamperHandler.setCompressionOnStamper(parameters.isCompressXref());

        LOG.debug("Setting metadata on temporary document.");
        @SuppressWarnings("unchecked")
        HashMap<String, String> actualMeta = reader.getInfo();
        for (Entry<PdfMetadataKey, String> meta : parameters.entrySet()) {
            actualMeta.put(meta.getKey().getKey(), meta.getValue());
        }
        stamperHandler.setMetadataOnStamper(actualMeta);

        nullSafeClosePdfReader(reader);
        nullSafeClosePdfStamperHandler(stamperHandler);

        flushSingleOutput(file(tmpFile).name(source.getName()), parameters.getOutput(), parameters.isOverwrite());

        LOG.debug("Metadata set on {}", parameters.getOutput());

    }

    public void after() {
        nullSafeClosePdfReader(reader);
        nullSafeClosePdfStamperHandler(stamperHandler);
    }

}
