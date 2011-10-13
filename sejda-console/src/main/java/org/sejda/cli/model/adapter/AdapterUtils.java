/*
 * Created on Sep 18, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.cli.model.adapter;

import org.apache.commons.lang3.StringUtils;
import org.sejda.core.exception.SejdaRuntimeException;

/**
 * Cli adapters parsing utils
 * 
 * @author Eduard Weissmann
 * 
 */
public final class AdapterUtils {

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
}
