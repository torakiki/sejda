/*
 * Created on 09/ago/2011
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

import static org.sejda.impl.itext.util.ITextUtils.nullSafeClosePdfReader;

import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.model.input.PdfSourceOpener;
import org.sejda.core.manipulation.model.outline.OutlineGoToPageDestinations;
import org.sejda.core.manipulation.model.parameter.SplitByGoToActionLevelParameters;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.impl.itext.component.ITextOutlineHandler;
import org.sejda.impl.itext.component.input.PdfSourceOpeners;
import org.sejda.impl.itext.component.split.GoToPageDestinationsPdfSplitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.pdf.PdfReader;

/**
 * Task splitting an input pdf document on a set of pages given by a GoTo Action level defined in the input parameter.
 * 
 * @author Andrea Vacondio
 * 
 */
public class SplitByGoToActionLevelTask implements Task<SplitByGoToActionLevelParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(SplitByGoToActionLevelTask.class);

    private PdfReader reader = null;
    private PdfSourceOpener<PdfReader> sourceOpener;
    private GoToPageDestinationsPdfSplitter splitter;

    public void before(SplitByGoToActionLevelParameters parameters) {
        sourceOpener = PdfSourceOpeners.newPartialReadOpener();
    }

    public void execute(SplitByGoToActionLevelParameters parameters) throws TaskException {
        LOG.debug("Opening {} ", parameters.getSource());
        reader = parameters.getSource().open(sourceOpener);

        LOG.debug("Retrieving outline information for level {}", parameters.getLevelToSplitAt());
        OutlineGoToPageDestinations goToPagesDestination = new ITextOutlineHandler(reader,
                parameters.getMatchingTitleRegEx())
                .getGoToPageDestinationForActionLevel(parameters.getLevelToSplitAt());
        splitter = new GoToPageDestinationsPdfSplitter(reader, parameters, goToPagesDestination);
        LOG.debug("Starting split by GoTo Action level for {} ", parameters);
        splitter.split();

        LOG.debug("Input documents splitted and written to {}", parameters.getOutput());
    }

    public void after() {
        nullSafeClosePdfReader(reader);
        splitter = null;
    }
}
