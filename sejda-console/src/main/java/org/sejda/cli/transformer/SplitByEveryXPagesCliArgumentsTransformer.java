/*
 * Created on 11/giu/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import org.sejda.cli.model.SplitByEveryXPagesTaskCliArguments;
import org.sejda.model.parameter.SplitByEveryXPagesParameters;

/**
 * {@link CommandCliArgumentsTransformer} for the SplitByEveryXPages task command line interface
 * 
 * @author Andrea Vacondio
 * 
 */
public class SplitByEveryXPagesCliArgumentsTransformer extends BaseCliArgumentsTransformer implements
        CommandCliArgumentsTransformer<SplitByEveryXPagesTaskCliArguments, SplitByEveryXPagesParameters> {

    /**
     * Transforms {@link SplitByEveryXPagesTaskCliArguments} to {@link SplitByEveryXPagesParameters}
     * 
     * @param taskCliArguments
     * @return populated task parameters
     */
    @Override
    public SplitByEveryXPagesParameters toTaskParameters(SplitByEveryXPagesTaskCliArguments taskCliArguments) {
        SplitByEveryXPagesParameters parameters = new SplitByEveryXPagesParameters(taskCliArguments.getPages());

        populateAbstractParameters(parameters, taskCliArguments);
        populateSourceParameters(parameters, taskCliArguments);
        populateOutputTaskParameters(parameters, taskCliArguments);
        populateOutputPrefix(parameters, taskCliArguments);
        populateOptimizableOutputParameters(parameters, taskCliArguments);
        populateDiscardableOutlineParameters(parameters, taskCliArguments);

        return parameters;
    }

}
