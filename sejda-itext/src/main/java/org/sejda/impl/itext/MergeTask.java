/*
 * Created on 12/ago/2011
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
package org.sejda.impl.itext;

import static org.apache.commons.lang3.StringUtils.join;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryPdfBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.util.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.impl.itext.util.ITextUtils.nullSafeClosePdfReader;

import java.io.File;

import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.model.input.PdfMergeInput;
import org.sejda.core.manipulation.model.input.PdfSourceOpener;
import org.sejda.core.manipulation.model.parameter.MergeParameters;
import org.sejda.core.manipulation.model.task.BaseTask;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.io.SingleOutputWriter;
import org.sejda.impl.itext.component.DefaultPdfCopier;
import org.sejda.impl.itext.component.FormFieldsAwarePdfCopier;
import org.sejda.impl.itext.component.PdfCopier;
import org.sejda.impl.itext.component.input.PdfSourceOpeners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.pdf.PdfReader;

/**
 * iText implementation for a merge task that merges a collection of input pdf documents.
 * 
 * @author Andrea Vacondio
 * 
 */
public class MergeTask extends BaseTask<MergeParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(MergeTask.class);

    private SingleOutputWriter outputWriter = OutputWriters.newSingleOutputWriter();
    private PdfSourceOpener<PdfReader> sourceOpener;
    private PdfCopier copier = null;
    private PdfReader reader;
    private int totalSteps;

    public void before(MergeParameters parameters) {
        totalSteps = parameters.getInputList().size();
        sourceOpener = PdfSourceOpeners.newFullReadOpener();
    }

    public void execute(MergeParameters parameters) throws TaskException {
        int currentStep = 0;
        File tmpFile = createTemporaryPdfBuffer();
        LOG.debug("Created output temporary buffer {} ", tmpFile);

        for (PdfMergeInput input : parameters.getInputList()) {
            LOG.debug("Opening input {} ", input.getSource());
            reader = input.getSource().open(sourceOpener);

            createCopierIfNeeded(parameters, tmpFile);

            if (!input.isAllPages()) {
                String selection = join(input.getPageSelection(), ',');
                LOG.debug("Setting pages selection ");
                reader.selectPages(selection);
                LOG.trace("Pages selection set to {}", selection);
            }

            copier.addAllPages(reader);
            copier.freeReader(reader);

            notifyEvent(getNotifiableTaskMetadata()).stepsCompleted(++currentStep).outOf(totalSteps);
        }

        closeResources();
        outputWriter.flushSingleOutput(file(tmpFile).name(parameters.getOutputName()), parameters.getOutput(),
                parameters.isOverwrite());
        LOG.debug("Input documents merged correctly and written to {}", parameters.getOutput());
    }

    public void after() {
        closeResources();
    }

    private void closeResources() {
        nullSafeCloseQuietly(copier);
        nullSafeClosePdfReader(reader);
    }

    private void createCopierIfNeeded(MergeParameters parameters, File tmpFile) throws TaskException {
        if (copier == null) {
            if (parameters.isCopyFormFields()) {
                copier = new FormFieldsAwarePdfCopier(tmpFile, parameters.getVersion());
                LOG.debug("Created FormFieldsAwarePdfCopier");
            } else {
                copier = new DefaultPdfCopier(reader, tmpFile, parameters.getVersion());
                LOG.debug("Created DefaultPdfCopier");
            }
        }
    }
}
