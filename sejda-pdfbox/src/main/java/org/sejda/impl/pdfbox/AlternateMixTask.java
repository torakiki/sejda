/*
 * Created on 15/set/2011
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

import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.impl.pdfbox.component.PDDocumentHandler.nullSafeClose;

import java.io.File;

import org.sejda.core.exception.TaskException;
import org.sejda.core.exception.TaskIOException;
import org.sejda.core.manipulation.model.parameter.AlternateMixParameters;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.support.io.SingleOutputWriterSupport;
import org.sejda.impl.pdfbox.component.PdfAlternateMixer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PDFBox implementation of the AlternateMix task performing the mix of two given {@link org.sejda.core.manipulation.model.input.PdfMixInput}s.
 * 
 * @author Andrea Vacondio
 * 
 */
public class AlternateMixTask implements Task<AlternateMixParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(AlternateMixTask.class);

    private PdfAlternateMixer mixer = null;
    private SingleOutputWriterSupport outputWriter;

    public void before(AlternateMixParameters parameters) throws TaskIOException {
        outputWriter = new SingleOutputWriterSupport();
        mixer = new PdfAlternateMixer(parameters.getFirstInput(), parameters.getSecondInput());
    }

    public void execute(AlternateMixParameters parameters) throws TaskException {

        mixer.mix();
        mixer.setVersionOnPDDocument(parameters.getVersion());
        mixer.compressXrefStream(parameters.isCompressXref());

        File tmpFile = outputWriter.createTemporaryPdfBuffer();
        LOG.debug("Created output temporary buffer {}", tmpFile);
        mixer.saveDecryptedPDDocument(tmpFile);

        outputWriter.flushSingleOutput(file(tmpFile).name(parameters.getOutputName()), parameters.getOutput(),
                parameters.isOverwrite());

        LOG.debug("Alternate mix with step first document {} and step second document {} completed.", parameters
                .getFirstInput().getStep(), parameters.getSecondInput().getStep());
    }

    public void after() {
        nullSafeClose(mixer);
    }

}
