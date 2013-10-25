/*
 * Created on Oct 25, 2013
 * Copyright 2013 by Edi Weissmann (edi.weissmann@gmail.com).
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

import org.sejda.cli.model.ExtractTextByPagesTaskCliArguments;
import org.sejda.model.parameter.ExtractTextByPagesParameters;

/**
 * {@link org.sejda.cli.transformer.CommandCliArgumentsTransformer} for the ExtractTextByPages task command line interface
 *
 * @author Edi Weissmann
 *
 */
public class ExtractTextByPagesCliArgumentsTransformer extends BaseCliArgumentsTransformer implements
        CommandCliArgumentsTransformer<ExtractTextByPagesTaskCliArguments, ExtractTextByPagesParameters> {

    /**
     * Transforms {@link org.sejda.cli.model.ExtractTextTaskCliArguments} to {@link org.sejda.model.parameter.ExtractTextParameters}
     * 
     * @param taskCliArguments
     * @return populated task parameters
     */
    public ExtractTextByPagesParameters toTaskParameters(ExtractTextByPagesTaskCliArguments taskCliArguments) {
        final ExtractTextByPagesParameters parameters = new ExtractTextByPagesParameters();
        parameters.setOutput(taskCliArguments.getOutput().getPdfDirectoryOutput());
        populateOutputPrefix(parameters, taskCliArguments);

        parameters.setOverwrite(taskCliArguments.getOverwrite());
        parameters.setTextEncoding(taskCliArguments.getTextEncoding());
        parameters.addPages(taskCliArguments.getPageNumbers());

        populateSourceParameters(parameters, taskCliArguments);

        return parameters;
    }
}
