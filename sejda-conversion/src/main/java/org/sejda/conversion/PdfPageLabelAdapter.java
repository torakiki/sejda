/*
 * Created on Sep 15, 2011
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

import org.apache.commons.lang3.StringUtils;
import org.sejda.conversion.exception.ConversionException;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.pdf.label.PdfLabelNumberingStyle;
import org.sejda.model.pdf.label.PdfPageLabel;

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
            throw new ConversionException("Could not parse input: '" + input + "'. " + e.getMessage(), e);
        }
    }

    private void doParseInput(String input) {
        String[] tokens = AdapterUtils.split(input);

        if (tokens.length < MIN_TOKENS) {
            throw new ConversionException(
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
