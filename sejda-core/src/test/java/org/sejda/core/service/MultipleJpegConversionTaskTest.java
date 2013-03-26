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

import java.io.InputStream;
import java.math.BigDecimal;

import org.junit.Ignore;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.parameter.image.PdfToJpegParameters;

/**
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class MultipleJpegConversionTaskTest extends MultipleImageConversionTaskTest<PdfToJpegParameters> {

    @Override
    PdfToJpegParameters getMultipleImageParameters() {
        PdfToJpegParameters parameters = new PdfToJpegParameters();
        parameters.setOutputPrefix("[CURRENTPAGE]");
        parameters.setResolutionInDpi(300);
        parameters.setUserZoom(new BigDecimal("1.5"));
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/enc_test_test_file.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceWithPassword(stream, "enc_test_test_file.pdf", "test");
        parameters.setSource(source);
        parameters.setOverwrite(true);
        return parameters;
    }

}
