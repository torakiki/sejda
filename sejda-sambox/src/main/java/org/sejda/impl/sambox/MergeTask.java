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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.sejda.commons.LookupTable;
import org.sejda.model.util.IOUtils;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.io.SingleOutputWriter;
import org.sejda.impl.sambox.component.AcroFormsMerger;
import org.sejda.impl.sambox.component.AnnotationsDistiller;
import org.sejda.impl.sambox.component.CatalogPageLabelsMerger;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.FilenameFooterWriter;
import org.sejda.impl.sambox.component.OutlineMerger;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.impl.sambox.component.PdfRotator;
import org.sejda.impl.sambox.component.PdfScaler;
import org.sejda.impl.sambox.component.TableOfContentsCreator;
import org.sejda.impl.sambox.component.image.ImagesToPdfDocumentConverter;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfMergeInput;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.MergeParameters;
import org.sejda.model.rotation.Rotation;
import org.sejda.model.scale.ScaleType;
import org.sejda.model.task.BaseTask;
import org.sejda.model.task.TaskExecutionContext;
import org.sejda.model.toc.ToCPolicy;
import org.sejda.sambox.pdmodel.PDDocumentInformation;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PageNotFoundException;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import static java.util.Optional.ofNullable;
import static org.sejda.commons.util.IOUtils.closeQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.impl.sambox.component.SignatureClipper.clipSignatures;

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
    private CatalogPageLabelsMerger catalogPageLabelsMerger;
    private AcroFormsMerger acroFormsMerger;
    private TableOfContentsCreator tocCreator;
    private FilenameFooterWriter footerWriter;
    private PDRectangle currentPageSize = PDRectangle.A4;
    private int pagesCounter = 0;
    private int inputsCounter = 0;
    private int firstInputNumberOfPages = 0;

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
        File tmpFile = IOUtils.createTemporaryBuffer(parameters.getOutput());
        outputWriter.taskOutput(tmpFile);
        LOG.debug("Temporary output set to {}", tmpFile);

        this.destinationDocument = new PDDocumentHandler();
        this.destinationDocument.setCreatorOnPDDocument();
        this.destinationDocument.setVersionOnPDDocument(parameters.getVersion());
        this.destinationDocument.setCompress(parameters.isCompress());
        this.acroFormsMerger = new AcroFormsMerger(parameters.getAcroFormPolicy(),
                this.destinationDocument.getUnderlyingPDDocument());
        this.tocCreator = new TableOfContentsCreator(parameters, this.destinationDocument.getUnderlyingPDDocument());
        this.footerWriter = new FilenameFooterWriter(parameters.isFilenameFooter(),
                this.destinationDocument.getUnderlyingPDDocument());
        this.catalogPageLabelsMerger = new CatalogPageLabelsMerger(parameters.getCatalogPageLabelsPolicy());

        ImagesToPdfDocumentConverter.convertImageMergeInputToPdf(parameters, executionContext());

        List<FooterWriterEntry> footerWriterEntries = new ArrayList<>();

        for (PdfMergeInput input : parameters.getPdfInputList()) {
            inputsCounter++;
            LOG.debug("Opening {}", input.getSource());
            executionContext().notifiableTaskMetadata().setCurrentSource(input.getSource());
            PDDocumentHandler sourceDocumentHandler = input.getSource().open(sourceOpener);
            toClose.add(sourceDocumentHandler);

            if (inputsCounter == 1) {
                firstInputNumberOfPages = sourceDocumentHandler.getNumberOfPages();
            }

            LOG.debug("Adding pages");
            LookupTable<PDPage> pagesLookup = new LookupTable<>();
            long relativePagesCounter = 0;
            Set<Integer> pagesToImport = input.getPages(sourceDocumentHandler.getNumberOfPages());
            for (Integer currentPage : pagesToImport) {
                pagesCounter++;
                relativePagesCounter++;
                try {
                    PDPage page = sourceDocumentHandler.getPage(currentPage);
                    // we keep rotation into account
                    currentPageSize = page.getMediaBox().rotate(page.getRotation());
                    // we don't use the original page because once added to the new tree we loose inheritable attributes
                    // so we use a page duplicate to explicitly assign inheritable resources
                    PDPage importedPage = destinationDocument.importPage(page);
                    pagesLookup.addLookupEntry(page, importedPage);

                    // rotate
                    Rotation rotation = parameters.getRotation(inputsCounter - 1);
                    if (rotation != Rotation.DEGREES_0) {
                        PdfRotator.rotate(importedPage, rotation);
                    }

                    String sourceBaseName = FilenameUtils.getBaseName(input.getSource().getName());

                    // processing the first page of the source
                    if (tocCreator.shouldGenerateToC() && relativePagesCounter == 1) {
                        if (parameters.isFirstInputCoverTitle() && inputsCounter == 1) {
                            // skip the cover/title document, don't add it to the ToC
                        } else {
                            tocCreator.pageSizeIfNotSet(currentPageSize);
                            String tocText = sourceBaseName;
                            if (ToCPolicy.DOC_TITLES == parameters.getTableOfContentsPolicy()) {
                                tocText = ofNullable(
                                        sourceDocumentHandler.getUnderlyingPDDocument().getDocumentInformation()).map(
                                                PDDocumentInformation::getTitle).filter(StringUtils::isNotBlank)
                                                .orElse(sourceBaseName);
                            }
                            tocCreator.appendItem(tocText, pagesCounter, importedPage);
                        }
                    }

                    boolean isPlacedAfterToc = true;
                    if (parameters.isFirstInputCoverTitle() && inputsCounter == 1) {
                        // the toc will be added after the cover/title pages
                        isPlacedAfterToc = false;
                    }

                    // we can determine the correct final page numbers only after the ToC has been generated
                    // otherwise we don't know how many pages the ToC consists of
                    // queue up footer writer items for after ToC generation
                    footerWriterEntries.add(new FooterWriterEntry(importedPage, sourceBaseName, pagesCounter, isPlacedAfterToc));
                    
                    LOG.trace("Added imported page");
                } catch (PageNotFoundException e) {
                    executionContext().assertTaskIsLenient(e);
                    notifyEvent(executionContext().notifiableTaskMetadata())
                            .taskWarning(String.format("Page %d was skipped, could not be processed", currentPage), e);
                }
            }
            
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

            catalogPageLabelsMerger.add(sourceDocumentHandler.getUnderlyingPDDocument(), pagesToImport);

            notifyEvent(executionContext().notifiableTaskMetadata()).stepsCompleted(++currentStep).outOf(totalSteps);
        }

        executionContext().notifiableTaskMetadata().clearCurrentSource();

        if (outlineMerger.hasOutline()) {
            LOG.debug("Adding generated outline");
            destinationDocument.setDocumentOutline(outlineMerger.getOutline());
        }

        ofNullable(acroFormsMerger.getForm()).filter(f -> !f.getFields().isEmpty()).ifPresent(f -> {
            LOG.debug("Adding generated AcroForm");
            destinationDocument.setDocumentAcroForm(f);
        });

        if (parameters.isNormalizePageSizes()) {
            LOG.debug("Normalizing page widths to match width of first page");
            // Do this before generating TOC, so the first page is from content.
            new PdfScaler(ScaleType.PAGE).scalePages(destinationDocument.getUnderlyingPDDocument());
        }

        int tocNumberOfPages = 0;
        if (tocCreator.hasToc()) {
            LOG.debug("Adding generated ToC");
            try {
                // add ToC as first page or after the cover/title pages
                int beforePageNumber = parameters.isFirstInputCoverTitle() ? firstInputNumberOfPages : 0;
                tocNumberOfPages = tocCreator.addToC(beforePageNumber);
            } catch (TaskException e) {
                notifyEvent(executionContext().notifiableTaskMetadata())
                        .taskWarning("Unable to create the Table of Contents", e);
            }
        }
        
        LOG.debug("Writing page footers");
        for(FooterWriterEntry entry: footerWriterEntries) {
            long finalPageNumber = entry.isPlacedAfterToc ? entry.pageNumber + tocNumberOfPages : entry.pageNumber;
            this.footerWriter.addFooter(entry.page, entry.fileName, finalPageNumber);
        }

        if (catalogPageLabelsMerger.hasPageLabels()) {
            LOG.debug("Adding merged /Catalog /PageLabels");
            destinationDocument.getUnderlyingPDDocument().getDocumentCatalog()
                    .setPageLabels(catalogPageLabelsMerger.getMergedPageLabels());
        }

        destinationDocument.savePDDocument(tmpFile, parameters.getOutput().getEncryptionAtRestPolicy());
        closeResources();

        parameters.getOutput().accept(outputWriter);
        LOG.debug("Input documents merged correctly and written to {}", parameters.getOutput());

    }

    private void closeResources() {
        Closeable current;
        while ((current = toClose.poll()) != null) {
            closeQuietly(current);
        }
        closeQuietly(destinationDocument);
    }

    @Override
    public void after() {
        closeResources();
        outputWriter = null;
    }
    
    private static class FooterWriterEntry {
        final PDPage page; 
        final String fileName; 
        final long pageNumber;
        final boolean isPlacedAfterToc;

        public FooterWriterEntry(PDPage page, String fileName, long pageNumber, boolean isPlacedAfterToc) {
            this.page = page;
            this.fileName = fileName;
            this.pageNumber = pageNumber;
            this.isPlacedAfterToc = isPlacedAfterToc;
        }
    }
}
