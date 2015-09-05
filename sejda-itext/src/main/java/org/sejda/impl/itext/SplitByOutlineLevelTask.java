/*
 * Created on 09/ago/2011
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import static org.sejda.impl.itext.util.ITextUtils.nullSafeClosePdfReader;

import org.sejda.impl.itext.component.ITextOutlineLevelsHandler;
import org.sejda.impl.itext.component.input.PdfSourceOpeners;
import org.sejda.impl.itext.component.split.PageDestinationsLevelPdfSplitter;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.outline.OutlinePageDestinations;
import org.sejda.model.parameter.SplitByOutlineLevelParameters;
import org.sejda.model.task.BaseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.pdf.PdfReader;

/**
 * Task splitting an input pdf document on a set of pages given by an outline level defined in the input parameter.
 * 
 * @author Andrea Vacondio
 * 
 */
public class SplitByOutlineLevelTask extends BaseTask<SplitByOutlineLevelParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(SplitByOutlineLevelTask.class);

    private PdfReader reader = null;
    private PdfSourceOpener<PdfReader> sourceOpener;
    private PageDestinationsLevelPdfSplitter splitter;

    @Override
    public void before(SplitByOutlineLevelParameters parameters) {
        sourceOpener = PdfSourceOpeners.newPartialReadOpener();
    }

    @Override
    public void execute(SplitByOutlineLevelParameters parameters) throws TaskException {
        LOG.debug("Opening {} ", parameters.getSource());
        reader = parameters.getSource().open(sourceOpener);

        LOG.debug("Retrieving outline information for level {}", parameters.getLevelToSplitAt());
        OutlinePageDestinations goToPagesDestination = new ITextOutlineLevelsHandler(reader,
                parameters.getMatchingTitleRegEx())
                .getPageDestinationsForLevel(parameters.getLevelToSplitAt());
        splitter = new PageDestinationsLevelPdfSplitter(reader, parameters, goToPagesDestination);
        LOG.debug("Starting split by outline level for {} ", parameters);
        splitter.split(getNotifiableTaskMetadata());

        LOG.debug("Input documents splitted and written to {}", parameters.getOutput());
    }

    @Override
    public void after() {
        nullSafeClosePdfReader(reader);
        splitter = null;
    }
}
