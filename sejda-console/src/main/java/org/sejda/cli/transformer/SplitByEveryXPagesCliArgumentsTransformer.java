/*
 * Created on 11/giu/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import org.sejda.cli.model.SplitByEveryXPagesTaskCliArguments;
import org.sejda.model.parameter.SplitByEveryXPagesParameters;

/**
 * {@link CommandCliArgumentsTransformer} for the SplitByEveryXPages task command line interface
 * 
 * @author Andrea Vacondio
 * 
 */
public class SplitByEveryXPagesCliArgumentsTransformer extends BaseCliArgumentsTransformer implements
        CommandCliArgumentsTransformer<SplitByEveryXPagesTaskCliArguments, SplitByEveryXPagesParameters> {

    /**
     * Transforms {@link SplitByEveryXPagesTaskCliArguments} to {@link SplitByEveryXPagesParameters}
     * 
     * @param taskCliArguments
     * @return populated task parameters
     */
    public SplitByEveryXPagesParameters toTaskParameters(SplitByEveryXPagesTaskCliArguments taskCliArguments) {
        SplitByEveryXPagesParameters parameters = new SplitByEveryXPagesParameters(taskCliArguments.getPages());

        populateAbstractParameters(parameters, taskCliArguments);
        populateSourceParameters(parameters, taskCliArguments);
        populateOutputTaskParameters(parameters, taskCliArguments);
        populateOutputPrefix(parameters, taskCliArguments);

        return parameters;
    }

}
