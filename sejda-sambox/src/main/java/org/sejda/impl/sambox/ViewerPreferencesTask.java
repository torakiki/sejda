/*
 * Created on 27/ago/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.impl.sambox;

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryPdfBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;
import static org.sejda.impl.sambox.util.ViewerPreferencesUtils.getDirection;
import static org.sejda.impl.sambox.util.ViewerPreferencesUtils.getDuplex;
import static org.sejda.impl.sambox.util.ViewerPreferencesUtils.getNFSMode;
import static org.sejda.impl.sambox.util.ViewerPreferencesUtils.getPrintScaling;
import static org.sejda.impl.sambox.util.ViewerPreferencesUtils.setBooleanPreferences;

import java.io.File;

import org.sejda.sambox.pdmodel.interactive.viewerpreferences.PDViewerPreferences;
import org.sejda.sambox.pdmodel.interactive.viewerpreferences.PDViewerPreferences.DUPLEX;
import org.sejda.sambox.pdmodel.interactive.viewerpreferences.PDViewerPreferences.NON_FULL_SCREEN_PAGE_MODE;
import org.sejda.sambox.pdmodel.interactive.viewerpreferences.PDViewerPreferences.PRINT_SCALING;
import org.sejda.sambox.pdmodel.interactive.viewerpreferences.PDViewerPreferences.READING_DIRECTION;
import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.ViewerPreferencesParameters;
import org.sejda.model.task.BaseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PDFBox implementation of a task setting viewer preferences on a list of {@link PdfSource}.
 * 
 * @author Andrea Vacondio
 * 
 */
public class ViewerPreferencesTask extends BaseTask<ViewerPreferencesParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(ViewerPreferencesTask.class);

    private PDDocumentHandler documentHandler = null;
    private int totalSteps;
    private MultipleOutputWriter outputWriter;
    private PdfSourceOpener<PDDocumentHandler> documentLoader;

    public void before(ViewerPreferencesParameters parameters) {
        totalSteps = parameters.getSourceList().size();
        documentLoader = new DefaultPdfSourceOpener();
        outputWriter = OutputWriters.newMultipleOutputWriter(parameters.isOverwrite());
    }

    public void execute(ViewerPreferencesParameters parameters) throws TaskException {
        int currentStep = 0;
        for (PdfSource<?> source : parameters.getSourceList()) {
            currentStep++;
            LOG.debug("Opening {} ...", source);
            documentHandler = source.open(documentLoader);
            documentHandler.setCreatorOnPDDocument();

            File tmpFile = createTemporaryPdfBuffer();
            LOG.debug("Created output on temporary buffer {} ...", tmpFile);

            documentHandler.setVersionOnPDDocument(parameters.getVersion());
            documentHandler.setCompress(parameters.isCompress());
            documentHandler.setPageModeOnDocument(parameters.getPageMode());
            documentHandler.setPageLayoutOnDocument(parameters.getPageLayout());

            setViewerPreferences(parameters);

            documentHandler.saveDecryptedPDDocument(tmpFile);
            String outName = nameGenerator(parameters.getOutputPrefix()).generate(
                    nameRequest().originalName(source.getName()).fileNumber(currentStep));
            outputWriter.addOutput(file(tmpFile).name(outName));

            nullSafeCloseQuietly(documentHandler);

            notifyEvent(getNotifiableTaskMetadata()).stepsCompleted(currentStep).outOf(totalSteps);
        }

        parameters.getOutput().accept(outputWriter);
        LOG.debug("Viewer preferences set on input documents and written to {}", parameters.getOutput());

    }

    private void setViewerPreferences(ViewerPreferencesParameters parameters) throws TaskException {
        PDViewerPreferences preferences = documentHandler.getViewerPreferences();
        setBooleanPreferences(preferences, parameters.getEnabledPreferences());
        if (parameters.getDirection() != null) {
            READING_DIRECTION direction = getDirection(parameters.getDirection());
            preferences.setReadingDirection(direction);
            LOG.trace("Direction set to '{}'.", direction);
        }
        if (parameters.getDuplex() != null) {
            DUPLEX duplex = getDuplex(parameters.getDuplex());
            preferences.setDuplex(duplex);
            LOG.trace("Duplex set to '{}'.", duplex);
        }
        if (parameters.getPrintScaling() != null) {
            PRINT_SCALING printScaling = getPrintScaling(parameters.getPrintScaling());
            preferences.setPrintScaling(printScaling);
            LOG.trace("PrintScaling set to '{}'.", printScaling);
        }
        NON_FULL_SCREEN_PAGE_MODE nfsMode = getNFSMode(parameters.getNfsMode());
        preferences.setNonFullScreenPageMode(nfsMode);
        LOG.trace("Non full screen mode set to '{}'.", nfsMode);
        documentHandler.setViewerPreferences(preferences);
    }

    public void after() {
        nullSafeCloseQuietly(documentHandler);
    }

}
