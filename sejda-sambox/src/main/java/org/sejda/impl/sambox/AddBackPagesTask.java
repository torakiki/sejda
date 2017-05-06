/*
 * Created on 20 gen 2016
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

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;
import static org.sejda.impl.sambox.component.SignatureClipper.clipSignatures;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.sejda.common.LookupTable;
import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.impl.sambox.component.AnnotationsDistiller;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.AddBackPagesParameters;
import org.sejda.model.pdf.encryption.PdfAccessPermission;
import org.sejda.model.task.BaseTask;
import org.sejda.model.task.TaskExecutionContext;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A task that adds one or more back pages to an existing set of PDF documents every 'n' pages
 * 
 * @author Andrea Vacondio
 *
 */
public class AddBackPagesTask extends BaseTask<AddBackPagesParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(AddBackPagesTask.class);

    private int totalSteps;
    private PDDocumentHandler sourceDocumentHandler = null;
    private MultipleOutputWriter outputWriter;
    private PdfSourceOpener<PDDocumentHandler> documentLoader;
    private PDDocumentHandler backPagesSource;
    private PDDocumentHandler destinationDocument;

    @Override
    public void before(AddBackPagesParameters parameters, TaskExecutionContext executionContext) throws TaskException {
        super.before(parameters, executionContext);
        totalSteps = parameters.getSourceList().size();
        documentLoader = new DefaultPdfSourceOpener();
        outputWriter = OutputWriters.newMultipleOutputWriter(parameters.getExistingOutputPolicy(), executionContext);
    }

    @Override
    public void execute(AddBackPagesParameters parameters) throws TaskException {
        LOG.debug("Opening back pages source {}", parameters.getBackPagesSource());
        backPagesSource = parameters.getBackPagesSource().open(documentLoader);
        List<PDPage> back = parameters.getPages(backPagesSource.getNumberOfPages()).stream()
                .map(p -> backPagesSource.getPage(p)).collect(Collectors.toList());
        if (back.size() <= 0) {
            throw new TaskExecutionException("No back page was selected");
        }
        LOG.debug("Retrieved {} back pages", back.size());

        int currentStep = 0;
        for (PdfSource<?> source : parameters.getSourceList()) {
            executionContext().assertTaskNotCancelled();

            this.destinationDocument = new PDDocumentHandler();
            this.destinationDocument.setCreatorOnPDDocument();
            this.destinationDocument.setVersionOnPDDocument(parameters.getVersion());
            this.destinationDocument.setCompress(parameters.isCompress());

            LOG.debug("Opening {}", source);
            sourceDocumentHandler = source.open(documentLoader);
            sourceDocumentHandler.getPermissions().ensurePermission(PdfAccessPermission.ASSEMBLE);

            File tmpFile = createTemporaryBuffer(parameters.getOutput());
            LOG.debug("Created output on temporary buffer {}", tmpFile);

            int pageCounter = 0;
            LookupTable<PDPage> pagesLookup = new LookupTable<>();
            LOG.debug("Adding pages and back pages");
            for (PDPage current : sourceDocumentHandler.getPages()) {
                executionContext().assertTaskNotCancelled();
                pagesLookup.addLookupEntry(current, destinationDocument.importPage(current));
                pageCounter++;
                if (pageCounter % parameters.getStep() == 0) {
                    back.forEach(p -> destinationDocument.importPage(p));
                }
            }

            LookupTable<PDAnnotation> annotationsLookup = new AnnotationsDistiller(
                    sourceDocumentHandler.getUnderlyingPDDocument()).retainRelevantAnnotations(pagesLookup);
            clipSignatures(annotationsLookup.values());

            destinationDocument.savePDDocument(tmpFile);

            String outName = nameGenerator(parameters.getOutputPrefix())
                    .generate(nameRequest().originalName(source.getName()).fileNumber(currentStep));
            outputWriter.addOutput(file(tmpFile).name(outName));

            nullSafeCloseQuietly(destinationDocument);
            nullSafeCloseQuietly(sourceDocumentHandler);
            notifyEvent(executionContext().notifiableTaskMetadata()).stepsCompleted(++currentStep).outOf(totalSteps);
        }

        nullSafeCloseQuietly(backPagesSource);
        parameters.getOutput().accept(outputWriter);
        LOG.debug("Back pages added after every {} pages to {} input documents and written to {}", parameters.getStep(),
                parameters.getSourceList().size(), parameters.getOutput());
    }

    @Override
    public void after() {
        nullSafeCloseQuietly(backPagesSource);
        nullSafeCloseQuietly(destinationDocument);
        nullSafeCloseQuietly(sourceDocumentHandler);
    }
}
