/*
 * Created on 03/set/2015
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

import java.io.Closeable;
import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

import org.sejda.common.LookupTable;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.io.SingleOutputWriter;
import org.sejda.impl.sambox.component.AcroFormsMerger;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.OutlineMerger;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfMergeInput;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.MergeParameters;
import org.sejda.model.task.BaseTask;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SAMBox implementation of the Merge task that merges together a number of documents or part of them.
 * 
 * @author Andrea Vacondio
 *
 */
public class MergeTask extends BaseTask<MergeParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(MergeTask.class);

    private SingleOutputWriter outputWriter;
    private PdfSourceOpener<PDDocumentHandler> sourceOpener;
    private int totalSteps;
    private PDDocumentHandler destinationDocument;
    private Queue<Closeable> toClose = new LinkedList<>();
    private OutlineMerger outlineMerger;
    private AcroFormsMerger acroFormsMerger;
    private PDRectangle currentPageSize = PDRectangle.LETTER;

    @Override
    public void before(MergeParameters parameters) {
        totalSteps = parameters.getInputList().size();
        sourceOpener = new DefaultPdfSourceOpener();
        outputWriter = OutputWriters.newSingleOutputWriter(parameters.getExistingOutputPolicy());
        outlineMerger = new OutlineMerger(parameters.getOutlinePolicy());
    }

    @Override
    public void execute(MergeParameters parameters) throws TaskException {
        int currentStep = 0;
        File tmpFile = createTemporaryPdfBuffer();
        LOG.debug("Created output temporary buffer {} ", tmpFile);

        this.destinationDocument = new PDDocumentHandler();
        this.destinationDocument.setCreatorOnPDDocument();
        this.destinationDocument.setVersionOnPDDocument(parameters.getVersion());
        this.destinationDocument.setCompress(parameters.isCompress());
        this.acroFormsMerger = new AcroFormsMerger(parameters.getAcroFormPolicy(),
                this.destinationDocument.getUnderlyingPDDocument());

        for (PdfMergeInput input : parameters.getInputList()) {
            LOG.debug("Opening {}", input.getSource());
            PDDocumentHandler sourceDocumentHandler = input.getSource().open(sourceOpener);
            toClose.add(sourceDocumentHandler);

            LOG.debug("Adding pages");
            LookupTable<PDPage> pagesLookup = new LookupTable<>();
            for (Integer currentPage : input.getPages(sourceDocumentHandler.getNumberOfPages())) {
                stopTaskIfCancelled();

                PDPage page = sourceDocumentHandler.getPage(currentPage);
                currentPageSize = page.getMediaBox();
                // we don't use the original page because once added to the new tree we loose inheritable attributes
                // so we use a page duplicate to explicitly assign inheritable resources
                pagesLookup.addLookupEntry(page, destinationDocument.importPage(page));
                LOG.trace("Added imported page");
            }

            outlineMerger.updateOutline(sourceDocumentHandler.getUnderlyingPDDocument(), input.getSource().getName(),
                    pagesLookup);

            LookupTable<PDAnnotation> annotationsLookup = processAnnotations(pagesLookup,
                    sourceDocumentHandler.getUnderlyingPDDocument());
            clipSignatures(annotationsLookup.values());

            acroFormsMerger.mergeForm(
                    sourceDocumentHandler.getUnderlyingPDDocument().getDocumentCatalog().getAcroForm(),
                    annotationsLookup);

            if (parameters.isBlankPageIfOdd()) {
                destinationDocument.addBlankPageIfOdd(currentPageSize);
            }
            notifyEvent(getNotifiableTaskMetadata()).stepsCompleted(++currentStep).outOf(totalSteps);
        }

        if (outlineMerger.hasOutline()) {
            LOG.debug("Adding generated outline");
            destinationDocument.setDocumentOutline(outlineMerger.getOutline());
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

    private void closeResources() {
        Closeable current;
        while ((current = toClose.poll()) != null) {
            nullSafeCloseQuietly(current);
        }
        nullSafeCloseQuietly(destinationDocument);
    }

    @Override
    public void after() {
        closeResources();
        outputWriter = null;
    }

}
