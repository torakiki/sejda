/*
 * Created on 28/nov/2010
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.core.util;

import org.junit.Test;
import org.sejda.core.manipulation.model.pdf.PdfEncryption;
import org.sejda.core.manipulation.model.pdf.PdfVersion;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfBooleanPreference;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfPageMode;
import org.sejda.core.support.util.PdfVersionUtility;

import static org.junit.Assert.assertEquals;

/**
 * @author Andrea Vacondio
 *
 */
public class PdfVersionUtilityTest {

    @Test
    public void testGetMaxPdfVersion() {
        assertEquals(PdfVersion.VERSION_1_7, PdfVersionUtility.getMax(PdfVersion.values()));
    }

    @Test
    public void testGetMaxMinRequiredVersion() {
        assertEquals(PdfVersion.VERSION_1_6, PdfVersionUtility.getMax(PdfBooleanPreference.DISPLAY_DOC_TITLE,
                PdfPageMode.FULLSCREEN, PdfEncryption.AES_ENC_128));
    }
}
