/*
 * Created on 18/set/2011
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
import org.sejda.model.image.ImageColorType;
import org.sejda.model.image.TiffCompressionType;
import org.sejda.model.pdf.page.PageRange;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Andrea Vacondio
 * 
 */
public class PdfToMultipleTiffParametersTest {

    @Test
    public void testEquals() {
        PdfToMultipleTiffParameters eq1 = new PdfToMultipleTiffParameters(ImageColorType.GRAY_SCALE);
        PdfToMultipleTiffParameters eq2 = new PdfToMultipleTiffParameters(ImageColorType.GRAY_SCALE);
        PdfToMultipleTiffParameters eq3 = new PdfToMultipleTiffParameters(ImageColorType.GRAY_SCALE);
        PdfToMultipleTiffParameters diff = new PdfToMultipleTiffParameters(ImageColorType.BLACK_AND_WHITE);
        diff.setCompressionType(TiffCompressionType.JPEG_TTN2);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void testGetPageSelection() {
        PdfToMultipleTiffParameters victim = new PdfToMultipleTiffParameters(ImageColorType.GRAY_SCALE);
        assertTrue(victim.getPageSelection().isEmpty());
        PdfToMultipleTiffParameters victim2 = new PdfToMultipleTiffParameters(ImageColorType.GRAY_SCALE);
        victim2.addPageRange(new PageRange(12));
        assertFalse(victim2.getPageSelection().isEmpty());
    }

    @Test
    public void getPages() {
        PdfToMultipleTiffParameters victim = new PdfToMultipleTiffParameters(ImageColorType.GRAY_SCALE);
        assertEquals(10, victim.getPages(10).size());
        PdfToMultipleTiffParameters victim2 = new PdfToMultipleTiffParameters(ImageColorType.GRAY_SCALE);
        victim2.addPageRange(new PageRange(12));
        assertEquals(4, victim2.getPages(15).size());
    }
}
