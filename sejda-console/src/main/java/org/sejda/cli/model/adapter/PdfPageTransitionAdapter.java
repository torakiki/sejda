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
import org.sejda.core.exception.SejdaRuntimeException;
import org.sejda.core.manipulation.model.pdf.transition.PdfPageTransition;
import org.sejda.core.manipulation.model.pdf.transition.PdfPageTransitionStyle;

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
            throw new ArgumentValidationException("Unparsable transition: '" + input + "'. " + e.getMessage(), e);
        }
    }

    /**
     * @param input
     */
    private void doParse(String input) {
        String[] tokens = AdapterUtils.splitAndTrim(input);

        if (tokens.length < MIN_TOKENS) {
            throw new ArgumentValidationException(
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
