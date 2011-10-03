/*
 * Created on Oct 3, 2011
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
package org.sejda.utils;

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
        StringBuffer buf = new StringBuffer(input);
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
        StringBuffer buf = new StringBuffer(input);
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
