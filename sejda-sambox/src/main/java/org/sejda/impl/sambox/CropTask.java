/*
 * Created on 12 dic 2015
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
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;
import static org.sejda.impl.sambox.component.Annotations.processAnnotations;
import static org.sejda.impl.sambox.component.SignatureClipper.clipSignatures;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.sejda.common.LookupTable;
import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.impl.sambox.component.AcroFormsMerger;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.CropParameters;
import org.sejda.model.task.BaseTask;
import org.sejda.model.task.TaskExecutionContext;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SAMBox implementation of the Crop task to set MEDIABOX and CROPBOX on an input document. This task allow multiple boxes on the same page, generating an output document that c
 * 
 * @author Andrea Vacondio
 *
 */
public class CropTask extends BaseTask<CropParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(CropTask.class);

    private PDDocumentHandler sourceDocumentHandler = null;
    private PDDocumentHandler destinationDocument = null;
    private MultipleOutputWriter outputWriter;
    private PdfSourceOpener<PDDocumentHandler> documentLoader;
    private LookupTable<PDPage> pagesLookup = new LookupTable<>();
    private AcroFormsMerger acroFormsMerger;

    @Override
    public void before(CropParameters parameters, TaskExecutionContext executionContext) throws TaskException {
        super.before(parameters, executionContext);
        documentLoader = new DefaultPdfSourceOpener();
        outputWriter = OutputWriters.newMultipleOutputWriter(parameters.getExistingOutputPolicy(), executionContext);
    }

    @Override
    public void execute(CropParameters parameters) throws TaskException {
        int currentStep = 0;
        int totalSteps = parameters.getSourceList().size();
        for (PdfSource<?> source : parameters.getSourceList()) {
            executionContext().assertTaskNotCancelled();

            currentStep++;

            LOG.debug("Opening {}", source);
            sourceDocumentHandler = source.open(documentLoader);

            File tmpFile = createTemporaryPdfBuffer();
            LOG.debug("Created output temporary buffer {}", tmpFile);
            this.destinationDocument = new PDDocumentHandler();
            destinationDocument.setVersionOnPDDocument(parameters.getVersion());
            LOG.debug("Done with version");
            destinationDocument.initialiseBasedOn(sourceDocumentHandler.getUnderlyingPDDocument());
            destinationDocument.setCompress(parameters.isCompress());
            LOG.debug("Done with init");

            this.acroFormsMerger = new AcroFormsMerger(parameters.getAcroFormPolicy(),
                    this.destinationDocument.getUnderlyingPDDocument());

            List<PDRectangle> cropAreas = parameters.getCropAreas().stream().map(r -> new PDRectangle(r.getLeft(),
                    r.getBottom(), r.getRight() - r.getLeft(), r.getTop() - r.getBottom())).collect(Collectors.toList());
            LOG.debug("Found {} crop boxes to apply", cropAreas.size());

            Set<Integer> excludedPages = parameters.getExcludedPages(sourceDocumentHandler.getNumberOfPages());
            int pageNum = 0;
            for (PDPage page : sourceDocumentHandler.getUnderlyingPDDocument().getPages()) {
                pageNum++;

                if(excludedPages.contains(pageNum)){
                    LOG.debug("Not cropping excluded page {}", pageNum);
                    PDPage newPage = destinationDocument.importPage(page);
                    pagesLookup.addLookupEntry(page, newPage);
                    continue;
                }

                for (PDRectangle box : cropAreas) {
                    executionContext().assertTaskNotCancelled();
                    box = unrotate(page, box);
                    PDPage newPage = destinationDocument.importPage(page);
                    pagesLookup.addLookupEntry(page, newPage);
                    newPage.setCropBox(box);
                    notifyEvent(executionContext().notifiableTaskMetadata()).stepsCompleted(++currentStep)
                            .outOf(totalSteps);
                }
            }
            LookupTable<PDAnnotation> annotations = processAnnotations(pagesLookup,
                    sourceDocumentHandler.getUnderlyingPDDocument());
            clipSignatures(annotations.values());

            acroFormsMerger.mergeForm(sourceDocumentHandler.getUnderlyingPDDocument().getDocumentCatalog().getAcroForm(),
                    annotations);

            if (acroFormsMerger.hasForm()) {
                LOG.debug("Adding generated AcroForm");
                destinationDocument.setDocumentAcroForm(acroFormsMerger.getForm());
            }

            destinationDocument.savePDDocument(tmpFile);
            nullSafeCloseQuietly(sourceDocumentHandler);

            String outName = nameGenerator(parameters.getOutputPrefix()).generate(
                    nameRequest().originalName(source.getName()).fileNumber(currentStep));
            outputWriter.addOutput(file(tmpFile).name(outName));

            notifyEvent(executionContext().notifiableTaskMetadata()).stepsCompleted(currentStep).outOf(totalSteps);
        }

        parameters.getOutput().accept(outputWriter);
        LOG.debug("Input documents cropped and written to {}", parameters.getOutput());
    }

    @Override
    public void after() {
        nullSafeCloseQuietly(sourceDocumentHandler);
        pagesLookup.clear();
        nullSafeCloseQuietly(destinationDocument);
    }

    private PDRectangle unrotate(PDPage page, PDRectangle rotated) {
        if (page.getRotation() == 90) {
            return new PDRectangle(page.getCropBox().getWidth() - rotated.getUpperRightY(), rotated.getLowerLeftX(),
                    rotated.getHeight(), rotated.getWidth());
        }
        if (page.getRotation() == 180) {
            return new PDRectangle(page.getCropBox().getWidth() - rotated.getUpperRightX(),
                    page.getCropBox().getHeight() - rotated.getUpperRightY(), rotated.getWidth(), rotated.getHeight());
        }
        if (page.getRotation() == 270) {
            return new PDRectangle(rotated.getLowerLeftY(), page.getCropBox().getHeight() - rotated.getUpperRightX(),
                    rotated.getHeight(), rotated.getWidth());
        }
        return rotated;
    }
}
