/*
 * Created on 22 ott 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.conversion;

import static org.sejda.conversion.AdapterUtils.parseInt;

import java.awt.Point;

import org.apache.commons.lang3.StringUtils;
import org.sejda.conversion.exception.ConversionException;
import org.sejda.model.exception.SejdaRuntimeException;

/**
 * Adapter for a string describing a {@link Point} in the form x,y
 * 
 * @author Andrea Vacondio
 *
 */
public class PointAdatper {
    private Point point = null;

    public PointAdatper(String rawString) {
        try {
            doParseInput(rawString);
        } catch (SejdaRuntimeException e) {
            throw new ConversionException("Could not parse input: '" + rawString + "'. " + e.getMessage(), e);
        }
    }

    private void doParseInput(String input) {
        String[] tokens = StringUtils.split(input, ",");
        if (tokens.length != 2) {
            throw new ConversionException("Expected a string in the form \"x,y\" but found '" + input + "'");
        }

        point = new Point(parseInt(tokens[0], "x coordinate"), parseInt(tokens[1], "y coordinate"));
    }

    public Point getPoint() {
        return point;
    }
}
