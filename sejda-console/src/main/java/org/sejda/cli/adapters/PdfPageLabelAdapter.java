/*
 * Created on Sep 15, 2011
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

import org.apache.commons.lang.StringUtils;
import org.sejda.core.exception.SejdaRuntimeException;
import org.sejda.core.manipulation.model.pdf.label.PdfLabelNumberingStyle;
import org.sejda.core.manipulation.model.pdf.label.PdfPageLabel;

/**
 * Adapter for a {@link PdfPageLabel} having a page number. Provides initialization from a String input, to be used in the cli interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class PdfPageLabelAdapter {

    private static final int LABEL_PREFIX_INDEX = 3;
    private static final int MIN_TOKENS = 3;

    private Integer pageNumber;
    private PdfPageLabel pdfPageLabel;

    public PdfPageLabelAdapter(String input) {

        try {
            doParseInput(input);
        } catch (SejdaRuntimeException e) {
            throw new SejdaRuntimeException("Could not parse input: '" + input + "'. " + e.getMessage(), e);
        }
    }

    private void doParseInput(String input) {
        String[] tokens = AdapterUtils.splitAndTrim(input);

        if (tokens.length < MIN_TOKENS) {
            throw new SejdaRuntimeException(
                    "Format expected is: pageNumber(mandatory):numberingStyle(mandatory):logicalPageNumber(mandatory):labelPrefix(optional)'");
        }

        pageNumber = AdapterUtils.parseInt(tokens[0], "page number");
        PdfLabelNumberingStyle numberingStyle = EnumUtils.valueOf(PdfLabelNumberingStyle.class, tokens[1],
                "numbering style");
        int labelSuffixStartFromNumber = AdapterUtils.parseInt(tokens[2], "label suffix start number");
        String labelPrefix = tokens.length < LABEL_PREFIX_INDEX + 1 ? "" : tokens[LABEL_PREFIX_INDEX];

        if (StringUtils.isBlank(labelPrefix)) {
            pdfPageLabel = PdfPageLabel.newInstanceWithoutLabel(numberingStyle, labelSuffixStartFromNumber);
        } else {
            pdfPageLabel = PdfPageLabel.newInstanceWithLabel(labelPrefix, numberingStyle, labelSuffixStartFromNumber);
        }
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public PdfPageLabel getPdfPageLabel() {
        return pdfPageLabel;
    }
}
