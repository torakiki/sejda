/*
 * Created on 26/ago/2011
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

import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryPdfBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.util.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.impl.itext.util.ITextUtils.nullSafeClosePdfReader;

import java.io.File;
import java.util.Set;

import org.sejda.core.exception.TaskException;
import org.sejda.core.exception.TaskExecutionException;
import org.sejda.core.manipulation.model.input.PdfSourceOpener;
import org.sejda.core.manipulation.model.parameter.ExtractPagesParameters;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.io.SingleOutputWriter;
import org.sejda.impl.itext.component.DefaultPdfCopier;
import org.sejda.impl.itext.component.PdfCopier;
import org.sejda.impl.itext.component.input.PdfSourceOpeners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.pdf.PdfReader;

/**
 * iText implementation of a task that extracts pages from a pdf source generating a single output pdf document containing the extracted pages.
 * 
 * @author Andrea Vacondio
 * 
 */
public class ExtractPagesTask implements Task<ExtractPagesParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(ExtractPagesTask.class);

    private SingleOutputWriter outputWriter = OutputWriters.newSingleOutputWriter();
    private PdfSourceOpener<PdfReader> sourceOpener;
    private PdfCopier copier = null;
    private PdfReader reader;

    public void before(ExtractPagesParameters parameters) {
        sourceOpener = PdfSourceOpeners.newPartialReadOpener();

    }

    public void execute(ExtractPagesParameters parameters) throws TaskException {
        File tmpFile = createTemporaryPdfBuffer();
        LOG.debug("Created output temporary buffer {} ", tmpFile);

        LOG.debug("Opening input {} ", parameters.getSource());
        reader = parameters.getSource().open(sourceOpener);

        Set<Integer> pages = parameters.getPages(reader.getNumberOfPages());
        if (pages == null || pages.isEmpty()) {
            throw new TaskExecutionException("No page has been selected for extraction.");
        }
        copier = new DefaultPdfCopier(reader, tmpFile, parameters.getVersion());
        LOG.trace("Created DefaultPdfCopier");

        int currentStep = 0;
        for (Integer page : pages) {
            LOG.trace("Adding page {}", page);
            copier.addPage(reader, page);
            notifyEvent().stepsCompleted(++currentStep).outOf(pages.size());
        }
        copier.freeReader(reader);

        closeResources();
        outputWriter.flushSingleOutput(file(tmpFile).name(parameters.getOutputName()), parameters.getOutput(),
                parameters.isOverwrite());
        LOG.debug("Pages extracted and written to {}", parameters.getOutput());

    }

    public void after() {
        closeResources();
    }

    private void closeResources() {
        nullSafeCloseQuietly(copier);
        nullSafeClosePdfReader(reader);
    }
}
