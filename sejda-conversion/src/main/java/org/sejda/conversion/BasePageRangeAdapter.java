/*
 * Created on Sep 4, 2011
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
import org.sejda.model.pdf.page.PageRange;

/**
 * Adapter for {@link PageRange}, providing initialization from {@link String}
 * 
 * @author Eduard Weissmann
 */
public class BasePageRangeAdapter {

    private static final String SEPARATOR = "-";
    private boolean acceptAllString = false;

    private PageRange pageRange;

    public BasePageRangeAdapter(String rawString, boolean acceptAllString) {
        this.acceptAllString = acceptAllString;
        try {
            pageRange = doParsePageRange(rawString);
        } catch (SejdaRuntimeException e) {
            throw new ConversionException("Unparsable page range '" + rawString + "'. " + e.getMessage(), e);
        }

        if (pageRange.getStart() > pageRange.getEnd()) {
            throw new ConversionException("Invalid page range '" + rawString + "', ends before starting");
        }
    }

    public PageRange getPageRange() {
        return pageRange;
    }

    /**
     * @param rawString
     *            string representation of the {@link PageRange}
     */
    private PageRange doParsePageRange(String rawString) {
        if (acceptAllString && AdapterUtils.isAllPages(rawString)) {
            return new PageRange(1);
        }
        String[] tokens = AdapterUtils.splitAndTrim(rawString, SEPARATOR);
        if (tokens.length == 1) {
            if (rawString.contains(SEPARATOR)) {
                // 23-<end>
                return new PageRange(parsePageNumber(tokens[0]));
            }
            // 23
            return new PageRange(parsePageNumber(tokens[0]), parsePageNumber(tokens[0]));
        } else if (tokens.length == 2) {
            int token0 = parsePageNumber(tokens[0]);
            int token1 = parsePageNumber(tokens[1]);
            return new PageRange(token0, token1);
        } else {
            throw new ConversionException(
                    "Ambiguous definition. Use following formats: [<page>] or [<page1>-<page2>] or [<page1>-]");
        }
    }

    private int parsePageNumber(String s) {
        return AdapterUtils.parseInt(StringUtils.trim(s), "page number");
    }

    /**
     * A {@link PageRange} adapter not allowing the 'all' string.
     * 
     * @author Andrea Vacondio
     * 
     */
    public static final class PageRangeAdapter extends BasePageRangeAdapter {

        public PageRangeAdapter(String rawString) {
            super(rawString, false);
        }

    }

    /**
     * A {@link PageRange} adapter allowing the 'all' string.
     * 
     * @author Andrea Vacondio
     * 
     */
    public static final class PageRangeWithAllAdapter extends BasePageRangeAdapter {

        public PageRangeWithAllAdapter(String rawString) {
            super(rawString, true);
        }

    }
}
