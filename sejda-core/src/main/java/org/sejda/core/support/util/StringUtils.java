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

import java.util.Arrays;
import java.util.List;

public class StringUtils {
    /**
     * Why this method?
     * Most String.trim() utilities out there will not trim non breaking space chars (ascii char '160') which is found in PDFs.
     *
     * @param in
     * @return
     */
    private static List<Character> extraWhitespaceChars = Arrays.asList((char) 160);

    public static String trimIncludingNbsp(String in) {
        String result = in.trim();

        for (Character c : extraWhitespaceChars) {
            if (result.length() >= 1 && result.charAt(0) == c) {
                result = result.substring(1);
            }

            if (result.length() >= 1 && result.charAt(result.length() - 1) == c) {
                result = result.substring(0, result.length() - 1);
            }
        }

        return result;
    }

}
