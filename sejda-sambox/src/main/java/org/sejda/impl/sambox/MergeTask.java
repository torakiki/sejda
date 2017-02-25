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

import static java.util.Optional.ofNullable;
import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryPdfBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.impl.sambox.component.SignatureClipper.clipSignatures;

import java.io.Closeable;
import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.sejda.common.LookupTable;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.io.SingleOutputWriter;
import org.sejda.impl.sambox.component.*;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfMergeInput;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.MergeParameters;
import org.sejda.model.scale.ScaleType;
import org.sejda.model.task.BaseTask;
import org.sejda.model.task.TaskExecutionContext;
import org.sejda.model.toc.ToCPolicy;
import org.sejda.sambox.cos.COSArray;
import org.sejda.sambox.cos.COSInteger;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PageNotFoundException;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDPageFitWidthDestination;
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
    private TableOfContentsCreator tocCreator;
    private FilenameFooterWriter footerWriter;
    private PDRectangle currentPageSize = PDRectangle.A4;
    private long pagesCounter = 0;

    @Override
    public void before(MergeParameters parameters, TaskExecutionContext executionContext) throws TaskException {
        super.before(parameters, executionContext);
        totalSteps = parameters.getInputList().size();
        sourceOpener = new DefaultPdfSourceOpener();
        outputWriter = OutputWriters.newSingleOutputWriter(parameters.getExistingOutputPolicy(), executionContext);
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
        this.tocCreator = new TableOfContentsCreator(parameters.getTableOfContentsPolicy(),
                this.destinationDocument.getUnderlyingPDDocument());
        this.footerWriter = new FilenameFooterWriter(parameters.isFilenameFooter(),
                this.destinationDocument.getUnderlyingPDDocument());

        for (PdfMergeInput input : parameters.getInputList()) {
            LOG.debug("Opening {}", input.getSource());
            PDDocumentHandler sourceDocumentHandler = input.getSource().open(sourceOpener);
            toClose.add(sourceDocumentHandler);

            LOG.debug("Adding pages");
            LookupTable<PDPage> pagesLookup = new LookupTable<>();
            long relativeCounter = 0;
            for (Integer currentPage : input.getPages(sourceDocumentHandler.getNumberOfPages())) {
                executionContext().assertTaskNotCancelled();
                pagesCounter++;
                relativeCounter++;
                try {
                    PDPage page = sourceDocumentHandler.getPage(currentPage);
                    // we keep rotation into account
                    currentPageSize = page.getMediaBox().rotate(page.getRotation());
                    // we don't use the original page because once added to the new tree we loose inheritable attributes
                    // so we use a page duplicate to explicitly assign inheritable resources
                    PDPage importedPage = destinationDocument.importPage(page);
                    pagesLookup.addLookupEntry(page, importedPage);

                    String sourceBaseName = FilenameUtils.getBaseName(input.getSource().getName());
                    // processing the first page of the source
                    if (tocCreator.shouldGenerateToC() && relativeCounter == 1) {
                        tocCreator.pageSizeIfNotSet(currentPageSize);
                        if (ToCPolicy.DOC_TITLES == parameters.getTableOfContentsPolicy()) {
                            sourceBaseName = ofNullable(
                                    sourceDocumentHandler.getUnderlyingPDDocument().getDocumentInformation())
                                            .map(i -> i.getTitle()).filter(StringUtils::isNotBlank)
                                            .orElse(sourceBaseName);
                        }
                        tocCreator.appendItem(sourceBaseName, pagesCounter, linkAnnotationFor(importedPage));
                    }

                    this.footerWriter.addFooter(importedPage, sourceBaseName, pagesCounter);
                    LOG.trace("Added imported page");
                } catch (PageNotFoundException e) {
                    executionContext().assertTaskIsLenient(e);
                    notifyEvent(executionContext().notifiableTaskMetadata())
                            .taskWarning(String.format("Page %d was skipped, could not be processed", currentPage), e);
                }
            }
            relativeCounter = 0;

            outlineMerger.updateOutline(sourceDocumentHandler.getUnderlyingPDDocument(), input.getSource().getName(),
                    pagesLookup);

            LookupTable<PDAnnotation> annotationsLookup = new AnnotationsDistiller(
                    sourceDocumentHandler.getUnderlyingPDDocument()).retainRelevantAnnotations(pagesLookup);
            clipSignatures(annotationsLookup.values());

            acroFormsMerger.mergeForm(
                    sourceDocumentHandler.getUnderlyingPDDocument().getDocumentCatalog().getAcroForm(),
                    annotationsLookup);

            if (parameters.isBlankPageIfOdd()) {
                ofNullable(destinationDocument.addBlankPageIfOdd(currentPageSize)).ifPresent(p -> pagesCounter++);
            }
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

        if(parameters.isNormalizePageSizes()) {
            LOG.debug("Normalizing page widths to match width of first page");
            // Do this before generating TOC, so the first page is from content.
            new PdfScaler(ScaleType.PAGE).resizePages(destinationDocument.getUnderlyingPDDocument());
        }

        if (tocCreator.hasToc()) {
            LOG.debug("Adding generated ToC");
            tocCreator.addToC(parameters.isBlankPageIfOdd());
        }

        destinationDocument.savePDDocument(tmpFile);
        closeResources();

        outputWriter.setOutput(file(tmpFile).name(parameters.getOutputName()));
        parameters.getOutput().accept(outputWriter);
        LOG.debug("Input documents merged correctly and written to {}", parameters.getOutput());

    }

    private PDAnnotationLink linkAnnotationFor(PDPage importedPage) {
        PDPageFitWidthDestination pageDest = new PDPageFitWidthDestination();
        pageDest.setPage(importedPage);
        PDAnnotationLink link = new PDAnnotationLink();
        link.setDestination(pageDest);
        link.setBorder(new COSArray(COSInteger.ZERO, COSInteger.ZERO, COSInteger.ZERO));
        return link;
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
