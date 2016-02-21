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

public final class StringUtils {
    private StringUtils() {
        // hide
    }

    /**
     * Why this method? Because when extracting text from PDFs you may get back non breaking space, which is technically different than whitespace but for the purpose of text
     * comparison and extraction it actually should behave the same.
     *
     * @return the input string with all non breaking space chars replaced by whitespace char ' '
     */
    public static String nbspAsWhitespace(String in) {
        return in.replace((char) 160, ' ');
    }

    // Useful to debug weird strings that contain non breaking spaces
    public static String asAsciiCodes(String in) {
        StringBuilder result = new StringBuilder();
        for (char c : in.toCharArray()) {
            result.append((int) c).append(" ");
        }
        return result.toString();
    }

}
