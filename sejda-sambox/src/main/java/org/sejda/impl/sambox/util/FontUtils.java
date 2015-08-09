/*
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * This file is part of the Sejda source code
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.sejda.model.pdf.StandardType1Font;
import org.sejda.sambox.pdmodel.font.PDType1Font;

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
