/*
 * Created on 28/lug/2011
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

import static org.sejda.core.manipulation.model.task.itext.util.ITextUtils.nullSafeClosePdfReader;

import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.model.input.PdfSourceOpener;
import org.sejda.core.manipulation.model.parameter.SimpleSplitParameters;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.manipulation.model.task.itext.component.input.PdfSourceOpeners;
import org.sejda.core.manipulation.model.task.itext.component.split.PagesPdfSplitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.pdf.PdfReader;

/**
 * Task splitting an input pdf document on a predefined set of pages defined in the type of split associated to the input parameter object.
 * 
 * @author Andrea Vacondio
 * 
 */
public class SimpleSplitTask implements Task<SimpleSplitParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleSplitTask.class);

    private PdfReader reader = null;
    private PdfSourceOpener<PdfReader> sourceOpener;
    private PagesPdfSplitter splitter;

    public void before(SimpleSplitParameters parameters) {
        sourceOpener = PdfSourceOpeners.newPartialReadOpener();
    }

    public void execute(SimpleSplitParameters parameters) throws TaskException {
        LOG.debug("Opening {} ...", parameters.getSource());
        reader = parameters.getSource().open(sourceOpener);

        splitter = new PagesPdfSplitter(reader).parameters(parameters).prefix(parameters.getOutputPrefix());
        LOG.debug("Starting simple split of type {} ...", parameters.getSplitType());
        splitter.pages(parameters.getSplitType().getPages(reader.getNumberOfPages())).split();

        LOG.debug("Input documents splitted and written to {}", parameters.getOutput());
    }

    public void after() {
        nullSafeClosePdfReader(reader);
        splitter = null;
    }

}
