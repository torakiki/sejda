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
package org.sejda.core.manipulation.model.task.itext;

import static org.sejda.core.manipulation.model.task.itext.util.ITextUtils.nullSafeClosePdfCopyHandler;
import static org.sejda.core.manipulation.model.task.itext.util.ITextUtils.nullSafeClosePdfReader;
import static org.sejda.core.manipulation.model.task.itext.util.PdfReaderUtils.openReader;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.model.FileOutput.file;

import java.io.File;

import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.model.input.PdfMixInput;
import org.sejda.core.manipulation.model.parameter.AlternateMixParameters;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.manipulation.model.task.itext.component.PdfCopyHandler;
import org.sejda.core.support.io.SingleOutputWriterSupport;
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
    private PdfCopyHandler copyHandler = null;
    private SingleOutputWriterSupport outputWriter;

    public void before(AlternateMixParameters parameters) {
        outputWriter = new SingleOutputWriterSupport();
    }

    public void execute(AlternateMixParameters parameters) throws TaskException {
        LOG.debug("Opening first input {} ...", parameters.getFirstInput().getSource());
        firstReader = openReader(parameters.getFirstInput().getSource());
        LOG.debug("Opening second input {} ...", parameters.getSecondInput().getSource());
        secondReader = openReader(parameters.getSecondInput().getSource());

        File tmpFile = outputWriter.createTemporaryPdfBuffer();
        LOG.debug("Created output on temporary buffer {} ...", tmpFile);
        copyHandler = new PdfCopyHandler(firstReader, tmpFile, parameters.getVersion());

        copyHandler.setCompressionOnCopier(parameters.isCompressXref());

        PdfMixProcessStatus firstDocStatus = new PdfMixProcessStatus(parameters.getFirstInput(),
                firstReader.getNumberOfPages());
        PdfMixProcessStatus secondDocStatus = new PdfMixProcessStatus(parameters.getSecondInput(),
                secondReader.getNumberOfPages());

        int currentStep = 0;
        int totalSteps = firstReader.getNumberOfPages() + secondReader.getNumberOfPages();
        while (firstDocStatus.hasNextPage() || secondDocStatus.hasNextPage()) {
            for (int i = 0; i < parameters.getFirstInput().getStep() && firstDocStatus.hasNextPage(); i++) {
                copyHandler.addPage(firstReader, firstDocStatus.nextPage());
                notifyEvent().stepsCompleted(++currentStep).outOf(totalSteps);
            }
            for (int i = 0; i < parameters.getSecondInput().getStep() && secondDocStatus.hasNextPage(); i++) {
                copyHandler.addPage(secondReader, secondDocStatus.nextPage());
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

    /**
     * Close readers and writer
     */
    private void closeResources() {
        nullSafeClosePdfReader(firstReader);
        nullSafeClosePdfReader(secondReader);
        nullSafeClosePdfCopyHandler(copyHandler);
    }

    /**
     * Holds the status of the process on a {@link PdfMixInput}
     * 
     * @author Andrea Vacondio
     * 
     */
    private static class PdfMixProcessStatus {

        private boolean reverse;
        private int currentPage;
        private int numberOfPages;

        PdfMixProcessStatus(PdfMixInput input, int numberOfPages) {
            this.reverse = input.isReverse();
            this.numberOfPages = numberOfPages;
            this.currentPage = (input.isReverse()) ? numberOfPages : 1;
        }

        /**
         * @return the next page number
         */
        int nextPage() {
            int retVal = currentPage;
            if (reverse) {
                currentPage--;
            } else {
                currentPage++;
            }
            return retVal;
        }

        /**
         * @return true if there is another page to be processed
         */
        boolean hasNextPage() {
            return (currentPage > 0 && currentPage <= numberOfPages);
        }
    }
}
