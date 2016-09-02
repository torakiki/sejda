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

import org.sejda.cli.model.NupTaskCliArguments;
import org.sejda.model.nup.PageOrder;
import org.sejda.model.parameter.NupParameters;

public class NupCliArgumentsTransformer extends BaseCliArgumentsTransformer
        implements CommandCliArgumentsTransformer<NupTaskCliArguments, NupParameters> {

    @Override
    public NupParameters toTaskParameters(NupTaskCliArguments taskCliArguments) {
        PageOrder pageOrder = PageOrder.HORIZONTAL;
        if (taskCliArguments.isVerticalOrdering()) {
            pageOrder = PageOrder.VERTICAL;
        }

        NupParameters parameters = new NupParameters(taskCliArguments.getN(), pageOrder);
        parameters.setPreservePageSize(taskCliArguments.isPreservePageSize());
        populateAbstractParameters(parameters, taskCliArguments);
        populateSourceParameters(parameters, taskCliArguments);
        populateOutputTaskParameters(parameters, taskCliArguments);
        populateOutputPrefix(parameters, taskCliArguments);

        return parameters;
    }
}
