/*
 * Created on Sep 21, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
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
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.cli.transformer;

import org.sejda.cli.model.SetPageTransitionsTaskCliArguments;
import org.sejda.conversion.PageNumberWithPdfPageTransitionAdapter;
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
    @Override
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
