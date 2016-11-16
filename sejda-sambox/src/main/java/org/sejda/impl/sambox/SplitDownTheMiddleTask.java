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

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;
import static org.sejda.impl.sambox.component.SignatureClipper.clipSignatures;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.sejda.common.LookupTable;
import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.impl.sambox.component.AnnotationsDistiller;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.impl.sambox.component.PageToFormXObject;
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
import org.sejda.sambox.pdmodel.PageNotFoundException;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.graphics.form.PDFormXObject;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
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

            LookupTable<PDPage> lookup = new LookupTable<>();
            for (int pageNumber = 1; pageNumber <= sourceHandler.getNumberOfPages(); pageNumber++) {
                PDPage page = sourceHandler.getPage(pageNumber);
                PDRectangle trimBox = page.getTrimBox();

                if (excludedPages.contains(pageNumber)) {
                    LOG.debug("Not cropping excluded page {}", pageNumber);
                    PDPage newPage = destinationHandler.importPage(page);
                    lookup.addLookupEntry(page, newPage);
                    continue;
                }

                try {
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

                        importLeftPage(page, lookup, ratio);
                        importRightPage(page, lookup, ratio);

                    } else {
                        // portrait orientation

                        importTopPage(page, lookup, ratio);
                        importBottomPage(page, lookup, ratio);
                    }
                } catch (PageNotFoundException ex) {
                    String warning = String.format("Page %d was skipped, could not be processed", pageNumber);
                    notifyEvent(executionContext().notifiableTaskMetadata()).taskWarning(warning);
                    LOG.warn(warning, ex);
                }
            }
            LookupTable<PDAnnotation> annotations = new AnnotationsDistiller(sourceHandler.getUnderlyingPDDocument())
                    .retainRelevantAnnotations(lookup);
            clipSignatures(annotations.values());

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

    private void importLeftPage(PDPage page, LookupTable<PDPage> lookup, double ratio) throws TaskIOException {
        PDRectangle trimBox = page.getTrimBox().rotate(page.getRotation());
        float w = trimBox.getWidth();
        float r = (float) ratio;
        float rightSideWidth = w / (r + 1);
        float leftSideWidth = w - rightSideWidth;

        importPage(page, lookup, leftSideWidth, trimBox.getHeight(), 0, 0);
    }

    private void importRightPage(PDPage page, LookupTable<PDPage> lookup, double ratio) throws TaskIOException {
        PDRectangle trimBox = page.getTrimBox().rotate(page.getRotation());
        float w = trimBox.getWidth();
        float r = (float) ratio;
        float rightSideWidth = w / (r + 1);
        float leftSideWidth = w - rightSideWidth;

        importPage(page, lookup, rightSideWidth, trimBox.getHeight(), -leftSideWidth, 0);
    }

    private void importTopPage(PDPage page, LookupTable<PDPage> lookup, double ratio) throws TaskIOException {
        PDRectangle trimBox = page.getTrimBox().rotate(page.getRotation());
        float h = trimBox.getHeight();
        float r = (float) ratio;
        float bottomSideHeight = h / (r + 1);
        float topSideHeight = h - bottomSideHeight;

        importPage(page, lookup, trimBox.getWidth(), topSideHeight, 0, -bottomSideHeight);
    }

    private void importBottomPage(PDPage page, LookupTable<PDPage> lookup, double ratio) throws TaskIOException {
        PDRectangle trimBox = page.getTrimBox().rotate(page.getRotation());
        float h = trimBox.getHeight();
        float r = (float) ratio;
        float bottomSideHeight = h / (r + 1);

        importPage(page, lookup, trimBox.getWidth(), bottomSideHeight, 0, 0);
    }

    private void importPage(PDPage sourcePage, LookupTable<PDPage> lookup, float width, float height, float xOffset, float yOffset) throws TaskIOException {
        PDRectangle newMediaBox = new PDRectangle(width, height);

        PDPage newPage = destinationHandler.addBlankPage(newMediaBox);
        lookup.addLookupEntry(sourcePage, newPage);

        try {
            PDFormXObject pageAsFormObject = getPageAsFormXObject(sourcePage);
            PDPageContentStream currentContentStream = new PDPageContentStream(
                    destinationHandler.getUnderlyingPDDocument(), newPage,
                    PDPageContentStream.AppendMode.APPEND, true, true);
            AffineTransform at = new AffineTransform();
            at.translate(xOffset, yOffset);

            Matrix matrix = new Matrix(at);

            currentContentStream.transform(matrix);
            currentContentStream.drawForm(pageAsFormObject);
            currentContentStream.close();
        } catch (IOException ex) {
            throw new TaskIOException(ex);
        }
    }

    @Override
    public void after() {
        closeResources();
    }

    private void closeResources() {
        nullSafeCloseQuietly(sourceHandler);
        nullSafeCloseQuietly(destinationHandler);
    }
}
