/*
 * Created on Sep 12, 2011
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

import org.sejda.cli.model.SimpleSplitTaskCliArguments;
import org.sejda.model.parameter.SimpleSplitParameters;

/**
 * {@link CommandCliArgumentsTransformer} for the SimpleSplit task command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class SimpleSplitCliArgumentsTransformer extends BaseCliArgumentsTransformer implements
        CommandCliArgumentsTransformer<SimpleSplitTaskCliArguments, SimpleSplitParameters> {

    /**
     * Transforms {@link SimpleSplitTaskCliArguments} to {@link SimpleSplitParameters}
     * 
     * @param taskCliArguments
     * @return populated task parameters
     */
    public SimpleSplitParameters toTaskParameters(SimpleSplitTaskCliArguments taskCliArguments) {
        SimpleSplitParameters parameters = new SimpleSplitParameters(taskCliArguments.getPredefinedPages()
                .getEnumValue());

        populateAbstractParameters(parameters, taskCliArguments);
        populateSourceParameters(parameters, taskCliArguments);
        populateOutputTaskParameters(parameters, taskCliArguments);
        populateOutputPrefix(parameters, taskCliArguments);

        return parameters;
    }
}
