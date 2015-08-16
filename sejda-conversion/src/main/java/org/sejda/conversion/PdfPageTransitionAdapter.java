/*
 * Created on Sep 20, 2011
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

import org.sejda.conversion.exception.ConversionException;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.pdf.transition.PdfPageTransition;
import org.sejda.model.pdf.transition.PdfPageTransitionStyle;

/**
 * Adapter for {@link PdfPageTransition} providing String based initialization
 * 
 * @author Eduard Weissmann
 * 
 */
public class PdfPageTransitionAdapter {
    private static final int MIN_TOKENS = 3;
    private PdfPageTransition pdfPageTransition;

    public PdfPageTransitionAdapter(String input) {
        try {
            doParse(input);
        } catch (SejdaRuntimeException e) {
            throw new ConversionException("Unparsable transition: '" + input + "'. " + e.getMessage(), e);
        }
    }

    /**
     * @param input
     */
    private void doParse(String input) {
        String[] tokens = AdapterUtils.splitAndTrim(input);

        if (tokens.length < MIN_TOKENS) {
            throw new ConversionException(
                    "Expected format is: 'transitionType:transitionDurationInSec:pageDisplayDurationInSec'");
        }

        PdfPageTransitionStyle style = EnumUtils.valueOf(PdfPageTransitionStyle.class, tokens[0], "transition type");
        int transitionDuration = AdapterUtils.parseInt(tokens[1], "transition duration in seconds");
        int displayDuration = AdapterUtils.parseInt(tokens[2], "page display duration in seconds");

        pdfPageTransition = PdfPageTransition.newInstance(style, transitionDuration, displayDuration);
    }

    public final PdfPageTransition getPdfPageTransition() {
        return pdfPageTransition;
    }
}
