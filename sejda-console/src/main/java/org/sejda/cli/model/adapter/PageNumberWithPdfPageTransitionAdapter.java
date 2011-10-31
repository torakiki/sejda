/*
 * Created on Sep 20, 2011
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

import org.sejda.cli.exception.ArgumentValidationException;
import org.sejda.conversion.AdapterUtils;
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
            throw new ArgumentValidationException("Unparsable page transition definition: '" + input + "'. "
                    + e.getMessage(), e);
        }
    }

    private void doParse(String input) {
        String[] tokens = AdapterUtils.splitAndTrim(input);

        if (tokens.length < MIN_SEPARATOR_OCCURENCES) {
            throw new ArgumentValidationException(
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
