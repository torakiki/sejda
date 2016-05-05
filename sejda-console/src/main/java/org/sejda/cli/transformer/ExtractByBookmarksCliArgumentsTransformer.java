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

import org.sejda.cli.model.ExtractByBookmarksTaskCliArguments;
import org.sejda.model.parameter.ExtractByOutlineParameters;

/**
 * {@link CommandCliArgumentsTransformer} for the ExtractByOutline task command line interface
 *
 */
public class ExtractByBookmarksCliArgumentsTransformer extends BaseCliArgumentsTransformer implements
        CommandCliArgumentsTransformer<ExtractByBookmarksTaskCliArguments, ExtractByOutlineParameters> {

    @Override
    public ExtractByOutlineParameters toTaskParameters(ExtractByBookmarksTaskCliArguments taskCliArguments) {
        ExtractByOutlineParameters parameters = new ExtractByOutlineParameters(
                taskCliArguments.getBookmarkLevel());

        if (taskCliArguments.isMatchingRegEx()) {
            parameters.setMatchingTitleRegEx(taskCliArguments.getMatchingRegEx());
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
