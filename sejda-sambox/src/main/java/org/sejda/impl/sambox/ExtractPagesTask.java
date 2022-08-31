/*
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox;

import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.impl.sambox.component.PagesExtractor;
import org.sejda.impl.sambox.component.optimization.OptimizationRuler;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.ExtractPagesParameters;
import org.sejda.model.pdf.encryption.PdfAccessPermission;
import org.sejda.model.task.BaseTask;
import org.sejda.model.task.TaskExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Set;

import static java.util.Optional.ofNullable;
import static org.sejda.commons.util.IOUtils.closeQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.model.util.IOUtils.createTemporaryBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

/**
 * SAMBox implementation of the task responsible for extracting pages from a given pdf document.
 *
 * @author Andrea Vacondio
 *
 */
public class ExtractPagesTask extends BaseTask<ExtractPagesParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(ExtractPagesTask.class);

    private MultipleOutputWriter outputWriter;
    private PdfSourceOpener<PDDocumentHandler> documentLoader;
    private PDDocumentHandler sourceDocumentHandler;

    @Override
    public void before(ExtractPagesParameters parameters, TaskExecutionContext executionContext) throws TaskException {
        super.before(parameters, executionContext);
        documentLoader = new DefaultPdfSourceOpener();
        outputWriter = OutputWriters.newMultipleOutputWriter(parameters.getExistingOutputPolicy(), executionContext);
    }

    @Override
    public void execute(ExtractPagesParameters parameters) throws TaskException {
        int currentStep = 0;
        int totalSteps = parameters.getSourceList().size();

        for (PdfSource<?> source : parameters.getSourceList()) {

            LOG.debug("Opening {}", source);
            executionContext().notifiableTaskMetadata().setCurrentSource(source);
            sourceDocumentHandler = source.open(documentLoader);
            sourceDocumentHandler.getPermissions().ensurePermission(PdfAccessPermission.ASSEMBLE);

            Set<Integer> pages = parameters.getPages(sourceDocumentHandler.getNumberOfPages());
            if (pages == null || pages.isEmpty()) {
                executionContext().assertTaskIsLenient(noPagesErrorMessage(source, parameters));
                notifyEvent(executionContext().notifiableTaskMetadata())
                        .taskWarning(noPagesErrorMessage(source, parameters));
            } else {
                LOG.debug("Extracting pages from {}, one file per range is '{}' ", source,
                        parameters.isSeparateFileForEachRange());
                try (PagesExtractor extractor = new PagesExtractor(sourceDocumentHandler.getUnderlyingPDDocument())) {
                    for (Set<Integer> pageSets : parameters.getPagesSets(sourceDocumentHandler.getNumberOfPages())) {
                        if (!pageSets.isEmpty()) {
                            File tmpFile = createTemporaryBuffer(parameters.getOutput());
                            LOG.debug("Created output temporary buffer {}", tmpFile);

                            int fileNumber = executionContext().incrementAndGetOutputDocumentsCounter();

                            String outName = ofNullable(parameters.getSpecificResultFilename(fileNumber))
                                    .orElseGet(() -> {
                                        return nameGenerator(parameters.getOutputPrefix())
                                                .generate(nameRequest().originalName(source.getName())
                                                        .fileNumber(fileNumber).page(pageSets.iterator().next()));
                                    });

                            outputWriter.addOutput(file(tmpFile).name(outName));

                            LOG.trace("Extracting pages {}", pageSets);
                            extractor.retain(pageSets, executionContext());
                            if (new OptimizationRuler(parameters.getOptimizationPolicy())
                                    .apply(sourceDocumentHandler.getUnderlyingPDDocument())) {
                                extractor.optimize();
                            }
                            extractor.setVersion(parameters.getVersion());
                            extractor.setCompress(parameters.isCompress());
                            extractor.save(tmpFile, parameters.discardOutline(),
                                    parameters.getOutput().getEncryptionAtRestPolicy());
                            extractor.reset();
                        }
                    }
                }
            }

            if (executionContext().outputDocumentsCounter() == 0) {
                throw new TaskException("The task didn't generate any output file");
            }

            closeQuietly(sourceDocumentHandler);
            notifyEvent(executionContext().notifiableTaskMetadata()).stepsCompleted(++currentStep).outOf(totalSteps);
        }
        executionContext().notifiableTaskMetadata().clearCurrentSource();
        parameters.getOutput().accept(outputWriter);
        LOG.debug("Pages extracted and written to {}", parameters.getOutput());
    }

    @Override
    public void after() {
        closeResource();
    }

    private void closeResource() {
        closeQuietly(sourceDocumentHandler);
    }

    private String noPagesErrorMessage(PdfSource<?> source, ExtractPagesParameters parameters) {
        if (parameters.isInvertSelection()) {
            return String.format("Document had all pages removed: %s", source.getName());
        }
        return String.format("No page has been selected for extraction from: %s", source.getName());
    }

}
