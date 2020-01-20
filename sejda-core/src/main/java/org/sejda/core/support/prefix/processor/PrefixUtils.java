/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com)
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
package org.sejda.core.support.prefix.processor;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

public final class PrefixUtils {
    private PrefixUtils() {
        // hide
    }

    /**
     * @return A string where all the variations of whitespace and horizontal whitespace are replace with a standard whitespace, all characters deemed unsafe for a filename are
     *         stripped and the resulting filename is trimmed
     */
    public static String toSafeFilename(String input) {
        // TODO maybe we should do the trimming when we ensure a proper file name length to make sure we do it for every generated file? Or we shouldn't do it at all?
        return defaultIfBlank(input, "").replaceAll("\\s|\\h", " ").replaceAll("[`\0\f\t\n\r\\\\/:*?\\\"<>|]", "")
                .trim();
    }

    /**
     * Strips all but characters that are known to be safe: alphanumerics for now.
     */
    public static String toStrictFilename(String input) {
        String safe = defaultIfBlank(input, "").replaceAll("[^A-Za-z0-9_ .-]", "");
        if (safe.length() > 255) {
            safe = safe.substring(0, 255);
        }
        return safe;
    }
}
