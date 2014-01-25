/*
 * Created on Sep 30, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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

import org.sejda.cli.model.CropTaskCliArguments;
import org.sejda.conversion.RectangularBoxAdapter;
import org.sejda.model.parameter.CropParameters;

/**
 * {@link CommandCliArgumentsTransformer} for the Crop task command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class CropCliArgumentsTransformer extends BaseCliArgumentsTransformer implements
        CommandCliArgumentsTransformer<CropTaskCliArguments, CropParameters> {

    /**
     * Transforms {@link CropTaskCliArguments} to {@link CropParameters}
     * 
     * @param taskCliArguments
     * @return populated task parameters
     */
    public CropParameters toTaskParameters(CropTaskCliArguments taskCliArguments) {
        CropParameters parameters = new CropParameters();

        for (RectangularBoxAdapter cropArea : taskCliArguments.getCropAreas()) {
            parameters.addCropArea(cropArea.getRectangularBox());
        }

        populateSourceParameters(parameters, taskCliArguments);
        populateOutputTaskParameters(parameters, taskCliArguments);
        populateAbstractParameters(parameters, taskCliArguments);

        return parameters;
    }
}
