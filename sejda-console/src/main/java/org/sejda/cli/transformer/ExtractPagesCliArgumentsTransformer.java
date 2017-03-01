/*
 * Created on Jul 1, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.cli.transformer;

import org.sejda.cli.exception.ArgumentValidationException;
import org.sejda.cli.model.ExtractPagesTaskCliArguments;
import org.sejda.model.parameter.ExtractPagesParameters;
import org.sejda.model.pdf.page.PredefinedSetOfPages;

/**
 * {@link CommandCliArgumentsTransformer} for the ExtractPages task command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class ExtractPagesCliArgumentsTransformer extends BaseCliArgumentsTransformer implements
        CommandCliArgumentsTransformer<ExtractPagesTaskCliArguments, ExtractPagesParameters> {

    /**
     * Transforms {@link ExtractPagesTaskCliArguments} to {@link ExtractPagesParameters}
     * 
     * @param taskCliArguments
     * @return populated task parameters
     */
    @Override
    public ExtractPagesParameters toTaskParameters(ExtractPagesTaskCliArguments taskCliArguments) {
        final ExtractPagesParameters parameters;
        if (taskCliArguments.isPredefinedPages()
                && taskCliArguments.getPredefinedPages().getEnumValue() != PredefinedSetOfPages.NONE) {
            parameters = new ExtractPagesParameters(taskCliArguments.getPredefinedPages().getEnumValue());
        } else if (taskCliArguments.isPageSelection()) {
            parameters = new ExtractPagesParameters();
            parameters.addAllPageRanges(taskCliArguments.getPageSelection().getPageRangeSet());
        } else {
            throw new ArgumentValidationException(
                    "Please specify at least one option that defines pages to be extracted");
        }
        populateAbstractParameters(parameters, taskCliArguments);
        populateSourceParameters(parameters, taskCliArguments);
        populateOutputTaskParameters(parameters, taskCliArguments);
        populateOutputPrefix(parameters, taskCliArguments);
        populateOptimizableOutputParameters(parameters, taskCliArguments);
        populateDiscardableOutlineParameters(parameters, taskCliArguments);

        return parameters;
    }
}
