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
package org.sejda.impl.itext;

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryPdfBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.impl.itext.util.ITextUtils.nullSafeClosePdfReader;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;

import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.io.SingleOutputWriter;
import org.sejda.impl.itext.component.PdfStamperHandler;
import org.sejda.impl.itext.component.input.PdfSourceOpeners;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.SetMetadataParameters;
import org.sejda.model.pdf.PdfMetadataKey;
import org.sejda.model.task.BaseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.pdf.PdfReader;

/**
 * Task setting metadata on an input {@link PdfSource}.
 * 
 * @author Andrea Vacondio
 * 
 */
public class SetMetadataTask extends BaseTask<SetMetadataParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(SetMetadataTask.class);

    private PdfReader reader = null;
    private PdfStamperHandler stamperHandler = null;
    private SingleOutputWriter outputWriter;
    private PdfSourceOpener<PdfReader> sourceOpener;

    public void before(SetMetadataParameters parameters) {
        sourceOpener = PdfSourceOpeners.newPartialReadOpener();
        outputWriter = OutputWriters.newSingleOutputWriter(parameters.isOverwrite());
    }

    public void execute(SetMetadataParameters parameters) throws TaskException {
        notifyEvent(getNotifiableTaskMetadata()).progressUndetermined();

        PdfSource<?> source = parameters.getSource();
        LOG.debug("Opening {} ", source);
        reader = source.open(sourceOpener);

        File tmpFile = createTemporaryPdfBuffer();
        LOG.debug("Created output temporary buffer {} ", tmpFile);
        stamperHandler = new PdfStamperHandler(reader, tmpFile, parameters.getVersion());

        stamperHandler.setCompressionOnStamper(parameters.isCompressXref());

        LOG.debug("Setting metadata on temporary document.");
        @SuppressWarnings("unchecked")
        HashMap<String, String> actualMeta = reader.getInfo();
        for (Entry<PdfMetadataKey, String> meta : parameters.entrySet()) {
            LOG.trace("'{}' -> '{}'", meta.getKey().getKey(), meta.getValue());
            actualMeta.put(meta.getKey().getKey(), meta.getValue());
        }
        stamperHandler.setMetadataOnStamper(actualMeta);

        nullSafeClosePdfReader(reader);
        nullSafeCloseQuietly(stamperHandler);

        outputWriter.setOutput(file(tmpFile).name(parameters.getOutputName()));
        parameters.getOutput().accept(outputWriter);

        LOG.debug("Metadata set on {}", parameters.getOutput());

    }

    public void after() {
        nullSafeClosePdfReader(reader);
        nullSafeCloseQuietly(stamperHandler);
    }

}
