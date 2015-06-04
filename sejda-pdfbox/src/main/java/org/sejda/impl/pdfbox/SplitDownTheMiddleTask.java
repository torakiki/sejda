/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sejda.impl.pdfbox;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.impl.pdfbox.component.DefaultPdfSourceOpener;
import org.sejda.impl.pdfbox.component.PDDocumentHandler;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.SplitDownTheMiddleParameters;
import org.sejda.model.pdf.encryption.PdfAccessPermission;
import org.sejda.model.task.BaseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

/**
 * The typical example is A3 -> two A4s or the double page scan split.
 *
 * Splits all (two page layout) pages in a document in two sides, down the middle, creating a document that contains double the number of pages.
 * Works for multiple inputs.
 * If the page orientation is portrait, the split is done horizontally. Otherwise, it is done vertically, for landscape orientation.
 */
public class SplitDownTheMiddleTask extends BaseTask<SplitDownTheMiddleParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(SplitDownTheMiddleTask.class);

    private int totalSteps;
    private PDDocumentHandler sourceHandler = null;
    private PDDocumentHandler destinationHandler = null;
    private MultipleOutputWriter outputWriter;
    private PdfSourceOpener<PDDocumentHandler> documentLoader;

    public void before(SplitDownTheMiddleParameters parameters) throws TaskException {
        totalSteps = parameters.getSourceList().size();
        documentLoader = new DefaultPdfSourceOpener();
        outputWriter = OutputWriters.newMultipleOutputWriter(parameters.isOverwrite());
    }

    public void execute(SplitDownTheMiddleParameters parameters) throws TaskException {

        int currentStep = 0;
        for (PdfSource<?> source : parameters.getSourceList()) {
            currentStep++;
            LOG.debug("Opening {}", source);
            sourceHandler = source.open(documentLoader);
            LOG.debug("Done Opening");
            sourceHandler.getPermissions().ensurePermission(PdfAccessPermission.COPY_AND_EXTRACT);
            LOG.debug("Done with perms");

            destinationHandler = new PDDocumentHandler();
            destinationHandler.setVersionOnPDDocument(parameters.getVersion());
            LOG.debug("Done with version");
            destinationHandler.initialiseBasedOn(sourceHandler);
            LOG.debug("Done with init");

            File tmpFile = createTemporaryBuffer();
            LOG.debug("Created output on temporary buffer {}", tmpFile);

            for (int pageNumber = 1; pageNumber <= sourceHandler.getNumberOfPages(); pageNumber++) {
                PDPage page = sourceHandler.getPage(pageNumber);
                PDRectangle trimBox = page.getTrimBox();


                // landscape vs portrait
                if(trimBox.getHeight() <= trimBox.getWidth()) {
                    // landscape orientation

                    PDPage leftPage = destinationHandler.importPage(page);
                    PDRectangle leftSide = new PDRectangle();
                    leftSide.setUpperRightY(trimBox.getUpperRightY());
                    leftSide.setUpperRightX(trimBox.getLowerLeftX() + trimBox.getWidth()/2);
                    leftSide.setLowerLeftY(trimBox.getLowerLeftY());
                    leftSide.setLowerLeftX(trimBox.getLowerLeftX());

                    leftPage.setCropBox(leftSide);

                    PDPage rightPage = destinationHandler.importPage(page);
                    PDRectangle rightSide = new PDRectangle();
                    rightSide.setUpperRightY(trimBox.getUpperRightY());
                    rightSide.setUpperRightX(trimBox.getUpperRightX());
                    rightSide.setLowerLeftY(trimBox.getLowerLeftY());
                    rightSide.setLowerLeftX(trimBox.getLowerLeftX() + trimBox.getWidth()/2);

                    rightPage.setCropBox(rightSide);
                } else {
                    // portrait orientation

                    PDPage topPage = destinationHandler.importPage(page);
                    PDRectangle upperSide = new PDRectangle();
                    upperSide.setUpperRightY(trimBox.getUpperRightY());
                    upperSide.setUpperRightX(trimBox.getUpperRightX());
                    upperSide.setLowerLeftY(trimBox.getLowerLeftY() + trimBox.getHeight() / 2);
                    upperSide.setLowerLeftX(trimBox.getLowerLeftX());

                    topPage.setCropBox(upperSide);

                    PDPage bottomPage = destinationHandler.importPage(page);
                    PDRectangle lowerSide = new PDRectangle();
                    lowerSide.setUpperRightY(trimBox.getLowerLeftY() + trimBox.getHeight()/2);
                    lowerSide.setUpperRightX(trimBox.getUpperRightX());
                    lowerSide.setLowerLeftY(trimBox.getLowerLeftY());
                    lowerSide.setLowerLeftX(trimBox.getLowerLeftX());

                    bottomPage.setCropBox(lowerSide);
                }


            }

            destinationHandler.savePDDocument(tmpFile);

            String outName = nameGenerator(parameters.getOutputPrefix()).generate(
                    nameRequest().originalName(source.getName()).fileNumber(currentStep));
            outputWriter.addOutput(file(tmpFile).name(outName));

            closeResources();

            notifyEvent(getNotifiableTaskMetadata()).stepsCompleted(currentStep).outOf(totalSteps);
        }

        parameters.getOutput().accept(outputWriter);
        LOG.debug("Text extracted from input documents and written to {}", parameters.getOutput());

    }

    public void after() {
        closeResources();
    }

    private void closeResources() {
        nullSafeCloseQuietly(sourceHandler);
        nullSafeCloseQuietly(destinationHandler);
    }
}
