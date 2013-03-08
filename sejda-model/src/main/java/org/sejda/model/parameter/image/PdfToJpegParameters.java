/*
 * Created on 01/mar/2013
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
package org.sejda.model.parameter.image;

import org.sejda.model.image.ImageColorType;
import org.sejda.model.image.ImageType;

/**
 * Parameter meant to convert an existing pdf source to JPEG images.
 * 
 * @author Andrea Vacondio
 * 
 */
public class PdfToJpegParameters extends AbstractPdfToMultipleImageParameters {

    public PdfToJpegParameters() {
        super(ImageColorType.COLOR_RGB);
    }

    @Override
    public ImageType getOutputImageType() {
        return ImageType.JPEG;
    }

}
