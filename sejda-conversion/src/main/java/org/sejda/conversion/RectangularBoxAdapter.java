/*
 * Created on Sep 30, 2011
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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.sejda.conversion.exception.ConversionException;
import org.sejda.model.RectangularBox;
import org.sejda.model.exception.SejdaRuntimeException;

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
            throw new ConversionException("Unparsable rectangular box: '" + input + "'. " + e.getMessage(), e);
        }
    }

    private void doParse(String input) {
        String[] tokens = doubleSplit(input.replaceFirst("^\\[", "").replaceFirst("\\]$", ""), "\\]\\[", ":");

        if (tokens.length < MIN_TOKENS) {
            throw new ConversionException("Expected format is: '[bottom:left][top:right]'");
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
        List<String> result = new LinkedList<>();
        for (String eachInput : inputs) {
            String[] newTokens = eachInput.split(delimiter);
            result.addAll(Arrays.asList(newTokens));
        }
        return result.toArray(new String[result.size()]);
    }

    public final RectangularBox getRectangularBox() {
        return rectangularBox;
    }
}
