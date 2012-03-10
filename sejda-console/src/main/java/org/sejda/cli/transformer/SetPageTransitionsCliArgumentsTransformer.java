/*
 * Created on Sep 21, 2011
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

import org.sejda.cli.model.SetPageTransitionsTaskCliArguments;
import org.sejda.cli.model.adapter.PageNumberWithPdfPageTransitionAdapter;
import org.sejda.model.parameter.SetPagesTransitionParameters;

/**
 * {@link CommandCliArgumentsTransformer} for the SetPageTransitions task command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class SetPageTransitionsCliArgumentsTransformer extends BaseCliArgumentsTransformer implements
        CommandCliArgumentsTransformer<SetPageTransitionsTaskCliArguments, SetPagesTransitionParameters> {

    /**
     * Transforms {@link SetPageTransitionsTaskCliArguments} to {@link SetPagesTransitionParameters}
     * 
     * @param taskCliArguments
     * @return populated task parameters
     */
    public SetPagesTransitionParameters toTaskParameters(SetPageTransitionsTaskCliArguments taskCliArguments) {
        SetPagesTransitionParameters parameters;
        if (taskCliArguments.isDefaultTransition()) {
            parameters = new SetPagesTransitionParameters(taskCliArguments.getDefaultTransition()
                    .getPdfPageTransition());
        } else {
            parameters = new SetPagesTransitionParameters();
        }

        parameters.setFullScreen(taskCliArguments.isFullscreen());

        if (taskCliArguments.isTransitions()) {
            for (PageNumberWithPdfPageTransitionAdapter each : taskCliArguments.getTransitions()) {
                parameters.putTransition(each.getPageNumber(), each.getPdfPageTransition());
            }
        }

        populateSourceParameters(parameters, taskCliArguments);
        populateOutputTaskParameters(parameters, taskCliArguments);
        populateAbstractParameters(parameters, taskCliArguments);

        return parameters;
    }
}
