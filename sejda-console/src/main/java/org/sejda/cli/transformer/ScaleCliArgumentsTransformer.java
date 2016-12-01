/*
 * Created on 01 dic 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.cli.transformer;

import org.sejda.cli.model.ScaleTaskCliArguments;
import org.sejda.model.parameter.ScaleParameters;

/**
 * {@link CommandCliArgumentsTransformer} for the Scale task command line interface
 * 
 * @author Andrea Vacondio
 *
 */
public class ScaleCliArgumentsTransformer extends BaseCliArgumentsTransformer
        implements CommandCliArgumentsTransformer<ScaleTaskCliArguments, ScaleParameters> {

    @Override
    public ScaleParameters toTaskParameters(ScaleTaskCliArguments args) {
        ScaleParameters parameters = new ScaleParameters(args.getScale());
        parameters.setScaleType(args.getType().getEnumValue());

        populateAbstractParameters(parameters, args);
        populateSourceParameters(parameters, args);
        populateOutputTaskParameters(parameters, args);
        populateOutputPrefix(parameters, args);
        return parameters;
    }

}
