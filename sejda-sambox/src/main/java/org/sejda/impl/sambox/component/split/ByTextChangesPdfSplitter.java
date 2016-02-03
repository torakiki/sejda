/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com)
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox.component.split;

import org.sejda.core.support.prefix.model.NameGenerationRequest;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.parameter.SplitByTextContentParameters;
import org.sejda.model.split.NextOutputStrategy;
import org.sejda.sambox.pdmodel.PDDocument;

/**
 * Splitter implementation that splits a document when text at a given area changes
 * 
 * @author Eduard Weissmann
 * 
 */
public class ByTextChangesPdfSplitter extends AbstractPdfSplitter<SplitByTextContentParameters> {
    private SplitByTextChangesOutputStrategy outputStrategy;

    public ByTextChangesPdfSplitter(PDDocument document, SplitByTextContentParameters parameters, boolean optimize)
            throws TaskIOException {
        super(document, parameters, optimize);
        this.outputStrategy = new SplitByTextChangesOutputStrategy(document, parameters.getTextArea(),
                parameters.getStartsWith(), parameters.getEndsWith());
    }

    @Override
    NameGenerationRequest enrichNameGenerationRequest(NameGenerationRequest request) {
        return request.text(outputStrategy.getTextByPage(request.getPage()));
    }

    @Override
    NextOutputStrategy nextOutputStrategy() {
        return outputStrategy;
    }
}
