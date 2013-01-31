/*
 * Created on 30/dic/2012
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
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
        if (!taskCliArguments.isNumbering() && !taskCliArguments.isLabel()) {
            throw new SejdaRuntimeException("No header or footer definition, numbering or label required.");
        }
        SetHeaderFooterParameters parameters = new SetHeaderFooterParameters();
        parameters.setPageRange(taskCliArguments.getPageRange().getPageRange());
        if (taskCliArguments.isNumbering()) {
            parameters.setNumbering(taskCliArguments.getNumbering().getNumbering());
        }
        if (taskCliArguments.isLabel()) {
            parameters.setLabelPrefix(taskCliArguments.getLabel());
        }
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
