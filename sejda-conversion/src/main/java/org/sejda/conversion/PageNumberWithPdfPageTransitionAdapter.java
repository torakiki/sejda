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

/**
 * Adapter for a {@link PdfPageTransition} with a page number reference, providing String based initialization
 * 
 * @author Eduard Weissmann
 * 
 */
public class PageNumberWithPdfPageTransitionAdapter {
    private static final int PAGE_NUMBER_INDEX = 3;
    private static final int MIN_SEPARATOR_OCCURENCES = 4;
    private Integer pageNumber;
    private PdfPageTransition pdfPageTransition;

    public PageNumberWithPdfPageTransitionAdapter(String input) {
        try {
            doParse(input);
        } catch (SejdaRuntimeException e) {
            throw new ConversionException("Unparsable page transition definition: '" + input + "'. " + e.getMessage(),
                    e);
        }
    }

    private void doParse(String input) {
        String[] tokens = AdapterUtils.splitAndTrim(input);

        if (tokens.length < MIN_SEPARATOR_OCCURENCES) {
            throw new ConversionException(
                    "Expected format is: 'transitionType:transitionDurationInSec:pageDisplayDurationInSec:pageNumber'");
        }

        pdfPageTransition = new PdfPageTransitionAdapter(tokens[0] + ":" + tokens[1] + ":" + tokens[2])
                .getPdfPageTransition();

        pageNumber = AdapterUtils.parseInt(tokens[PAGE_NUMBER_INDEX], "page number");
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public final PdfPageTransition getPdfPageTransition() {
        return pdfPageTransition;
    }
}
