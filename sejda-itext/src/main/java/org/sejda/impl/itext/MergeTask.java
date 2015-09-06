/*
 * Created on 12/ago/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.impl.itext;

import static org.apache.commons.lang3.StringUtils.join;
import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryPdfBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.impl.itext.util.ITextUtils.nullSafeClosePdfReader;

import java.io.File;

import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.io.SingleOutputWriter;
import org.sejda.impl.itext.component.DefaultPdfCopier;
import org.sejda.impl.itext.component.FormFieldsAwarePdfCopier;
import org.sejda.impl.itext.component.PdfCopier;
import org.sejda.impl.itext.component.input.PdfSourceOpeners;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfMergeInput;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.MergeParameters;
import org.sejda.model.pdf.form.AcroFormPolicy;
import org.sejda.model.task.BaseTask;
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

    private SingleOutputWriter outputWriter;
    private PdfSourceOpener<PdfReader> sourceOpener;
    private OutlineMerger outlineMerger;
    private PdfCopier copier = null;
    private PdfReader reader;
    private int totalSteps;

    @Override
    public void before(MergeParameters parameters) {
        totalSteps = parameters.getInputList().size();
        sourceOpener = PdfSourceOpeners.newFullReadOpener();
        outputWriter = OutputWriters.newSingleOutputWriter(parameters.isOverwrite());
        outlineMerger = new OutlineMerger(parameters.getOutlinePolicy());
    }

    @Override
    public void execute(MergeParameters parameters) throws TaskException {
        int currentStep = 0;
        File tmpFile = createTemporaryPdfBuffer();
        LOG.debug("Created output temporary buffer {} ", tmpFile);

        for (PdfMergeInput input : parameters.getInputList()) {
            LOG.debug("Opening input {} ", input.getSource());
            reader = input.getSource().open(sourceOpener);

            createCopierIfNeeded(parameters, tmpFile);
            outlineMerger.updateOutline(reader, input, copier.getNumberOfCopiedPages());

            if (!input.isAllPages()) {
                String selection = join(input.getPageSelection(), ',');
                LOG.debug("Setting pages selection");
                reader.selectPages(selection);
                LOG.trace("Pages selection set to {}", selection);
            }

            copier.addAllPages(reader);

            if (parameters.isBlankPageIfOdd()) {
                LOG.debug("Adding blank page if required");
                copier.addBlankPageIfOdd(reader);
            }
            copier.freeReader(reader);

            notifyEvent(getNotifiableTaskMetadata()).stepsCompleted(++currentStep).outOf(totalSteps);
        }
        copier.setOutline(outlineMerger.getOutline());
        closeResources();
        outputWriter.setOutput(file(tmpFile).name(parameters.getOutputName()));
        parameters.getOutput().accept(outputWriter);
        LOG.debug("Input documents merged correctly and written to {}", parameters.getOutput());
    }

    @Override
    public void after() {
        closeResources();
    }

    private void closeResources() {
        nullSafeCloseQuietly(copier);
        nullSafeClosePdfReader(reader);
    }

    private void createCopierIfNeeded(MergeParameters parameters, File tmpFile) throws TaskException {
        if (copier == null) {
            if (parameters.getAcroFormPolicy() == AcroFormPolicy.MERGE) {
                copier = new FormFieldsAwarePdfCopier(tmpFile, parameters.getVersion());
                LOG.debug("Created FormFieldsAwarePdfCopier");
            } else {
                copier = new DefaultPdfCopier(reader, tmpFile, parameters.getVersion());
                LOG.debug("Created DefaultPdfCopier");
            }
            copier.setCompression(parameters.isCompress());
        }
    }
}
