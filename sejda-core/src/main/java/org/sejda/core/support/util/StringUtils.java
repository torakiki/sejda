/*
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
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.core.support.util;

import java.text.Normalizer;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;

public final class StringUtils {
    private StringUtils() {
        // hide
    }

    public static String isolateRTLIfRequired(String s) {
        if (isRtl(s)) {
            return '\u2068' + s + '\u2069';
        }
        return s;
    }

    public static boolean isRtl(String string) {
        if (string == null) {
            return false;
        }

        java.text.Bidi bidi = new java.text.Bidi(string, java.text.Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT);
        return bidi.isMixed() || bidi.isRightToLeft();
    }
    
    public static boolean equalsNormalized(String s1, String s2) {
        return Normalizer.normalize(s1, Normalizer.Form.NFKC).equals(Normalizer.normalize(s2, Normalizer.Form.NFKC));
    }

    public static String shapeArabicIf(String s) {
        if(isRtl(s)){
            return shapeArabic(s);
        }
        
        return s;
    }
    public static String shapeArabic(String s) {
        try {
            Bidi bidi = new Bidi((new ArabicShaping(ArabicShaping.LETTERS_SHAPE)).shape(s), 127);
            bidi.setReorderingMode(0);
            return bidi.writeReordered(2);
        } catch (ArabicShapingException ex) {
            return s;
        }
    }
}
