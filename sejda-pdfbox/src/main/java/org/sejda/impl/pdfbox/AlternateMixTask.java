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

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.support.io.IOUtils.createTemporaryPdfBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;

import java.io.File;

import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.io.SingleOutputWriter;
import org.sejda.impl.pdfbox.component.PdfAlternateMixer;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.parameter.AlternateMixParameters;
import org.sejda.model.task.BaseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PDFBox implementation of the AlternateMix task performing the mix of two given {@link org.sejda.model.input.PdfMixInput}s.
 * 
 * @author Andrea Vacondio
 * 
 */
public class AlternateMixTask extends BaseTask<AlternateMixParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(AlternateMixTask.class);

    private PdfAlternateMixer mixer = null;
    private SingleOutputWriter outputWriter;

    public void before(AlternateMixParameters parameters) throws TaskIOException {
        mixer = new PdfAlternateMixer(parameters.getFirstInput(), parameters.getSecondInput());
        outputWriter = OutputWriters.newSingleOutputWriter(parameters.isOverwrite());
    }

    public void execute(AlternateMixParameters parameters) throws TaskException {

        mixer.mix(getNotifiableTaskMetadata());
        mixer.setVersionOnPDDocument(parameters.getVersion());
        mixer.compressXrefStream(parameters.isCompress());

        File tmpFile = createTemporaryPdfBuffer();
        LOG.debug("Created output temporary buffer {}", tmpFile);
        mixer.saveDecryptedPDDocument(tmpFile);

        outputWriter.setOutput(file(tmpFile).name(parameters.getOutputName()));
        parameters.getOutput().accept(outputWriter);

        LOG.debug("Alternate mix with step first document {} and step second document {} completed.", parameters
                .getFirstInput().getStep(), parameters.getSecondInput().getStep());
    }

    public void after() {
        nullSafeCloseQuietly(mixer);
    }

}
