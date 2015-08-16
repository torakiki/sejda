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
import org.sejda.cli.model.RotateTaskCliArguments;
import org.sejda.model.parameter.RotateParameters;
import org.sejda.model.pdf.page.PredefinedSetOfPages;

/**
 * {@link CommandCliArgumentsTransformer} for the Rotate task command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class RotateCliArgumentsTransformer extends BaseCliArgumentsTransformer implements
        CommandCliArgumentsTransformer<RotateTaskCliArguments, RotateParameters> {

    /**
     * Transforms {@link RotateTaskCliArguments} to {@link RotateParameters}
     * 
     * @param taskCliArguments
     * @return populated task parameters
     */
    public RotateParameters toTaskParameters(RotateTaskCliArguments taskCliArguments) {
        RotateParameters parameters;
        if (taskCliArguments.isPredefinedPages()
                && taskCliArguments.getPredefinedPages().getEnumValue() != PredefinedSetOfPages.NONE) {
            parameters = new RotateParameters(taskCliArguments.getRotation().getEnumValue(), taskCliArguments.getPredefinedPages().getEnumValue());
        } else if (taskCliArguments.isPageSelection()) {
            parameters = new RotateParameters(taskCliArguments.getRotation().getEnumValue(), PredefinedSetOfPages.NONE);
            parameters.addAllPageRanges(taskCliArguments.getPageSelection().getPageRangeSet());
        } else {
            throw new ArgumentValidationException(
                    "Please specify at least one option that defines pages to be rotated");
        }
        populateAbstractParameters(parameters, taskCliArguments);
        populateSourceParameters(parameters, taskCliArguments);
        populateOutputTaskParameters(parameters, taskCliArguments);
        populateOutputPrefix(parameters, taskCliArguments);
        return parameters;
    }
}
