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

import static java.lang.Character.DIRECTIONALITY_LEFT_TO_RIGHT;
import static java.lang.Character.DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING;
import static java.lang.Character.DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE;
import static java.lang.Character.DIRECTIONALITY_RIGHT_TO_LEFT;
import static java.lang.Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
import static java.lang.Character.DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING;
import static java.lang.Character.DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE;

public final class StringUtils {
    private StringUtils() {
        // hide
    }

    // Useful to debug weird strings
    public static String asUnicodes(String in) {
        if (in == null)
            return null;

        StringBuilder result = new StringBuilder();
        for (int offset = 0; offset < in.length();) {
            int codepoint = in.codePointAt(offset);
            result.append("\\U+").append(Integer.toHexString(codepoint).toUpperCase());
            offset += Character.charCount(codepoint);
        }
        return result.toString();
    }

    public static String normalizeLineEndings(String in) {
        return in.replaceAll("\\r\\n", "\n");
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

        for (int i = 0, n = string.length(); i < n; ++i) {
            byte d = Character.getDirectionality(string.charAt(i));

            switch (d) {
            case DIRECTIONALITY_RIGHT_TO_LEFT:
            case DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC:
            case DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING:
            case DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE:
                return true;

            case DIRECTIONALITY_LEFT_TO_RIGHT:
            case DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING:
            case DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE:
                return false;
            }
        }

        return false;
    }
}
