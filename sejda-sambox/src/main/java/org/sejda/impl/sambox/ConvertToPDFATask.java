package org.sejda.impl.sambox;
/*
 * Created on 29/05/24
 * Copyright 2024 Sober Lemur S.r.l. and Sejda BV
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

import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.impl.sambox.component.optimization.ResourceDictionaryCleaner;
import org.sejda.impl.sambox.component.pdfa.ConversionContext;
import org.sejda.impl.sambox.component.pdfa.Rules;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.parameter.ConvertToPDFAParameters;
import org.sejda.model.task.BaseTask;
import org.sejda.model.task.TaskExecutionContext;
import org.sejda.sambox.pdmodel.PDPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static java.util.Optional.ofNullable;
import static org.sejda.commons.util.IOUtils.closeQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;
import static org.sejda.impl.sambox.component.pdfa.Rules.contentStreamRules;
import static org.sejda.impl.sambox.component.pdfa.Rules.documentRules;
import static org.sejda.impl.sambox.component.pdfa.Rules.pageRules;
import static org.sejda.model.util.IOUtils.createTemporaryBuffer;

/**
 * Task to convert a list of PDF documents to PDF/A.
 *
 * @author Andrea Vacondio
 */
public class ConvertToPDFATask extends BaseTask<ConvertToPDFAParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(ConvertToPDFATask.class);

    private int totalSteps;
    private DefaultPdfSourceOpener documentLoader;
    private MultipleOutputWriter outputWriter;
    private PDDocumentHandler documentHandler;
    private ResourceDictionaryCleaner resourceCleaner;

    @Override
    public void before(ConvertToPDFAParameters parameters, TaskExecutionContext executionContext) throws TaskException {
        super.before(parameters, executionContext);
        totalSteps = parameters.getSourceList().size();
        documentLoader = new DefaultPdfSourceOpener(executionContext);
        outputWriter = OutputWriters.newMultipleOutputWriter(parameters.getExistingOutputPolicy(), executionContext);
        resourceCleaner = new ResourceDictionaryCleaner();
    }

    @Override
    public void execute(ConvertToPDFAParameters parameters) throws TaskException {
        for (PdfSource<?> source : parameters.getSourceList()) {
            int fileNumber = executionContext().incrementAndGetOutputDocumentsCounter();

            LOG.debug("Opening {}", source);
            executionContext().notifiableTaskMetadata().setCurrentSource(source);
            documentHandler = source.open(documentLoader);
            documentHandler.setCreatorOnPDDocument();

            File tmpFile = createTemporaryBuffer(parameters.getOutput());
            LOG.debug("Created output on temporary buffer {}", tmpFile);
            try {
                var context = new ConversionContext(parameters, executionContext().notifiableTaskMetadata());
                documentRules(context).accept(documentHandler.getUnderlyingPDDocument());
                for (PDPage page : documentHandler.getPages()) {
                    pageRules(context).accept(page);
                    contentStreamRules(context).accept(page);
                }

                context.maybeFixFontsWidths();
                resourceCleaner.accept(documentHandler.getUnderlyingPDDocument());

                documentHandler.setTransformer(Rules.preSaveCOSTransformer(context));
                documentHandler.setCompress(parameters.isCompress());
                documentHandler.savePDDocument(tmpFile, parameters.getOutput().getEncryptionAtRestPolicy());

                String outName = ofNullable(parameters.getSpecificResultFilename(fileNumber)).orElseGet(
                        () -> nameGenerator(parameters.getOutputPrefix()).generate(
                                nameRequest().originalName(source.getName()).fileNumber(fileNumber)));

                outputWriter.addOutput(file(tmpFile).name(outName));
            } catch (TaskException | IOException e) {
                executionContext().assertTaskIsLenient(e);
                notifyEvent(executionContext().notifiableTaskMetadata()).taskWarning("Conversion failed", e);
            }

            closeQuietly(documentHandler);
            notifyEvent(executionContext().notifiableTaskMetadata()).stepsCompleted(fileNumber).outOf(totalSteps);
        }
        //TODO task fails if no output
        executionContext().notifiableTaskMetadata().clearCurrentSource();
        parameters.getOutput().accept(outputWriter);
        LOG.debug("Input documents optimized and written to {}", parameters.getOutput());
    }

    @Override
    public void after() {
        closeQuietly(documentHandler);
    }
}
