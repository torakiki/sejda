/*
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.ViewerPreferencesParameters;
import org.sejda.model.task.BaseTask;
import org.sejda.model.task.TaskExecutionContext;
import org.sejda.sambox.pdmodel.interactive.viewerpreferences.PDViewerPreferences;
import org.sejda.sambox.pdmodel.interactive.viewerpreferences.PDViewerPreferences.DUPLEX;
import org.sejda.sambox.pdmodel.interactive.viewerpreferences.PDViewerPreferences.NON_FULL_SCREEN_PAGE_MODE;
import org.sejda.sambox.pdmodel.interactive.viewerpreferences.PDViewerPreferences.PRINT_SCALING;
import org.sejda.sambox.pdmodel.interactive.viewerpreferences.PDViewerPreferences.READING_DIRECTION;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static java.util.Optional.ofNullable;
import static org.sejda.commons.util.IOUtils.closeQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.model.util.IOUtils.createTemporaryBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;
import static org.sejda.impl.sambox.util.ViewerPreferencesUtils.getDirection;
import static org.sejda.impl.sambox.util.ViewerPreferencesUtils.getDuplex;
import static org.sejda.impl.sambox.util.ViewerPreferencesUtils.getNFSMode;
import static org.sejda.impl.sambox.util.ViewerPreferencesUtils.getPrintScaling;
import static org.sejda.impl.sambox.util.ViewerPreferencesUtils.setBooleanPreferences;

/**
 * SAMBox implementation of a task setting viewer preferences on a list of {@link PdfSource}.
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

    @Override
    public void before(ViewerPreferencesParameters parameters, TaskExecutionContext executionContext)
            throws TaskException {
        super.before(parameters, executionContext);
        totalSteps = parameters.getSourceList().size();
        documentLoader = new DefaultPdfSourceOpener();
        outputWriter = OutputWriters.newMultipleOutputWriter(parameters.getExistingOutputPolicy(), executionContext);
    }

    @Override
    public void execute(ViewerPreferencesParameters parameters) throws TaskException {
        for (PdfSource<?> source : parameters.getSourceList()) {
            int fileNumber = executionContext().incrementAndGetOutputDocumentsCounter();
            LOG.debug("Opening {}", source);
            executionContext().notifiableTaskMetadata().setCurrentSource(source);
            documentHandler = source.open(documentLoader);
            documentHandler.setCreatorOnPDDocument();

            File tmpFile = createTemporaryBuffer(parameters.getOutput());
            LOG.debug("Created output on temporary buffer {}", tmpFile);

            documentHandler.setVersionOnPDDocument(parameters.getVersion());
            documentHandler.setCompress(parameters.isCompress());
            documentHandler.setPageModeOnDocument(parameters.getPageMode());
            documentHandler.setPageLayoutOnDocument(parameters.getPageLayout());

            setViewerPreferences(parameters);

            documentHandler.savePDDocument(tmpFile, parameters.getOutput().getEncryptionAtRestPolicy());
            String outName = ofNullable(parameters.getSpecificResultFilename(fileNumber)).orElseGet(() -> {
                return nameGenerator(parameters.getOutputPrefix())
                        .generate(nameRequest().originalName(source.getName()).fileNumber(fileNumber));
            });
            outputWriter.addOutput(file(tmpFile).name(outName));

            closeQuietly(documentHandler);

            notifyEvent(executionContext().notifiableTaskMetadata()).stepsCompleted(fileNumber).outOf(totalSteps);
        }

        executionContext().notifiableTaskMetadata().clearCurrentSource();
        parameters.getOutput().accept(outputWriter);
        LOG.debug("Viewer preferences set on input documents and written to {}", parameters.getOutput());

    }

    private void setViewerPreferences(ViewerPreferencesParameters parameters) throws TaskException {
        PDViewerPreferences preferences = documentHandler.getViewerPreferences();
        setBooleanPreferences(preferences, parameters.getEnabledPreferences());
        if (parameters.getDirection() != null) {
            READING_DIRECTION direction = getDirection(parameters.getDirection());
            preferences.setReadingDirection(direction);
            LOG.trace("Direction set to '{}'", direction);
        }
        if (parameters.getDuplex() != null) {
            DUPLEX duplex = getDuplex(parameters.getDuplex());
            preferences.setDuplex(duplex);
            LOG.trace("Duplex set to '{}'", duplex);
        }
        if (parameters.getPrintScaling() != null) {
            PRINT_SCALING printScaling = getPrintScaling(parameters.getPrintScaling());
            preferences.setPrintScaling(printScaling);
            LOG.trace("PrintScaling set to '{}'", printScaling);
        }
        NON_FULL_SCREEN_PAGE_MODE nfsMode = getNFSMode(parameters.getNfsMode());
        preferences.setNonFullScreenPageMode(nfsMode);
        LOG.trace("Non full screen mode set to '{}'", nfsMode);
        documentHandler.setViewerPreferences(preferences);
    }

    @Override
    public void after() {
        closeQuietly(documentHandler);
    }

}
