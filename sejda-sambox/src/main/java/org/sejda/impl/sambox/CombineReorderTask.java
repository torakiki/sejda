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

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryPdfBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.impl.sambox.component.Annotations.processAnnotations;
import static org.sejda.impl.sambox.component.SignatureClipper.clipSignatures;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.sejda.common.LookupTable;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.io.SingleOutputWriter;
import org.sejda.impl.sambox.component.AcroFormsMerger;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.FileIndexAndPage;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.CombineReorderParameters;
import org.sejda.model.task.BaseTask;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PageNotFoundException;
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
    private AcroFormsMerger acroFormsMerger;
    private LookupTable<PDPage> pagesLookup = new LookupTable<>();

    @Override
    public void before(CombineReorderParameters parameters) {
        sourceOpener = new DefaultPdfSourceOpener();
        outputWriter = OutputWriters.newSingleOutputWriter(parameters.getExistingOutputPolicy());

    }

    @Override
    public void execute(CombineReorderParameters parameters) throws TaskException {

        File tmpFile = createTemporaryPdfBuffer();
        LOG.debug("Created output temporary buffer {} ", tmpFile);

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
        }

        int currentStep = 0;
        int totalSteps = parameters.getPages().size() + documents.size();

        for (int i = 0; i < parameters.getPages().size(); i++) {
            stopTaskIfCancelled();

            FileIndexAndPage filePage = parameters.getPages().get(i);
            int pageNum = filePage.getPage();

            try {
                PDPage page = documents.get(filePage.getFileIndex()).getPage(pageNum);
                pagesLookup.addLookupEntry(page, destinationDocument.importPage(page));
            } catch (PageNotFoundException ex){
                String warning = String.format("Page %d was skipped, could not be processed", pageNum);
                notifyEvent(getNotifiableTaskMetadata()).taskWarning(warning);
                LOG.warn(warning, ex);
            }

            notifyEvent(getNotifiableTaskMetadata()).stepsCompleted(++currentStep).outOf(totalSteps);
        }

        for (PDDocumentHandler document : documents) {
            LookupTable<PDAnnotation> annotationsLookup = processAnnotations(pagesLookup,
                    document.getUnderlyingPDDocument());
            clipSignatures(annotationsLookup.values());

            acroFormsMerger.mergeForm(document.getUnderlyingPDDocument().getDocumentCatalog().getAcroForm(),
                    annotationsLookup);
            notifyEvent(getNotifiableTaskMetadata()).stepsCompleted(++currentStep).outOf(totalSteps);
        }

        if (acroFormsMerger.hasForm()) {
            LOG.debug("Adding generated AcroForm");
            destinationDocument.setDocumentAcroForm(acroFormsMerger.getForm());
        }

        destinationDocument.savePDDocument(tmpFile);
        closeResources();

        outputWriter.setOutput(file(tmpFile).name(parameters.getOutputName()));
        parameters.getOutput().accept(outputWriter);
        LOG.debug("Input documents merged correctly and written to {}", parameters.getOutput());
    }

    @Override
    public void after() {
        closeResources();
        outputWriter = null;
        documents.clear();
        pagesLookup.clear();
    }

    private void closeResources() {
        for (PDDocumentHandler document : documents) {
            nullSafeCloseQuietly(document);
        }
        nullSafeCloseQuietly(destinationDocument);
    }

}
