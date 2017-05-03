/*
 * Copyright 2017 by Eduard Weissmann (edi.weissmann@gmail.com).
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

import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.pdf.page.PredefinedSetOfPages;

import java.util.Set;

/**
 * Accepts predefined sets of pages, such as 'all', 'odd' or 'even' together with a set of page ranges '1,2-10,15-' in one go.
 */
public class PageRangeSetOrPredefinedSetOfPagesAdapter {

    private PredefinedSetOfPages predefinedSetOfPages;
    private Set<PageRange> pageRanges;

    public PageRangeSetOrPredefinedSetOfPagesAdapter(String in) {
        try {
            predefinedSetOfPages = new PredefinedSetOfPagesAdapter(in).getEnumValue();
        } catch(SejdaRuntimeException ex) {
            pageRanges = new PageRangeSetAdapter(in).getPageRangeSet();
        }
    }

    public PredefinedSetOfPages getPredefinedSetOfPages() {
        return predefinedSetOfPages;
    }

    public Set<PageRange> getPageRanges() {
        return pageRanges;
    }
}
