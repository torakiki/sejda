/*
 * Created on 30/dic/2012
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
