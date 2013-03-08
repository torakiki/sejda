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
package org.sejda.model.parameter.image;

import org.junit.Test;
import org.sejda.TestUtils;

/**
 * @author Andrea Vacondio
 * 
 */
public class PdfToJpegParametersTest {
    @Test
    public void testEquals() {
        PdfToJpegParameters eq1 = new PdfToJpegParameters();
        PdfToJpegParameters eq2 = new PdfToJpegParameters();
        PdfToJpegParameters eq3 = new PdfToJpegParameters();
        PdfToJpegParameters diff = new PdfToJpegParameters();
        diff.setResolutionInDpi(120);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }
}
