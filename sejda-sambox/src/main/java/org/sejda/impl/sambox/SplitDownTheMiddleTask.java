/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com).
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox;

import static java.util.Optional.ofNullable;
import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;
import static org.sejda.impl.sambox.component.SignatureClipper.clipSignatures;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sejda.common.LookupTable;
import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.impl.sambox.component.*;
import org.sejda.impl.sambox.util.RectangleUtils;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.SplitDownTheMiddleParameters;
import org.sejda.model.pdf.encryption.PdfAccessPermission;
import org.sejda.model.repaginate.Repagination;
import org.sejda.model.split.SplitDownTheMiddleMode;
import org.sejda.model.task.BaseTask;
import org.sejda.model.task.TaskExecutionContext;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PDPageContentStream;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.graphics.form.PDFormXObject;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.sejda.sambox.util.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The typical example is A3 -&gt; two A4s or the double page scan split.
 *
 * Splits all (two page layout) pages in a document in two sides, down the middle, creating a document that contains double the number of pages. Works for multiple inputs. If the
 * page orientation is portrait, the split is done horizontally. Otherwise, it is done vertically, for landscape orientation.
 * 
 * @author Eduard Weissmann
 */
public class SplitDownTheMiddleTask extends BaseTask<SplitDownTheMiddleParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(SplitDownTheMiddleTask.class);

    private int totalSteps;
    private PDDocumentHandler sourceHandler = null;
    private PDDocumentHandler destinationHandler = null;
    private MultipleOutputWriter outputWriter;
    private PdfSourceOpener<PDDocumentHandler> documentLoader;
    private LookupTable<PDPage> pagesLookup = new LookupTable<>();
    private LookupTable<PDPage> secondPagesLookup = new LookupTable<>();

    @Override
    public void before(SplitDownTheMiddleParameters parameters, TaskExecutionContext executionContext)
            throws TaskException {
        super.before(parameters, executionContext);
        totalSteps = parameters.getSourceList().size();
        documentLoader = new DefaultPdfSourceOpener();
        outputWriter = OutputWriters.newMultipleOutputWriter(parameters.getExistingOutputPolicy(), executionContext);
    }

    @Override
    public void execute(SplitDownTheMiddleParameters parameters) throws TaskException {

        int currentStep = 0;
        for (PdfSource<?> source : parameters.getSourceList()) {
            executionContext().assertTaskNotCancelled();
            currentStep++;
            LOG.debug("Opening {}", source);
            sourceHandler = source.open(documentLoader);
            sourceHandler.getPermissions().ensurePermission(PdfAccessPermission.COPY_AND_EXTRACT);

            destinationHandler = new PDDocumentHandler();
            destinationHandler.setVersionOnPDDocument(parameters.getVersion());
            destinationHandler.initialiseBasedOn(sourceHandler.getUnderlyingPDDocument());
            destinationHandler.setCompress(parameters.isCompress());

            File tmpFile = createTemporaryBuffer();
            LOG.debug("Created output on temporary buffer {}", tmpFile);

            Set<Integer> excludedPages = parameters.getExcludedPages(sourceHandler.getNumberOfPages());

            pagesLookup = new LookupTable<>();
            secondPagesLookup = new LookupTable<>();

            for (int pageNumber = 1; pageNumber <= sourceHandler.getNumberOfPages(); pageNumber++) {
                PDPage page = sourceHandler.getPage(pageNumber);
                PDRectangle trimBox = page.getTrimBox();

                if (excludedPages.contains(pageNumber)) {
                    LOG.debug("Not splitting down the middle page {}", pageNumber);
                    PDPage newPage = destinationHandler.importPage(page);
                    pagesLookup.addLookupEntry(page, newPage);
                    continue;
                }

                double ratio = parameters.getRatio();

                // by default determine based on page mode whether the split should be horizontal or vertical
                // based on whether the page is in portrait or landscape mode
                boolean landscapeMode = trimBox.getHeight() <= trimBox.getWidth();
                // adjust to user perceived
                if(page.getRotation() == 90 || page.getRotation() == 270) {
                    landscapeMode = !landscapeMode;
                }

                // allow user to override this by explicitly setting a split mode
                if (parameters.getMode() == SplitDownTheMiddleMode.HORIZONTAL) {
                    landscapeMode = false;
                } else if(parameters.getMode() == SplitDownTheMiddleMode.VERTICAL) {
                    landscapeMode = true;
                }

                // landscape vs portrait
                if (landscapeMode) {
                    // landscape orientation

                    importLeftPage(page, ratio);
                    importRightPage(page, ratio);

                } else {
                    // portrait orientation

                    importTopPage(page, ratio);
                    importBottomPage(page, ratio);
                }
            }

            processAnnotations(pagesLookup);
            processAnnotations(secondPagesLookup);

            // repaginate
            if (parameters.getRepagination() == Repagination.LAST_FIRST) {
                int pages = destinationHandler.getNumberOfPages();

                // differs based on even/odd number of double-layout pages
                int startStep = pages / 2 % 2;
                // alternates between 1 or 3
                int step = startStep == 0 ? 3 : 1;

                int i = pages - startStep;
                while (i > 0) {
                    destinationHandler.movePageToDocumentEnd(i);
                    i -= step;

                    if (step == 1) {
                        step = 3;
                    } else {
                        step = 1;
                    }
                }
            }

            PDDocumentOutline destinationOutline = new PDDocumentOutline();
            new OutlineDistiller(sourceHandler.getUnderlyingPDDocument()).appendRelevantOutlineTo(destinationOutline, pagesLookup);
            destinationHandler.getUnderlyingPDDocument().getDocumentCatalog().setDocumentOutline(destinationOutline);

            destinationHandler.savePDDocument(tmpFile);

            String outName = nameGenerator(parameters.getOutputPrefix())
                    .generate(nameRequest().originalName(source.getName()).fileNumber(currentStep));
            outputWriter.addOutput(file(tmpFile).name(outName));

            closeResources();

            notifyEvent(executionContext().notifiableTaskMetadata()).stepsCompleted(currentStep).outOf(totalSteps);
        }

        parameters.getOutput().accept(outputWriter);
        LOG.debug("Text extracted from input documents and written to {}", parameters.getOutput());

    }

    private Map<PDPage, PDFormXObject> cache = new HashMap<>();

    private PDFormXObject getPageAsFormXObject(PDPage page) throws IOException {
        if(!cache.containsKey(page)) {
            cache.put(page, new PageToFormXObject().apply(page));
        }

        return cache.get(page);
    }

    private void importLeftPage(PDPage page, double ratio) throws TaskIOException {
        PDRectangle trimBox = page.getTrimBox().rotate(page.getRotation());

        float w = trimBox.getWidth();
        float r = (float) ratio;
        float rightSideWidth = w / (r + 1);
        float leftSideWidth = w - rightSideWidth;

        importPage(page, leftSideWidth, trimBox.getHeight(), 0, 0);
    }

    private void importRightPage(PDPage page, double ratio) throws TaskIOException {
        PDRectangle trimBox = page.getTrimBox().rotate(page.getRotation());

        float w = trimBox.getWidth();
        float r = (float) ratio;
        float rightSideWidth = w / (r + 1);
        float leftSideWidth = w - rightSideWidth;

        importPage(page, rightSideWidth, trimBox.getHeight(), -leftSideWidth, 0);
    }

    private void importTopPage(PDPage page, double ratio) throws TaskIOException {
        PDRectangle trimBox = page.getTrimBox().rotate(page.getRotation());

        float h = trimBox.getHeight();
        float r = (float) ratio;
        float bottomSideHeight = h / (r + 1);
        float topSideHeight = h - bottomSideHeight;

        importPage(page, trimBox.getWidth(), topSideHeight, 0, - bottomSideHeight);
    }

    private void importBottomPage(PDPage page, double ratio) throws TaskIOException {
        PDRectangle trimBox = page.getTrimBox().rotate(page.getRotation());

        float h = trimBox.getHeight();
        float r = (float) ratio;
        float bottomSideHeight = h / (r + 1);

        importPage(page, trimBox.getWidth(), bottomSideHeight, 0, 0);
    }

    private Map<PDPage, Offsets> offsetsMap = new HashMap<>();

    private void importPage(PDPage sourcePage, float width, float height, float xOffset, float yOffset) throws TaskIOException {
        PDRectangle newMediaBox = new PDRectangle(width, height);

        PDRectangle mediaBox = sourcePage.getMediaBox().rotate(sourcePage.getRotation());
        PDRectangle trimBox = sourcePage.getTrimBox().rotate(sourcePage.getRotation());

        float cropLeftMargin = trimBox.getLowerLeftX() - mediaBox.getLowerLeftX();
        float cropBottomMargin = trimBox.getLowerLeftY() - mediaBox.getLowerLeftY();

        PDPage newPage = destinationHandler.addBlankPage(newMediaBox);
        if(pagesLookup.hasLookupFor(sourcePage)) {
            secondPagesLookup.addLookupEntry(sourcePage, newPage);
        } else {
            pagesLookup.addLookupEntry(sourcePage, newPage);
        }

        offsetsMap.put(newPage, new Offsets(xOffset, yOffset, width, height));

        try {
            PDFormXObject pageAsFormObject = getPageAsFormXObject(sourcePage);
            PDPageContentStream currentContentStream = new PDPageContentStream(
                    destinationHandler.getUnderlyingPDDocument(), newPage,
                    PDPageContentStream.AppendMode.APPEND, true, true);
            AffineTransform at = new AffineTransform();
            at.translate(xOffset + cropLeftMargin, yOffset + cropBottomMargin);

            Matrix matrix = new Matrix(at);

            currentContentStream.transform(matrix);
            currentContentStream.drawForm(pageAsFormObject);
            currentContentStream.close();
        } catch (IOException ex) {
            throw new TaskIOException(ex);
        }
    }

    private void processAnnotations(LookupTable<PDPage> lookup) {
        LookupTable<PDAnnotation> annotations = new AnnotationsDistiller(sourceHandler.getUnderlyingPDDocument())
                .retainRelevantAnnotations(lookup);
        clipSignatures(annotations.values());

//        LOG.info("Processing {} annotations", annotations.values().size());

        for(int i = 1; i <= sourceHandler.getNumberOfPages(); i++) {
            PDPage oldPage = sourceHandler.getPage(i);
            List<PDAnnotation> oldPageAnnotations = oldPage.getAnnotations();
            for(PDAnnotation oldAnnotation: oldPageAnnotations) {
                PDAnnotation newAnnotation = annotations.lookup(oldAnnotation);
                PDPage newPage = lookup.lookup(oldPage);

                if(newPage != null && newAnnotation != null) {

                    PDRectangle oldMediaBox = oldPage.getMediaBox();
                    PDRectangle oldTrimBox = oldPage.getTrimBox();
                    PDRectangle rotatedOldMediaBox = oldMediaBox.rotate(oldPage.getRotation());
                    PDRectangle rotatedOldTrimBox = oldTrimBox.rotate(oldPage.getRotation());

                    float rotatedCropLeftMargin = rotatedOldTrimBox.getLowerLeftX() - rotatedOldMediaBox.getLowerLeftX();
                    float rotatedCropBottomMargin = rotatedOldTrimBox.getLowerLeftY() - rotatedOldMediaBox.getLowerLeftY();

                    Offsets offsets = offsetsMap.get(newPage);

                    if(offsets == null) {
                        // this page was imported without splitting it, was in excludedPages
                        continue;
                    }

                    PDRectangle newPageBoundsInOldPage = new PDRectangle(-offsets.xOffset, -offsets.yOffset, offsets.newWidth, offsets.newHeight);
                    newPageBoundsInOldPage = RectangleUtils.rotate(-oldPage.getRotation(), newPageBoundsInOldPage, rotatedOldMediaBox);
                    PDRectangle oldRectangle = newAnnotation.getRectangle();

                    if(oldRectangle == null) {
                        // annotation has no presentation bounds, can happen for some form fields
                        continue;
                    }

                    if(RectangleUtils.intersect(newPageBoundsInOldPage, oldRectangle)) {
                        if(newAnnotation.getNormalAppearanceStream() != null) {
                            PDRectangle mediaBox = oldPage.getMediaBox();
                            PDRectangle boundingBox = ofNullable(oldPage.getCropBox()).orElse(mediaBox);

                            // TODO: this doesn't work for cropped and rotated at the same time

                            // this comes from PDFBox Superimpose class
                            AffineTransform at = new AffineTransform();
                            //at.translate(mediaBox.getLowerLeftX() - boundingBox.getLowerLeftX(),
                            //        mediaBox.getLowerLeftY() - boundingBox.getLowerLeftY());
                            switch (oldPage.getRotation()) {
                                case 90:
                                    // at.scale(boundingBox.getWidth() / boundingBox.getHeight(),
                                    // boundingBox.getHeight() / boundingBox.getWidth());
                                    at.translate(0, boundingBox.getWidth());
                                    at.rotate(-Math.PI / 2.0);
                                    break;
                                case 180:
                                    at.translate(boundingBox.getWidth(), boundingBox.getHeight());
                                    at.rotate(-Math.PI);
                                    break;
                                case 270:
                                    // at.scale(boundingBox.getWidth() / boundingBox.getHeight(),
                                    // boundingBox.getHeight() / boundingBox.getWidth());
                                    at.translate(boundingBox.getHeight(), 0);
                                    at.rotate(-Math.PI * 1.5);
                                    break;
                                default:
                                    // no additional transformations necessary
                            }
                            // Compensate for Crop Boxes not starting at 0,0
                            //at.translate(-boundingBox.getLowerLeftX(), -boundingBox.getLowerLeftY());
                            Rectangle2D transformedRect = at.createTransformedShape(oldRectangle.toGeneralPath()).getBounds2D();

                            PDRectangle newRect = new PDRectangle((float)transformedRect.getX(),
                                    (float)transformedRect.getY(),
                                    (float)transformedRect.getWidth(),
                                    (float)transformedRect.getHeight());

                            newRect = RectangleUtils.translate(offsets.xOffset - rotatedCropLeftMargin, offsets.yOffset - rotatedCropBottomMargin, newRect);

                            newAnnotation.setRectangle(newRect);

                            LOG.debug("Updating annotation {} to page {}", newAnnotation, destinationHandler.getPages().indexOf(newPage));
                            int idx = newPage.getAnnotations().indexOf(newAnnotation);
                            newPage.getAnnotations().set(idx, newAnnotation);
                        }
                    } else {
                        LOG.debug("Removing annotation {} to page {}", newAnnotation, destinationHandler.getPages().indexOf(newPage));
                        newPage.getAnnotations().remove(newAnnotation);
                    }
                }
            }
        }
    }

    @Override
    public void after() {
        closeResources();
        pagesLookup.clear();
        secondPagesLookup.clear();
    }

    private void closeResources() {
        nullSafeCloseQuietly(sourceHandler);
        nullSafeCloseQuietly(destinationHandler);
    }

    static final class Offsets {
        public final float xOffset;
        public final float yOffset;
        public final float newWidth;
        public final float newHeight;

        public Offsets(float xOffset, float yOffset, float newWidth, float newHeight) {
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.newWidth = newWidth;
            this.newHeight = newHeight;
        }
    }
}
