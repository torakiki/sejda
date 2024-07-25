/*
 * Copyright 2015 Sober Lemur S.r.l. and Sejda BV.
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

import static org.sejda.commons.util.IOUtils.closeQuietly;
import static org.sejda.model.util.IOUtils.createTemporaryBuffer;

import java.io.File;

import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.io.SingleOutputWriter;
import org.sejda.impl.sambox.component.PdfAlternateMixer;
import org.sejda.model.exception.TaskException;
import org.sejda.model.parameter.AlternateMixMultipleInputParameters;
import org.sejda.model.task.BaseTask;
import org.sejda.model.task.TaskExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SAMBox implementation of the AlternateMix task performing the mix of two given {@link org.sejda.model.input.PdfMixInput}s.
 * 
 * @author Andrea Vacondio
 */
public class AlternateMixTask extends BaseTask<AlternateMixMultipleInputParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(AlternateMixTask.class);

    private PdfAlternateMixer mixer = null;
    private SingleOutputWriter outputWriter;

    @Override
    public void before(AlternateMixMultipleInputParameters parameters, TaskExecutionContext executionContext)
            throws TaskException {
        super.before(parameters, executionContext);
        mixer = new PdfAlternateMixer();
        outputWriter = OutputWriters.newSingleOutputWriter(parameters.getExistingOutputPolicy(), executionContext);
    }

    @Override
    public void execute(AlternateMixMultipleInputParameters parameters) throws TaskException {

        LOG.debug("Starting alternate mix of {} input documents", parameters.getInputList().size());
        mixer.mix(parameters.getInputList(), executionContext());
        mixer.setVersionOnPDDocument(parameters.getVersion());
        mixer.setCompress(parameters.isCompress());

        File tmpFile = createTemporaryBuffer(parameters.getOutput());
        outputWriter.taskOutput(tmpFile);
        LOG.debug("Temporary output set to {}", tmpFile);
        mixer.savePDDocument(tmpFile, parameters.getOutput().getEncryptionAtRestPolicy());
        closeQuietly(mixer);

        parameters.getOutput().accept(outputWriter);

        LOG.debug("Alternate mix of {} files completed", parameters.getInputList().size());
    }

    @Override
    public void after() {
        closeQuietly(mixer);
    }

}
