/*
 * Created on 17 dic 2015
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

import static java.util.Optional.ofNullable;
import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryBuffer;
import static org.sejda.impl.sambox.component.SignatureClipper.clipSignatures;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sejda.common.LookupTable;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.io.SingleOutputWriter;
import org.sejda.impl.sambox.component.*;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.FileIndexAndPage;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.CombineReorderParameters;
import org.sejda.model.task.BaseTask;
import org.sejda.model.task.TaskExecutionContext;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PageNotFoundException;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SAMBox implementation of a task that allows combining multiple pdf sources, allowing reordering of the pdf pages regardless of the ordering in the original sources.
 * 
 * @author Andrea Vacondio
 *
 */
public class CombineReorderTask extends BaseTask<CombineReorderParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(CombineReorderTask.class);

    private SingleOutputWriter outputWriter;
    private PdfSourceOpener<PDDocumentHandler> sourceOpener;
    private PDDocumentHandler destinationDocument;
    private List<PDDocumentHandler> documents = new ArrayList<>();
    private Map<PDDocumentHandler, String> documentNames = new HashMap<>();
    private AcroFormsMerger acroFormsMerger;
    private LookupTable<PDPage> pagesLookup = new LookupTable<>();
    private OutlineMerger outlineMerger;

    @Override
    public void before(CombineReorderParameters parameters, TaskExecutionContext executionContext)
            throws TaskException {
        super.before(parameters, executionContext);
        sourceOpener = new DefaultPdfSourceOpener();
        outputWriter = OutputWriters.newSingleOutputWriter(parameters.getExistingOutputPolicy(), executionContext);
        outlineMerger = new OutlineMerger(parameters.getOutlinePolicy());
    }

    @Override
    public void execute(CombineReorderParameters parameters) throws TaskException {

        File tmpFile = createTemporaryBuffer(parameters.getOutput());
        outputWriter.taskOutput(tmpFile);
        LOG.debug("Temporary output set to {}", tmpFile);

        this.destinationDocument = new PDDocumentHandler();
        this.destinationDocument.setCreatorOnPDDocument();
        this.destinationDocument.setVersionOnPDDocument(parameters.getVersion());
        this.destinationDocument.setCompress(parameters.isCompress());
        this.acroFormsMerger = new AcroFormsMerger(parameters.getAcroFormPolicy(),
                this.destinationDocument.getUnderlyingPDDocument());

        for (PdfSource<?> input : parameters.getSourceList()) {
            LOG.debug("Opening {}", input.getSource());
            PDDocumentHandler sourceDocumentHandler = input.open(sourceOpener);
            documents.add(sourceDocumentHandler);
            documentNames.put(sourceDocumentHandler, input.getName());
        }

        int currentStep = 0;
        int totalSteps = parameters.getPages().size() + documents.size();
        PdfRotator rotator = new PdfRotator(destinationDocument.getUnderlyingPDDocument());

        PDPage lastPage = null;

        for (int i = 0; i < parameters.getPages().size(); i++) {
            executionContext().assertTaskNotCancelled();

            FileIndexAndPage filePage = parameters.getPages().get(i);
            int pageNum = filePage.getPage();

            if (filePage.isAddBlankPage()) {
                PDRectangle mediaBox = PDRectangle.A4;
                if (lastPage != null) {
                    mediaBox = lastPage.getMediaBox();
                }
                destinationDocument.addBlankPage(mediaBox);
            } else {
                try {
                    PDPage page = documents.get(filePage.getFileIndex()).getPage(pageNum);
                    PDPage newPage = destinationDocument.importPage(page);
                    lastPage = newPage;
                    pagesLookup.addLookupEntry(page, newPage);
                    rotator.rotate(i + 1, filePage.getRotation());
                } catch (PageNotFoundException e) {
                    executionContext().assertTaskIsLenient(e);
                    notifyEvent(executionContext().notifiableTaskMetadata())
                            .taskWarning(String.format("Page %d was skipped, could not be processed", pageNum), e);
                }
            }

            notifyEvent(executionContext().notifiableTaskMetadata()).stepsCompleted(++currentStep).outOf(totalSteps);
        }

        for (PDDocumentHandler document : documents) {
            outlineMerger.updateOutline(document.getUnderlyingPDDocument(), documentNames.get(document), pagesLookup);

            LookupTable<PDAnnotation> annotationsLookup = new AnnotationsDistiller(document.getUnderlyingPDDocument())
                    .retainRelevantAnnotations(pagesLookup);
            clipSignatures(annotationsLookup.values());

            acroFormsMerger.mergeForm(document.getUnderlyingPDDocument().getDocumentCatalog().getAcroForm(),
                    annotationsLookup);
            notifyEvent(executionContext().notifiableTaskMetadata()).stepsCompleted(++currentStep).outOf(totalSteps);
        }

        if (outlineMerger.hasOutline()) {
            LOG.debug("Adding generated outline");
            destinationDocument.setDocumentOutline(outlineMerger.getOutline());
        }

        ofNullable(acroFormsMerger.getForm()).filter(f -> !f.getFields().isEmpty()).ifPresent(f -> {
            LOG.debug("Adding generated AcroForm");
            destinationDocument.setDocumentAcroForm(f);
        });

        destinationDocument.savePDDocument(tmpFile);
        closeResources();

        parameters.getOutput().accept(outputWriter);
        LOG.debug("Input documents merged correctly and written to {}", parameters.getOutput());
    }

    @Override
    public void after() {
        closeResources();
        outputWriter = null;
        documents.clear();
        documentNames.clear();
        pagesLookup.clear();
    }

    private void closeResources() {
        for (PDDocumentHandler document : documents) {
            nullSafeCloseQuietly(document);
        }
        nullSafeCloseQuietly(destinationDocument);
    }

}
