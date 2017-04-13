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

import java.util.*;

public final class StringUtils {
    private StringUtils() {
        // hide
    }

    /**
     * Removes control characters like \n, \t or \r
     * Replaces whitespace, including Unicode 'non-breaking-space', with plain regular 'space'
     */
    public static String normalizeWhitespace(String in) {
        // removes control characters like \n, \r or \t
        // replaces all whitespace (eg: &nbsp;) with ' ' (space)
        String result = in.replaceAll("[\\n\\t\\r]", "").replaceAll("\\p{Z}\\s", " ");
        result = result.replace((char) 160, ' ');
        return result;
    }

    // Useful to debug weird strings
    public static String asUnicodes(String in) {
        if(in == null) return null;

        StringBuilder result = new StringBuilder();
        for (int offset = 0; offset < in.length(); ) {
            int codepoint = in.codePointAt(offset);
            result.append("\\U+").append(Integer.toHexString(codepoint).toUpperCase());
            offset += Character.charCount(codepoint);
        }
        return result.toString();
    }

    /**
     * Returns a list of characters that exist in s1 but not in s2
     */
    public static Set<Character> difference(String s1, String s2) {
        Set<Character> result = new LinkedHashSet<>();
        for(Character c: s1.toCharArray()) {
            if(!s2.contains(c.toString())) {
                result.add(c);
            }
        }

        return result;
    }
}
