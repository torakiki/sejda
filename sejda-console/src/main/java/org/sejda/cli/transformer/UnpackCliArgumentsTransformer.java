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

import org.sejda.cli.model.UnpackTaskCliArguments;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.UnpackParameters;

/**
 * {@link CommandCliArgumentsTransformer} for the Unpack task command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class UnpackCliArgumentsTransformer extends BaseCliArgumentsTransformer implements
        CommandCliArgumentsTransformer<UnpackTaskCliArguments, UnpackParameters> {

    /**
     * Transforms {@link UnpackTaskCliArguments} to {@link UnpackParameters}
     * 
     * @param taskCliArguments
     * @return populated task parameters
     */
    @Override
    public UnpackParameters toTaskParameters(UnpackTaskCliArguments taskCliArguments) {
        UnpackParameters parameters = new UnpackParameters(taskCliArguments.getOutput().getPdfDirectoryOutput());
        populateCommonParameters(parameters, taskCliArguments);
        parameters.setExistingOutputPolicy(taskCliArguments.getExistingOutput().getEnumValue());
        if(taskCliArguments.getOverwrite()) {
            parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        }
        populateSourceParameters(parameters, taskCliArguments);
        return parameters;
    }
}
