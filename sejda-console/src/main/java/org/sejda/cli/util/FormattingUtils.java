/*
 * Created on Oct 3, 2011
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
package org.sejda.cli.util;

/**
 * String utils
 * 
 * @author Eduard Weissmann
 * 
 */
public final class FormattingUtils {

    private FormattingUtils() {
        // Utils
    }

    public static String justifyLeft(int width, String input) {
        StringBuilder buf = new StringBuilder(input);
        int lastspace = -1;
        int linestart = 0;
        int i = 0;

        while (i < buf.length()) {
            if (buf.charAt(i) == ' ') {
                lastspace = i;
            }
            if (buf.charAt(i) == '\n') {
                lastspace = -1;
                linestart = i + 1;
            }
            if (i > linestart + width - 1) {
                if (lastspace != -1) {
                    buf.setCharAt(lastspace, '\n');
                    linestart = lastspace + 1;
                    lastspace = -1;
                } else {
                    buf.insert(i, '\n');
                    linestart = i + 1;
                }
            }
            i++;
        }
        return buf.toString();
    }

    public static String leftPadMultiline(String input, char padChar, int padWidth) {
        StringBuilder buf = new StringBuilder(input);
        String paddingString = repeatedChar(padChar, padWidth);
        int i = 0;

        while (i < buf.length()) {
            if (buf.charAt(i) == '\n') {
                buf.insert(i + 1, paddingString);
            }
            i++;
        }
        return buf.toString();
    }

    public static String repeatedChar(char chr, int times) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < times; i++) {
            result.append(chr);
        }
        return result.toString();
    }
}
