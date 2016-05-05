/*
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

import org.sejda.cli.model.SplitByTextTaskCliArguments;
import org.sejda.model.TopLeftRectangularBox;
import org.sejda.model.parameter.SplitByTextContentParameters;

public class SplitByTextCliArgumentsTransformer extends BaseCliArgumentsTransformer implements
        CommandCliArgumentsTransformer<SplitByTextTaskCliArguments, SplitByTextContentParameters> {

    @Override
    public SplitByTextContentParameters toTaskParameters(SplitByTextTaskCliArguments taskCliArguments) {
        TopLeftRectangularBox box = new TopLeftRectangularBox(taskCliArguments.getTop(), taskCliArguments.getLeft(), taskCliArguments.getWidth(), taskCliArguments.getHeight());
        SplitByTextContentParameters parameters = new SplitByTextContentParameters(box);

        populateAbstractParameters(parameters, taskCliArguments);
        populateSourceParameters(parameters, taskCliArguments);
        populateOutputTaskParameters(parameters, taskCliArguments);
        populateOutputPrefix(parameters, taskCliArguments);
        populateOptimizableOutputParameters(parameters, taskCliArguments);
        populateDiscardableOutlineParameters(parameters, taskCliArguments);

        if (taskCliArguments.isStartsWith()) {
            parameters.setStartsWith(taskCliArguments.getStartsWith());
        }

        return parameters;
    }
}
