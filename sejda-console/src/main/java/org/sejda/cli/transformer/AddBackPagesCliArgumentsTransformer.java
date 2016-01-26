/*
 * Created on 21 gen 2016
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

import org.sejda.cli.exception.ArgumentValidationException;
import org.sejda.cli.model.AddBackPagesTaskCliArguments;
import org.sejda.model.parameter.AddBackPagesParameters;

/**
 * {@link CommandCliArgumentsTransformer} for the AddBackPages task command line interface
 * 
 * @author Andrea Vacondio
 *
 */
public class AddBackPagesCliArgumentsTransformer extends BaseCliArgumentsTransformer
        implements CommandCliArgumentsTransformer<AddBackPagesTaskCliArguments, AddBackPagesParameters> {

    @Override
    public AddBackPagesParameters toTaskParameters(AddBackPagesTaskCliArguments taskCliArguments) {
        AddBackPagesParameters parameters = new AddBackPagesParameters();
        populateAbstractParameters(parameters, taskCliArguments);
        populateSourceParameters(parameters, taskCliArguments);
        populateOutputTaskParameters(parameters, taskCliArguments);
        populateOutputPrefix(parameters, taskCliArguments);
        if (taskCliArguments.isPageSelection()) {
            parameters.addAllPageRanges(taskCliArguments.getPageSelection().getPageRangeSet());
        }
        parameters.setStep(taskCliArguments.getStep());
        if (taskCliArguments.getBackPagesSource().size() != 1) {
            throw new ArgumentValidationException(
                    "Only one back pages file expected, received " + taskCliArguments.getFiles().size());
        }
        parameters.setBackPagesSource(taskCliArguments.getBackPagesSource().get(0).getPdfFileSource());
        return parameters;
    }

}
