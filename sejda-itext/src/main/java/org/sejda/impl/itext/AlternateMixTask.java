/*
 * Created on 25/dic/2010
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
import static org.sejda.core.support.util.ComponentsUtility.nullSafeClose;
import static org.sejda.impl.itext.util.ITextUtils.nullSafeClosePdfReader;

import java.io.File;

import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.model.input.PdfMixInput.PdfMixInputProcessStatus;
import org.sejda.core.manipulation.model.input.PdfSourceOpener;
import org.sejda.core.manipulation.model.parameter.AlternateMixParameters;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.support.io.SingleOutputWriterSupport;
import org.sejda.impl.itext.component.DefaultPdfCopier;
import org.sejda.impl.itext.component.PdfCopier;
import org.sejda.impl.itext.component.input.PdfSourceOpeners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.pdf.PdfReader;

/**
 * iText implementation for the alternate mix task
 * 
 * @author Andrea Vacondio
 * 
 */
public class AlternateMixTask implements Task<AlternateMixParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(AlternateMixTask.class);

    private PdfReader firstReader = null;
    private PdfReader secondReader = null;
    private PdfCopier copier = null;
    private SingleOutputWriterSupport outputWriter;
    private PdfSourceOpener<PdfReader> sourceOpener;

    public void before(AlternateMixParameters parameters) {
        outputWriter = new SingleOutputWriterSupport();
        sourceOpener = PdfSourceOpeners.newPartialReadOpener();
    }

    public void execute(AlternateMixParameters parameters) throws TaskException {
        LOG.debug("Opening first input {} ", parameters.getFirstInput().getSource());
        firstReader = parameters.getFirstInput().getSource().open(sourceOpener);
        LOG.debug("Opening second input {} ", parameters.getSecondInput().getSource());
        secondReader = parameters.getSecondInput().getSource().open(sourceOpener);

        File tmpFile = outputWriter.createTemporaryPdfBuffer();
        LOG.debug("Created output temporary buffer {} ", tmpFile);
        copier = new DefaultPdfCopier(firstReader, tmpFile, parameters.getVersion());

        copier.setCompression(parameters.isCompressXref());

        PdfMixInputProcessStatus firstDocStatus = parameters.getFirstInput().newProcessingStatus(
                firstReader.getNumberOfPages());
        PdfMixInputProcessStatus secondDocStatus = parameters.getSecondInput().newProcessingStatus(
                secondReader.getNumberOfPages());

        int currentStep = 0;
        int totalSteps = firstReader.getNumberOfPages() + secondReader.getNumberOfPages();
        while (firstDocStatus.hasNextPage() || secondDocStatus.hasNextPage()) {
            for (int i = 0; i < parameters.getFirstInput().getStep() && firstDocStatus.hasNextPage(); i++) {
                copier.addPage(firstReader, firstDocStatus.nextPage());
                notifyEvent().stepsCompleted(++currentStep).outOf(totalSteps);
            }
            for (int i = 0; i < parameters.getSecondInput().getStep() && secondDocStatus.hasNextPage(); i++) {
                copier.addPage(secondReader, secondDocStatus.nextPage());
                notifyEvent().stepsCompleted(++currentStep).outOf(totalSteps);
            }
        }

        closeResources();

        outputWriter.flushSingleOutput(file(tmpFile).name(parameters.getOutputName()), parameters.getOutput(),
                parameters.isOverwrite());

        LOG.debug("Alternate mix with step first document {} and step second document {} completed.", parameters
                .getFirstInput().getStep(), parameters.getSecondInput().getStep());
    }

    public void after() {
        closeResources();
    }

    private void closeResources() {
        nullSafeClosePdfReader(firstReader);
        nullSafeClosePdfReader(secondReader);
        nullSafeClose(copier);
    }
}
