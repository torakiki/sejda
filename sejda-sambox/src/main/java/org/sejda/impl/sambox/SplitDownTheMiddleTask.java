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
import static org.sejda.impl.sambox.component.Annotations.processAnnotations;
import static org.sejda.impl.sambox.component.SignatureClipper.clipSignatures;

import java.io.File;

import org.sejda.common.LookupTable;
import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.SplitDownTheMiddleParameters;
import org.sejda.model.pdf.encryption.PdfAccessPermission;
import org.sejda.model.repaginate.Repagination;
import org.sejda.model.task.BaseTask;
import org.sejda.model.task.TaskExecutionContext;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
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
        outputWriter = OutputWriters.newMultipleOutputWriter(parameters.getExistingOutputPolicy());
    }

    @Override
    public void execute(SplitDownTheMiddleParameters parameters) throws TaskException {

        int currentStep = 0;
        for (PdfSource<?> source : parameters.getSourceList()) {
            executionContext().assertTaskNotCancelled();
            currentStep++;
            LOG.debug("Opening {}", source);
            sourceHandler = source.open(documentLoader);
            LOG.debug("Done Opening");
            sourceHandler.getPermissions().ensurePermission(PdfAccessPermission.COPY_AND_EXTRACT);
            LOG.debug("Done with perms");

            destinationHandler = new PDDocumentHandler();
            destinationHandler.setVersionOnPDDocument(parameters.getVersion());
            LOG.debug("Done with version");
            destinationHandler.initialiseBasedOn(sourceHandler.getUnderlyingPDDocument());
            destinationHandler.setCompress(parameters.isCompress());
            LOG.debug("Done with init");

            File tmpFile = createTemporaryBuffer();
            LOG.debug("Created output on temporary buffer {}", tmpFile);

            LookupTable<PDPage> lookup = new LookupTable<>();
            for (int pageNumber = 1; pageNumber <= sourceHandler.getNumberOfPages(); pageNumber++) {
                PDPage page = sourceHandler.getPage(pageNumber);
                PDRectangle trimBox = page.getTrimBox();

                // landscape vs portrait
                if (trimBox.getHeight() <= trimBox.getWidth()) {
                    // landscape orientation

                    boolean leftFirst = page.getRotation() != 270 && page.getRotation() != 180;

                    if(leftFirst) {
                        importLeftPage(page, lookup);
                        importRightPage(page, lookup);
                    } else {
                        importRightPage(page, lookup);
                        importLeftPage(page, lookup);
                    }

                } else {
                    // portrait orientation

                    boolean topFirst = page.getRotation() != 90 && page.getRotation() != 180;

                    if(topFirst) {
                        importTopPage(page, lookup);
                        importBottomPage(page, lookup);
                    } else {
                        importBottomPage(page, lookup);
                        importTopPage(page, lookup);
                    }

                }
            }
            LookupTable<PDAnnotation> annotations = processAnnotations(lookup, sourceHandler.getUnderlyingPDDocument());
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

    private void importLeftPage(PDPage page, LookupTable<PDPage> lookup){
        PDRectangle trimBox = page.getTrimBox();
        PDPage leftPage = destinationHandler.importPage(page);
        lookup.addLookupEntry(page, leftPage);
        PDRectangle leftSide = new PDRectangle();
        leftSide.setUpperRightY(trimBox.getUpperRightY());
        leftSide.setUpperRightX(trimBox.getLowerLeftX() + trimBox.getWidth() / 2);
        leftSide.setLowerLeftY(trimBox.getLowerLeftY());
        leftSide.setLowerLeftX(trimBox.getLowerLeftX());

        leftPage.setCropBox(leftSide);
        leftPage.setTrimBox(leftSide);
        leftPage.setMediaBox(leftSide);
    }

    private void importRightPage(PDPage page, LookupTable<PDPage> lookup){
        PDRectangle trimBox = page.getTrimBox();
        PDPage rightPage = destinationHandler.importPage(page);
        lookup.addLookupEntry(page, rightPage);
        PDRectangle rightSide = new PDRectangle();
        rightSide.setUpperRightY(trimBox.getUpperRightY());
        rightSide.setUpperRightX(trimBox.getUpperRightX());
        rightSide.setLowerLeftY(trimBox.getLowerLeftY());
        rightSide.setLowerLeftX(trimBox.getLowerLeftX() + trimBox.getWidth() / 2);

        rightPage.setCropBox(rightSide);
        rightPage.setTrimBox(rightSide);
        rightPage.setMediaBox(rightSide);
    }

    private void importTopPage(PDPage page, LookupTable<PDPage> lookup){
        PDRectangle trimBox = page.getTrimBox();
        PDPage topPage = destinationHandler.importPage(page);
        lookup.addLookupEntry(page, topPage);
        PDRectangle upperSide = new PDRectangle();
        upperSide.setUpperRightY(trimBox.getUpperRightY());
        upperSide.setUpperRightX(trimBox.getUpperRightX());
        upperSide.setLowerLeftY(trimBox.getLowerLeftY() + trimBox.getHeight() / 2);
        upperSide.setLowerLeftX(trimBox.getLowerLeftX());

        topPage.setCropBox(upperSide);
        topPage.setTrimBox(upperSide);
        topPage.setMediaBox(upperSide);
    }

    private void importBottomPage(PDPage page, LookupTable<PDPage> lookup){
        PDRectangle trimBox = page.getTrimBox();
        PDPage bottomPage = destinationHandler.importPage(page);
        lookup.addLookupEntry(page, bottomPage);
        PDRectangle lowerSide = new PDRectangle();
        lowerSide.setUpperRightY(trimBox.getLowerLeftY() + trimBox.getHeight() / 2);
        lowerSide.setUpperRightX(trimBox.getUpperRightX());
        lowerSide.setLowerLeftY(trimBox.getLowerLeftY());
        lowerSide.setLowerLeftX(trimBox.getLowerLeftX());

        bottomPage.setCropBox(lowerSide);
        bottomPage.setTrimBox(lowerSide);
        bottomPage.setMediaBox(lowerSide);
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
