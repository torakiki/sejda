/*
 * Created on Jul 1, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
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
