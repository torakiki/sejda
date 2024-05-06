/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com)
 *
 * This file is part of the Sejda source code
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox;

import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.impl.sambox.component.PdfRotator;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.RotateParameters;
import org.sejda.model.pdf.encryption.PdfAccessPermission;
import org.sejda.model.rotation.Rotation;
import org.sejda.model.task.BaseTask;
import org.sejda.model.task.TaskExecutionContext;
import org.sejda.sambox.pdmodel.PageNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static java.util.Optional.ofNullable;
import static org.sejda.commons.util.IOUtils.closeQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;
import static org.sejda.model.util.IOUtils.createTemporaryBuffer;

/**
 * SAMBox implementation of a task performing pages rotation on a list of {@link PdfSource}.
 * 
 * @author Eduard Weissmann
 * 
 */
public class RotateTask extends BaseTask<RotateParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(RotateTask.class);

    private int totalSteps;
    private PDDocumentHandler documentHandler = null;
    private MultipleOutputWriter outputWriter;
    private PdfSourceOpener<PDDocumentHandler> documentLoader;

    @Override
    public void before(RotateParameters parameters, TaskExecutionContext executionContext) throws TaskException {
        super.before(parameters, executionContext);
        totalSteps = parameters.getSourceList().size();
        documentLoader = new DefaultPdfSourceOpener(executionContext);
        outputWriter = OutputWriters.newMultipleOutputWriter(parameters.getExistingOutputPolicy(), executionContext);
    }

    @Override
    public void execute(RotateParameters parameters) throws TaskException {

        for (int sourceIndex = 0; sourceIndex < parameters.getSourceList().size(); sourceIndex++) {
            PdfSource<?> source = parameters.getSourceList().get(sourceIndex);

            int fileNumber = executionContext().incrementAndGetOutputDocumentsCounter();
            LOG.debug("Opening {}", source);
            executionContext().notifiableTaskMetadata().setCurrentSource(source);
            try {
                documentHandler = source.open(documentLoader);
                documentHandler.getPermissions().ensurePermission(PdfAccessPermission.ASSEMBLE);
                documentHandler.setCreatorOnPDDocument();

                File tmpFile = createTemporaryBuffer(parameters.getOutput());
                LOG.debug("Created output on temporary buffer {}", tmpFile);

                PdfRotator rotator = new PdfRotator(documentHandler.getUnderlyingPDDocument());
                for (int page = 1; page <= documentHandler.getNumberOfPages(); page++) {
                    Rotation rotation = parameters.getRotation(sourceIndex, page);

                    if (rotation != Rotation.DEGREES_0) {
                        try {
                            rotator.rotate(page, rotation);
                        } catch (PageNotFoundException e) {
                            String warningMessage = String.format("Page %d of %s was skipped, could not be rotated",
                                    page, source.getName());
                            executionContext().assertTaskIsLenient(e);
                            notifyEvent(executionContext().notifiableTaskMetadata()).taskWarning(warningMessage, e);
                        }
                    }
                }

                documentHandler.setVersionOnPDDocument(parameters.getVersion());
                documentHandler.setCompress(parameters.isCompress());
                documentHandler.savePDDocument(tmpFile, parameters.getOutput().getEncryptionAtRestPolicy());

                String outName = ofNullable(parameters.getSpecificResultFilename(fileNumber)).orElseGet(
                        () -> nameGenerator(parameters.getOutputPrefix()).generate(
                                nameRequest().originalName(source.getName()).fileNumber(fileNumber)));

                outputWriter.addOutput(file(tmpFile).name(outName));
            } finally {
                closeQuietly(documentHandler);
            }

            notifyEvent(executionContext().notifiableTaskMetadata()).stepsCompleted(fileNumber).outOf(totalSteps);
        }
        executionContext().notifiableTaskMetadata().clearCurrentSource();

        parameters.getOutput().accept(outputWriter);
        LOG.debug("Input documents rotated and written to {}", parameters.getOutput());
    }

    @Override
    public void after() {
        closeQuietly(documentHandler);
    }

}
