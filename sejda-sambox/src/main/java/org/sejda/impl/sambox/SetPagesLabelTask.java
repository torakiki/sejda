/*
 * Created on 18 dic 2015
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.io.SingleOutputWriter;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.SetPagesLabelParameters;
import org.sejda.model.task.BaseTask;
import org.sejda.model.task.TaskExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.sejda.commons.util.IOUtils.closeQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.model.util.IOUtils.createTemporaryBuffer;

/**
 * SAMBox implementation of a task that applies page labels to a given document
 * 
 * @author Andrea Vacondio
 *
 */
public class SetPagesLabelTask extends BaseTask<SetPagesLabelParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(SetPagesLabelTask.class);

    private PDDocumentHandler documentHandler = null;
    private SingleOutputWriter outputWriter;
    private PdfSourceOpener<PDDocumentHandler> documentLoader;

    @Override
    public void before(SetPagesLabelParameters parameters, TaskExecutionContext executionContext) throws TaskException {
        super.before(parameters, executionContext);
        documentLoader = new DefaultPdfSourceOpener(executionContext);
        outputWriter = OutputWriters.newSingleOutputWriter(parameters.getExistingOutputPolicy(), executionContext);
    }

    @Override
    public void execute(SetPagesLabelParameters parameters) throws TaskException {
        notifyEvent(executionContext().notifiableTaskMetadata()).progressUndetermined();

        PdfSource<?> source = parameters.getSource();
        LOG.debug("Opening {}", source);
        executionContext().notifiableTaskMetadata().setCurrentSource(source);

        documentHandler = source.open(documentLoader);

        File tmpFile = createTemporaryBuffer(parameters.getOutput());
        outputWriter.taskOutput(tmpFile);
        LOG.debug("Temporary output set to {}", tmpFile);

        LOG.debug("Applying {} labels ", parameters.getLabels().size());
        documentHandler.setPageLabelsOnDocument(parameters.getLabels());

        documentHandler.setCreatorOnPDDocument();
        documentHandler.setVersionOnPDDocument(parameters.getVersion());
        documentHandler.setCompress(parameters.isCompress());
        documentHandler.savePDDocument(tmpFile, parameters.getOutput().getEncryptionAtRestPolicy());
        closeQuietly(documentHandler);

        executionContext().notifiableTaskMetadata().clearCurrentSource();
        parameters.getOutput().accept(outputWriter);
        LOG.debug("Labels applied to {}", parameters.getOutput());
    }

    @Override
    public void after() {
        closeQuietly(documentHandler);
    }

}
