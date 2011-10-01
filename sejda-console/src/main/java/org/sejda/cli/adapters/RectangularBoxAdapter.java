/*
 * Created on Sep 30, 2011
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
package org.sejda.cli.adapters;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.sejda.core.exception.SejdaRuntimeException;
import org.sejda.core.manipulation.model.RectangularBox;

/**
 * Adapter for {@link RectangularBox}, providing initialization from string
 * 
 * @author Eduard Weissmann
 * 
 */
public class RectangularBoxAdapter {

    private static final int INDEX_OF_BOTTOM_TOKEN = 0;
    private static final int INDEX_OF_LEFT_TOKEN = 1;
    private static final int INDEX_OF_TOP_TOKEN = 2;
    private static final int INDEX_OF_RIGHT_TOKEN = 3;
    private static final int MIN_TOKENS = 4;
    private RectangularBox rectangularBox;

    public RectangularBoxAdapter(String input) {
        try {
            doParse(input);
        } catch (SejdaRuntimeException e) {
            throw new SejdaRuntimeException("Unparsable rectangular box: '" + input + "'. " + e.getMessage(), e);
        }
    }

    /**
     * @param input
     */
    private void doParse(String input) {
        String[] tokens = doubleSplit(input.replaceFirst("^\\(", "").replaceFirst("\\)$", ""), "\\)\\(", ":");

        if (tokens.length < MIN_TOKENS) {
            throw new SejdaRuntimeException("Expected format is: '(bottom:left)(top:right)'");
        }

        int bottom = AdapterUtils.parseInt(tokens[INDEX_OF_BOTTOM_TOKEN], "bottom");
        int left = AdapterUtils.parseInt(tokens[INDEX_OF_LEFT_TOKEN], "left");
        int top = AdapterUtils.parseInt(tokens[INDEX_OF_TOP_TOKEN], "top");
        int right = AdapterUtils.parseInt(tokens[INDEX_OF_RIGHT_TOKEN], "right");

        rectangularBox = RectangularBox.newInstance(bottom, left, top, right);
    }

    private String[] doubleSplit(String input, String firstDelimter, String secondDelimiter) {
        return splitStrings(splitStrings(new String[] { input }, firstDelimter), secondDelimiter);
    }

    private String[] splitStrings(String[] inputs, String delimiter) {
        List<String> result = new LinkedList<String>();
        for (String eachInput : inputs) {
            String[] newTokens = eachInput.split(delimiter);
            result.addAll(Arrays.asList(newTokens));
        }
        return result.toArray(new String[] {});
    }

    public final RectangularBox getRectangularBox() {
        return rectangularBox;
    }
}
