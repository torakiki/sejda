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

import org.sejda.cli.model.ExtractTextTaskCliArguments;
import org.sejda.core.manipulation.model.parameter.ExtractTextParameters;

/**
 * {@link CommandCliArgumentsTransformer} for the ExtractText task command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class ExtractTextCliArgumentsTransformer extends BaseCliArgumentsTransformer implements
        CommandCliArgumentsTransformer<ExtractTextTaskCliArguments, ExtractTextParameters> {

    /**
     * Transforms {@link ExtractTextTaskCliArguments} to {@link ExtractTextParameters}
     * 
     * @param taskCliArguments
     * @return populated task parameters
     */
    public ExtractTextParameters toTaskParameters(ExtractTextTaskCliArguments taskCliArguments) {
        final ExtractTextParameters parameters = new ExtractTextParameters(taskCliArguments.getOutput()
                .getPdfDirectoryOutput());
        populateOutputPrefix(parameters, taskCliArguments);

        parameters.setOverwrite(taskCliArguments.getOverwrite());
        parameters.setTextEncoding(taskCliArguments.getTextEncoding());

        populateSourceParameters(parameters, taskCliArguments);

        return parameters;
    }
}
