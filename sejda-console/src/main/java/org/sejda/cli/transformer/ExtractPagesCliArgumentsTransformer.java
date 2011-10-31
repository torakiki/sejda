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
import org.sejda.cli.model.ExtractPagesTaskCliArguments;
import org.sejda.model.parameter.ExtractPagesParameters;

/**
 * {@link CommandCliArgumentsTransformer} for the ExtractPages task command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class ExtractPagesCliArgumentsTransformer extends BaseCliArgumentsTransformer implements
        CommandCliArgumentsTransformer<ExtractPagesTaskCliArguments, ExtractPagesParameters> {

    /**
     * Transforms {@link ExtractPagesTaskCliArguments} to {@link ExtractPagesParameters}
     * 
     * @param taskCliArguments
     * @return populated task parameters
     */
    public ExtractPagesParameters toTaskParameters(ExtractPagesTaskCliArguments taskCliArguments) {
        final ExtractPagesParameters parameters;
        if (taskCliArguments.isPredefinedPages()) {
            parameters = new ExtractPagesParameters(taskCliArguments.getPredefinedPages().getEnumValue());
        } else if (taskCliArguments.isPageSelection()) {
            parameters = new ExtractPagesParameters(taskCliArguments.getPageSelection().getPageRangeSet());
        } else {
            throw new ArgumentValidationException(
                    "Please specify at least one option that defines pages to be extracted");
        }
        populateAbstractParameters(parameters, taskCliArguments);
        populateSourceParameters(parameters, taskCliArguments);

        return parameters;
    }
}
