/*
 * Created on Oct 25, 2013
 * Copyright 2013 by Edi Weissmann (edi.weissmann@gmail.com).
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

import org.sejda.cli.model.ExtractTextByPagesTaskCliArguments;
import org.sejda.model.parameter.ExtractTextByPagesParameters;

/**
 * {@link org.sejda.cli.transformer.CommandCliArgumentsTransformer} for the ExtractTextByPages task command line interface
 * 
 * @author Edi Weissmann
 * 
 */
public class ExtractTextByPagesCliArgumentsTransformer extends BaseCliArgumentsTransformer implements
        CommandCliArgumentsTransformer<ExtractTextByPagesTaskCliArguments, ExtractTextByPagesParameters> {

    /**
     * Transforms {@link org.sejda.cli.model.ExtractTextTaskCliArguments} to {@link org.sejda.model.parameter.ExtractTextParameters}
     * 
     * @param taskCliArguments
     * @return populated task parameters
     */
    @Override
    public ExtractTextByPagesParameters toTaskParameters(ExtractTextByPagesTaskCliArguments taskCliArguments) {
        final ExtractTextByPagesParameters parameters = new ExtractTextByPagesParameters();
        populateCommonMultipleOutputParameters(parameters, taskCliArguments);
        populateOutputPrefix(parameters, taskCliArguments);

        parameters.setTextEncoding(taskCliArguments.getTextEncoding());
        if (taskCliArguments.isPageSelection()) {
            parameters.addAllPageRanges(taskCliArguments.getPageSelection().getPageRangeSet());
        }

        populateSourceParameters(parameters, taskCliArguments);

        return parameters;
    }
}
