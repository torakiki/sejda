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

import static java.util.Optional.ofNullable;
import static org.sejda.commons.util.IOUtils.closeQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.io.SingleOutputWriter;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.SetMetadataParameters;
import org.sejda.model.task.BaseTask;
import org.sejda.model.task.TaskExecutionContext;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDDocumentInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SAMBox implementation of a task setting metadata on an input {@link PdfSource}.
 * 
 * @author Eduard Weissmann
 * 
 */
public class SetMetadataTask extends BaseTask<SetMetadataParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(SetMetadataTask.class);

    private PDDocumentHandler documentHandler = null;
    private MultipleOutputWriter outputWriter;
    private PdfSourceOpener<PDDocumentHandler> documentLoader;

    @Override
    public void before(SetMetadataParameters parameters, TaskExecutionContext executionContext) throws TaskException {
        super.before(parameters, executionContext);
        documentLoader = new DefaultPdfSourceOpener();
        outputWriter = OutputWriters.newMultipleOutputWriter(parameters.getExistingOutputPolicy(), executionContext);
    }

    @Override
    public void execute(SetMetadataParameters parameters) throws TaskException {
        int totalSteps = parameters.getSourceList().size();

        for (int sourceIndex = 0; sourceIndex < parameters.getSourceList().size(); sourceIndex++) {
            PdfSource<?> source = parameters.getSourceList().get(sourceIndex);
            int fileNumber = executionContext().incrementAndGetOutputDocumentsCounter();
            
            try {
                LOG.debug("Opening {}", source);

                documentHandler = source.open(documentLoader);
                documentHandler.setCreatorOnPDDocument();

                File tmpFile = createTemporaryBuffer(parameters.getOutput());

                PDDocument doc = documentHandler.getUnderlyingPDDocument();
                doc.setOnBeforeWriteAction(new PDDocument.OnBeforeWrite() {
                    @Override
                    public void onBeforeWrite() throws IOException {
                        LOG.debug("Setting metadata on temporary document.");
                        PDDocumentInformation actualMeta = documentHandler.getUnderlyingPDDocument().getDocumentInformation();
                        for (Entry<String, String> meta : parameters.getMetadata().entrySet()) {
                            LOG.trace("'{}' -> '{}'", meta.getKey(), meta.getValue());
                            actualMeta.setCustomMetadataValue(meta.getKey(), meta.getValue());
                        }

                        for (String keyToRemove : parameters.getToRemove()) {
                            LOG.trace("Removing '{}'", keyToRemove);
                            actualMeta.removeMetadataField(keyToRemove);
                        }
                    }
                });

                documentHandler.setVersionOnPDDocument(parameters.getVersion());
                documentHandler.setCompress(parameters.isCompress());
                documentHandler.savePDDocument(tmpFile, parameters.getOutput().getEncryptionAtRestPolicy());

                String outName = ofNullable(parameters.getSpecificResultFilename(fileNumber)).orElseGet(() -> {
                    return nameGenerator(parameters.getOutputPrefix())
                            .generate(nameRequest().originalName(source.getName()).fileNumber(fileNumber));
                });

                outputWriter.addOutput(file(tmpFile).name(outName));
                
            } finally {
                closeQuietly(documentHandler);
            }

            notifyEvent(executionContext().notifiableTaskMetadata()).stepsCompleted(fileNumber).outOf(totalSteps);
        }

        parameters.getOutput().accept(outputWriter);
        LOG.debug("Metadata set and written to {}", parameters.getOutput());

    }

    @Override
    public void after() {
        closeQuietly(documentHandler);
    }

}
