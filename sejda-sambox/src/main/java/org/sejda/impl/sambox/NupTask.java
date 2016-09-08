/*
 * Copyright 2016 by Eduard Weissmann (edi.weissmann@gmail.com).
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

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryPdfBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;
import static org.sejda.impl.sambox.component.SignatureClipper.clipSignatures;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;

import org.sejda.common.LookupTable;
import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.impl.sambox.component.AcroFormsMerger;
import org.sejda.impl.sambox.component.AnnotationsDistiller;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.impl.sambox.component.PageToFormXObject;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.nup.PageOrder;
import org.sejda.model.parameter.NupParameters;
import org.sejda.model.pdf.form.AcroFormPolicy;
import org.sejda.model.task.BaseTask;
import org.sejda.model.task.TaskExecutionContext;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PDPageContentStream;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.graphics.form.PDFormXObject;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
import org.sejda.sambox.util.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NupTask extends BaseTask<NupParameters> {
    private static final Logger LOG = LoggerFactory.getLogger(NupTask.class);

    private PDDocumentHandler sourceDocumentHandler = null;
    private PDDocumentHandler destinationDocument = null;
    private MultipleOutputWriter outputWriter;
    private PdfSourceOpener<PDDocumentHandler> documentLoader;
    private LookupTable<PDPage> pagesLookup = new LookupTable<>();
    private AcroFormsMerger acroFormsMerger;

    @Override
    public void before(NupParameters parameters, TaskExecutionContext executionContext) throws TaskException {
        super.before(parameters, executionContext);
        documentLoader = new DefaultPdfSourceOpener();
        outputWriter = OutputWriters.newMultipleOutputWriter(parameters.getExistingOutputPolicy(), executionContext);
    }

    @Override
    public void execute(NupParameters parameters) throws TaskException {
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
            destinationDocument.initialiseBasedOn(sourceDocumentHandler.getUnderlyingPDDocument());
            destinationDocument.setCompress(parameters.isCompress());

            this.acroFormsMerger = new AcroFormsMerger(AcroFormPolicy.MERGE_RENAMING_EXISTING_FIELDS,
                    this.destinationDocument.getUnderlyingPDDocument());

            int numberOfPages = sourceDocumentHandler.getNumberOfPages();

            int documentRotation = sourceDocumentHandler.getPage(1).getRotation();
            for (int i = 1; i <= numberOfPages; i++) {
                int pageRotation = sourceDocumentHandler.getPage(i).getRotation();
                if (pageRotation != documentRotation) {
                    documentRotation = 0;
                    break;
                }
            }

            if (documentRotation != 0) {
                LOG.debug("Document rotation is " + documentRotation);
            }

            int n = parameters.getN();
            // only supports n = power of 2 at the moment
            int pow = (int) (Math.log(n) / Math.log(2));

            // calculate new sizes
            PDRectangle pageSize = sourceDocumentHandler.getPage(1).getMediaBox();
            if (documentRotation == 90 || documentRotation == 270) {
                // Take initial document rotation into account
                pageSize = pageSize.rotate(documentRotation);
            }

            PDRectangle newSize = new PDRectangle(pageSize.getWidth(), pageSize.getHeight());

            int columns = 1;
            int rows = 1;

            for (int i = 0; i < pow; i++) {
                // Eg: two portrait A4's fit on a landscape A3
                boolean landscape = newSize.getWidth() > newSize.getHeight();

                if (landscape) {
                    rows = rows * 2;
                    newSize = new PDRectangle(newSize.getWidth(), newSize.getHeight() * 2);
                } else {
                    columns = columns * 2;
                    newSize = new PDRectangle(newSize.getWidth() * 2, newSize.getHeight());
                }

                LOG.debug(String.format("Landscape? %s, cols: %s, rows: %s, size: %s x %s", landscape, columns, rows,
                        newSize.getWidth(), newSize.getHeight()));
            }

            if (parameters.isPreservePageSize()) {
                boolean landscape = newSize.getWidth() > newSize.getHeight();
                newSize = new PDRectangle(pageSize.getWidth(), pageSize.getHeight());
                boolean originalLandscape = pageSize.getWidth() > pageSize.getHeight();
                if (landscape && !originalLandscape) {
                    newSize = newSize.rotate(90);
                }
            }

            try {
                int currentRow = 0;
                int currentColumn = 0;

                PDPage currentPage = destinationDocument.addBlankPage(newSize);

                LOG.debug("Original page size: " + pageSize.getWidth() + "x" + pageSize.getHeight()
                        + ", new page size: " + newSize.getWidth() + "x" + newSize.getHeight() + ", columns: " + columns
                        + " rows: " + rows);

                for (int i = 1; i <= numberOfPages; i++) {

                    PDFormXObject pageAsFormObject = new PageToFormXObject().apply(sourceDocumentHandler.getPage(i));
                    float xOffset = pageSize.getWidth() * currentColumn;
                    float yOffset = newSize.getHeight() - (pageSize.getHeight() * (currentRow + 1));
                    float xScale = 1.0f;

                    if (parameters.isPreservePageSize()) {
                        xOffset = newSize.getWidth() / columns * currentColumn;
                        yOffset = newSize.getHeight() - (newSize.getHeight() / rows * (currentRow + 1));
                        xScale = (newSize.getWidth() / columns) / pageSize.getWidth();
                    }

                    LOG.debug("Column: " + currentColumn + ", row: " + currentRow + ", xOffset: " + xOffset
                            + " yOffset: " + yOffset + " xScale: " + xScale);

                    if (pageAsFormObject != null) {
                        PDPageContentStream currentContentStream = new PDPageContentStream(
                                destinationDocument.getUnderlyingPDDocument(), currentPage,
                                PDPageContentStream.AppendMode.APPEND, true, true);
                        AffineTransform at = new AffineTransform();
                        at.translate(xOffset, yOffset);
                        at.scale(xScale, xScale);

                        Matrix matrix = new Matrix(at);

                        currentContentStream.transform(matrix);
                        currentContentStream.drawForm(pageAsFormObject);
                        currentContentStream.close();
                    }

                    if (parameters.getPageOrder() == PageOrder.HORIZONTAL) {
                        // increment column
                        currentColumn += 1;
                        if (currentColumn >= columns) {
                            currentColumn = 0;
                            currentRow += 1;
                        }

                        // increment row if required, moving to next page
                        if (currentRow >= rows && i != numberOfPages) {
                            currentRow = 0;
                            currentColumn = 0;
                            currentPage = destinationDocument.addBlankPage(newSize);
                        }
                    } else if (parameters.getPageOrder() == PageOrder.VERTICAL) {
                        // increment row
                        currentRow += 1;
                        if (currentRow >= rows) {
                            currentRow = 0;
                            currentColumn += 1;
                        }

                        // increment column if required, moving to next page
                        if (currentColumn >= columns && i != numberOfPages) {
                            currentColumn = 0;
                            currentRow = 0;
                            currentPage = destinationDocument.addBlankPage(newSize);
                        }

                    }
                }

            } catch (IOException e) {
                throw new TaskException(e);
            }

            LookupTable<PDAnnotation> annotations = new AnnotationsDistiller(
                    sourceDocumentHandler.getUnderlyingPDDocument()).retainRelevantAnnotations(pagesLookup);
            clipSignatures(annotations.values());

            acroFormsMerger.mergeForm(
                    sourceDocumentHandler.getUnderlyingPDDocument().getDocumentCatalog().getAcroForm(), annotations);

            if (acroFormsMerger.hasForm()) {
                LOG.debug("Adding generated AcroForm");
                destinationDocument.setDocumentAcroForm(acroFormsMerger.getForm());
            }

            destinationDocument.savePDDocument(tmpFile);
            nullSafeCloseQuietly(sourceDocumentHandler);

            String outName = nameGenerator(parameters.getOutputPrefix())
                    .generate(nameRequest().originalName(source.getName()).fileNumber(currentStep));
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
}
