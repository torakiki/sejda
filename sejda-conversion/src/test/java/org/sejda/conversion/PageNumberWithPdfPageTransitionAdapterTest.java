/*
 * Created on 27/gen/2014
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
package org.sejda.conversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.sejda.conversion.exception.ConversionException;
import org.sejda.model.pdf.transition.PdfPageTransitionStyle;

/**
 * @author Andrea Vacondio
 * 
 */
public class PageNumberWithPdfPageTransitionAdapterTest {

    @Test(expected = ConversionException.class)
    public void testNegative() {
        new PageNumberWithPdfPageTransitionAdapter("box_inward:2:3");
    }

    @Test
    public void testPositive() {
        PageNumberWithPdfPageTransitionAdapter victim = new PageNumberWithPdfPageTransitionAdapter("box_inward:2:3:5");
        assertEquals(5, victim.getPageNumber().intValue());
        assertNotNull(victim.getPdfPageTransition());
        assertEquals(PdfPageTransitionStyle.BOX_INWARD, victim.getPdfPageTransition().getStyle());
        assertEquals(2, victim.getPdfPageTransition().getTransitionDuration());
        assertEquals(3, victim.getPdfPageTransition().getDisplayDuration());
    }
}
