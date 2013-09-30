/*
 * Created on 26/set/2011
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
package org.sejda.core.service;

import org.junit.Ignore;
import org.sejda.model.image.ImageColorType;
import org.sejda.model.image.TiffCompressionType;
import org.sejda.model.parameter.image.PdfToMultipleTiffParameters;

/**
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class MultipleTiffConversionTaskTest extends
        MultipleImageConversionTaskTest<PdfToMultipleTiffParameters> {

    @Override
    PdfToMultipleTiffParameters getMultipleImageParametersWithoutSource() {
        PdfToMultipleTiffParameters parameters = new PdfToMultipleTiffParameters(ImageColorType.GRAY_SCALE);
        parameters.setCompressionType(TiffCompressionType.PACKBITS);
        parameters.setOutputPrefix("[CURRENTPAGE]");
        parameters.setResolutionInDpi(96);
        parameters.setOverwrite(true);
        return parameters;
    }

}
