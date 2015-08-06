/*
 * Created on 30/dic/2012
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.cli.transformer;

import org.sejda.cli.model.SetHeaderFooterTaskCliArguments;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.parameter.SetHeaderFooterParameters;

/**
 * {@link CommandCliArgumentsTransformer} for the SetHeaderFooter task command line interface
 * 
 * @author Andrea Vacondio
 * 
 */
public class SetHeaderFooterCliArgumentsTransformer extends BaseCliArgumentsTransformer implements
        CommandCliArgumentsTransformer<SetHeaderFooterTaskCliArguments, SetHeaderFooterParameters> {

    public SetHeaderFooterParameters toTaskParameters(SetHeaderFooterTaskCliArguments taskCliArguments) {
        if (taskCliArguments.getLabel() == null) {
            throw new SejdaRuntimeException("Please specify the text label to apply");
        }
        SetHeaderFooterParameters parameters = new SetHeaderFooterParameters();
        parameters.setPageRange(taskCliArguments.getPageRange().getPageRange());
        parameters.setPattern(taskCliArguments.getLabel());
        populateAlignment(taskCliArguments, parameters);
        populateFont(taskCliArguments, parameters);
        populateSourceParameters(parameters, taskCliArguments);
        populateOutputTaskParameters(parameters, taskCliArguments);
        populateAbstractParameters(parameters, taskCliArguments);
        return parameters;
    }

    private void populateAlignment(SetHeaderFooterTaskCliArguments taskCliArguments,
            SetHeaderFooterParameters parameters) {
        if (taskCliArguments.isHorizontalAlign()) {
            parameters.setHorizontalAlign(taskCliArguments.getHorizontalAlign().getEnumValue());
        }
        if (taskCliArguments.isVerticalAlign()) {
            parameters.setVerticalAlign(taskCliArguments.getVerticalAlign().getEnumValue());
        }
    }

    private void populateFont(SetHeaderFooterTaskCliArguments taskCliArguments, SetHeaderFooterParameters parameters) {
        if (taskCliArguments.isFontSize()) {
            parameters.setFontSize(taskCliArguments.getFontSize());
        }
        if (taskCliArguments.isFont()) {
            parameters.setFont(taskCliArguments.getFont().getEnumValue());
        }
    }

}
