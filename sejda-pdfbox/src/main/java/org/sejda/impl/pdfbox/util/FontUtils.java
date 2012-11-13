/*
 * Created on 13/nov/2012
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
package org.sejda.impl.pdfbox.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.sejda.model.pdf.StandardType1Font;

/**
 * Utility to map from Sejda font definition to PDFBox.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class FontUtils {

    private FontUtils() {
        // hide
    }

    private static final Map<StandardType1Font, PDType1Font> STANDARD_TYPE1_FONTS;
    static {
        Map<StandardType1Font, PDType1Font> fontsCache = new HashMap<StandardType1Font, PDType1Font>();
        fontsCache.put(StandardType1Font.CURIER, PDType1Font.COURIER);
        fontsCache.put(StandardType1Font.CURIER_BOLD, PDType1Font.COURIER_BOLD);
        fontsCache.put(StandardType1Font.CURIER_BOLD_OBLIQUE, PDType1Font.COURIER_BOLD_OBLIQUE);
        fontsCache.put(StandardType1Font.CURIER_OBLIQUE, PDType1Font.COURIER_OBLIQUE);
        fontsCache.put(StandardType1Font.HELVETICA, PDType1Font.HELVETICA);
        fontsCache.put(StandardType1Font.HELVETICA_BOLD, PDType1Font.HELVETICA_BOLD);
        fontsCache.put(StandardType1Font.HELVETICA_BOLD_OBLIQUE, PDType1Font.HELVETICA_BOLD_OBLIQUE);
        fontsCache.put(StandardType1Font.HELVETICA_OBLIQUE, PDType1Font.HELVETICA_OBLIQUE);
        fontsCache.put(StandardType1Font.SYMBOL, PDType1Font.SYMBOL);
        fontsCache.put(StandardType1Font.ZAPFDINGBATS, PDType1Font.ZAPF_DINGBATS);
        fontsCache.put(StandardType1Font.TIMES_BOLD, PDType1Font.TIMES_BOLD);
        fontsCache.put(StandardType1Font.TIMES_BOLD_ITALIC, PDType1Font.TIMES_BOLD_ITALIC);
        fontsCache.put(StandardType1Font.TIMES_ITALIC, PDType1Font.TIMES_ITALIC);
        fontsCache.put(StandardType1Font.TIMES_ROMAN, PDType1Font.TIMES_ROMAN);
        STANDARD_TYPE1_FONTS = Collections.unmodifiableMap(fontsCache);
    }

    /**
     * Mapping between Sejda and PDFBox standard type 1 fonts implementation
     * 
     * @param st1Font
     * @return the PDFBox font.
     */
    public static PDType1Font getStandardType1Font(StandardType1Font st1Font) {
        return STANDARD_TYPE1_FONTS.get(st1Font);
    }
}
