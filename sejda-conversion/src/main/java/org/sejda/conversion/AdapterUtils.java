/*
 * Created on Sep 18, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.conversion;

import org.apache.commons.lang3.StringUtils;
import org.sejda.model.exception.SejdaRuntimeException;

/**
 * Cli adapters parsing utils
 * 
 * @author Eduard Weissmann
 * 
 */
public final class AdapterUtils {

    private static final String ALL = "all";

    private AdapterUtils() {
        // utils are not instantiated, use statically
    }

    /**
     * Parse input string into an integer, throwing an exception in case the input cannot be parsed
     * 
     * @param input
     * @param explainedInput
     *            the user-friendly name of what the input represents, to be used in exception message. Ex: "page number" or "phone number"
     * @see #parseIntSilently(String) for an alternative that silently returns {@code null} in case the input cannot be parsed
     * @return parsed int value
     * @throws SejdaRuntimeException
     *             if the input cannot be parsed
     */
    public static int parseInt(String input, String explainedInput) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new SejdaRuntimeException("Unrecognized " + explainedInput + ": '" + input + "'", e);
        }
    }

    private static final String DEFAULT_SEPARATOR = ":";

    /**
     * Splits an input string using the default ":" separator
     * 
     * @param input
     * @return an array of String tokens that result from the split
     */
    public static String[] split(String input) {
        return StringUtils.split(input, DEFAULT_SEPARATOR);
    }

    /**
     * Same as {@link #splitAndTrim(String, String)} only it uses default ":" separator
     * 
     * @param input
     * @return an array of String tokens that result from the split
     */
    public static String[] splitAndTrim(String input) {
        return StringUtils.split(StringUtils.trim(input), DEFAULT_SEPARATOR);
    }

    /**
     * Splits an input string using the specified separator. The input string is trimmed before being split, and the same trim operation is applied to all the split tokens
     * 
     * @param input
     * @param separator
     * @return an array of String tokens that result from the split
     */
    public static String[] splitAndTrim(String input, String separator) {
        return StringUtils.split(StringUtils.trim(input), separator);
    }

    /**
     * Parse input string into an integer, silently returning a {@code null} in case the input cannot be parsed
     * 
     * @param input
     * @return an Integer matching the string input, or null if the input cannot be parsed as an Integer
     * @see #parseInt(String, String) for an alternative that throws an exception when input cannot be parsed
     */
    public static Integer parseIntSilently(String input) {
        try {
            return Integer.valueOf(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 
     * @param rawString
     * @return true if the input raw string represent the sejda-console convention string for 'all', where 'all' is usually used when defining page selection to select all pages.
     */
    public static boolean isAllPages(String rawString) {
        return StringUtils.equalsIgnoreCase(ALL, rawString);
    }
}
