/*
 * Created on 30/dic/2012
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import org.sejda.conversion.exception.ConversionException;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.pdf.headerfooter.Numbering;
import org.sejda.model.pdf.headerfooter.NumberingStyle;

/**
 * Adapter for a {@link Numbering}, providing initialization from {@link String}
 * 
 * @author Andrea Vacondio
 * 
 */
public class NumberingAdapter {

    private static final int TOKENS_NUMBER = 2;
    private Numbering numbering = Numbering.NULL;

    public NumberingAdapter(String rawString) {
        try {
            doParseInput(rawString);
        } catch (SejdaRuntimeException e) {
            throw new ConversionException("Could not parse input: '" + rawString + "'. " + e.getMessage(), e);
        }
    }

    private void doParseInput(String input) {
        String[] tokens = AdapterUtils.split(input);

        if (tokens.length != TOKENS_NUMBER) {
            throw new ConversionException("Format expected is: pageNumber(mandatory):numberingStyle(mandatory)'");
        }

        int pageNumber = AdapterUtils.parseInt(tokens[0], "page number");
        NumberingStyle numberingStyle = EnumUtils.valueOf(NumberingStyle.class, tokens[1], "numbering style");
        numbering = new Numbering(numberingStyle, pageNumber);
    }

    public Numbering getNumbering() {
        return numbering;
    }

}
