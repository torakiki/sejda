/*
 * Created on Sep 12, 2011
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

import org.sejda.cli.model.SplitByPagesTaskCliArguments;
import org.sejda.model.parameter.SplitByPagesParameters;

/**
 * {@link CommandCliArgumentsTransformer} for the SplitByPages task command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class SplitByPagesCliArgumentsTransformer extends BaseCliArgumentsTransformer implements
        CommandCliArgumentsTransformer<SplitByPagesTaskCliArguments, SplitByPagesParameters> {

    /**
     * Transforms {@link SplitByPagesTaskCliArguments} to {@link SplitByPagesParameters}
     * 
     * @param taskCliArguments
     * @return populated task parameters
     */
    @Override
    public SplitByPagesParameters toTaskParameters(SplitByPagesTaskCliArguments taskCliArguments) {
        SplitByPagesParameters parameters = new SplitByPagesParameters();
        parameters.addPages(taskCliArguments.getPageNumbers());

        populateAbstractParameters(parameters, taskCliArguments);
        populateSourceParameters(parameters, taskCliArguments);
        populateOutputTaskParameters(parameters, taskCliArguments);
        populateOutputPrefix(parameters, taskCliArguments);
        populateOptimizableOutputParameters(parameters, taskCliArguments);
        populateDiscardableOutlineParameters(parameters, taskCliArguments);

        return parameters;
    }
}
