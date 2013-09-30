/*
 * Created on 08/mar/2013
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
import org.sejda.model.parameter.image.PdfToJpegParameters;

/**
 * @author Andrea Vacondio
 *
 */
@Ignore
public abstract class MultipleJpegConversionTaskTest extends MultipleImageConversionTaskTest<PdfToJpegParameters> {

    @Override
    PdfToJpegParameters getMultipleImageParametersWithoutSource() {
        PdfToJpegParameters parameters = new PdfToJpegParameters();
        parameters.setOutputPrefix("[CURRENTPAGE]");
        parameters.setResolutionInDpi(300);
        parameters.setUserZoom(1.5f);
        parameters.setOverwrite(true);
        return parameters;
    }

}
