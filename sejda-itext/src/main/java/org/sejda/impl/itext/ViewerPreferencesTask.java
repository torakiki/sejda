/*
 * Created on 21/set/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.impl.itext;

import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;
import static org.sejda.core.support.util.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.impl.itext.util.ITextUtils.nullSafeClosePdfReader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.model.input.PdfSource;
import org.sejda.core.manipulation.model.input.PdfSourceOpener;
import org.sejda.core.manipulation.model.parameter.ViewerPreferencesParameters;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfBooleanPreference;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.support.io.MultipleOutputWriterSupport;
import org.sejda.impl.itext.component.PdfStamperHandler;
import org.sejda.impl.itext.component.input.PdfSourceOpeners;
import org.sejda.impl.itext.util.ViewerPreferencesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;

/**
 * Task setting viewer preferences on a list of {@link PdfSource}.
 * 
 * @author Andrea Vacondio
 * 
 */
public class ViewerPreferencesTask implements Task<ViewerPreferencesParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(ViewerPreferencesTask.class);

    private PdfReader reader = null;
    private PdfStamperHandler stamperHandler = null;
    private int totalSteps;
    private int preferences;
    private Map<PdfName, PdfObject> configuredPreferences;
    private MultipleOutputWriterSupport outputWriter;
    private PdfSourceOpener<PdfReader> sourceOpener;

    public void before(ViewerPreferencesParameters parameters) {
        outputWriter = new MultipleOutputWriterSupport();
        totalSteps = parameters.getSourceList().size();
        preferences = ViewerPreferencesUtils.getViewerPreferences(parameters.getPageMode(), parameters.getPageLayout());
        configuredPreferences = getConfiguredViewerPreferencesMap(parameters);
        sourceOpener = PdfSourceOpeners.newPartialReadOpener();
        if (LOG.isTraceEnabled()) {
            LOG.trace("The following preferences will be set on the input pdf sources:");
            for (Entry<PdfName, PdfObject> entry : configuredPreferences.entrySet()) {
                LOG.trace("{} = {}", entry.getKey(), entry.getValue());
            }
            LOG.trace("Page mode = {}", parameters.getPageMode());
            LOG.trace("Page layout = {}", parameters.getPageLayout());
        }
    }

    public void execute(ViewerPreferencesParameters parameters) throws TaskException {
        int currentStep = 0;

        for (PdfSource source : parameters.getSourceList()) {
            LOG.debug("Opening {} ", source);
            reader = source.open(sourceOpener);

            File tmpFile = outputWriter.createTemporaryPdfBuffer();
            LOG.debug("Created output temporary buffer {} ", tmpFile);
            stamperHandler = new PdfStamperHandler(reader, tmpFile, parameters.getVersion());

            stamperHandler.setCompressionOnStamper(parameters.isCompressXref());
            stamperHandler.setCreatorOnStamper(reader);
            // set mode and layout
            stamperHandler.setViewerPreferencesOnStamper(preferences);

            // set other preferences
            for (Entry<PdfName, PdfObject> entry : configuredPreferences.entrySet()) {
                stamperHandler.addViewerPreferenceOnStamper(entry.getKey(), entry.getValue());
            }

            nullSafeClosePdfReader(reader);
            nullSafeCloseQuietly(stamperHandler);

            String outName = nameGenerator(parameters.getOutputPrefix()).generate(
                    nameRequest().originalName(source.getName()));
            outputWriter.addOutput(file(tmpFile).name(outName));

            notifyEvent().stepsCompleted(++currentStep).outOf(totalSteps);
        }

        outputWriter.flushOutputs(parameters.getOutput(), parameters.isOverwrite());
        LOG.debug("Viewer preferences set on input documents and written to {}", parameters.getOutput());

    }

    public void after() {
        nullSafeClosePdfReader(reader);
        nullSafeCloseQuietly(stamperHandler);
    }

    /**
     * 
     * @param parameters
     * @return a map of preferences with corresponding value to be set on the documents
     */
    private Map<PdfName, PdfObject> getConfiguredViewerPreferencesMap(ViewerPreferencesParameters parameters) {
        Map<PdfName, PdfObject> confPreferences = new HashMap<PdfName, PdfObject>();
        if (parameters.getDirection() != null) {
            confPreferences.put(PdfName.DIRECTION, ViewerPreferencesUtils.getDirection(parameters.getDirection()));
        }
        if (parameters.getDuplex() != null) {
            confPreferences.put(PdfName.DUPLEX, ViewerPreferencesUtils.getDuplex(parameters.getDuplex()));
        }
        if (parameters.getPrintScaling() != null) {
            confPreferences.put(PdfName.PRINTSCALING,
                    ViewerPreferencesUtils.getPrintScaling(parameters.getPrintScaling()));
        }
        confPreferences.put(PdfName.NONFULLSCREENPAGEMODE, ViewerPreferencesUtils.getNFSMode(parameters.getNfsMode()));

        Set<PdfBooleanPreference> activePref = parameters.getEnabledPreferences();
        for (PdfBooleanPreference boolPref : PdfBooleanPreference.values()) {
            if (activePref.contains(boolPref)) {
                confPreferences.put(ViewerPreferencesUtils.getBooleanPreference(boolPref), PdfBoolean.PDFTRUE);
            } else {
                confPreferences.put(ViewerPreferencesUtils.getBooleanPreference(boolPref), PdfBoolean.PDFFALSE);
            }
        }
        return confPreferences;
    }
}
