/*
 * Created on Sep 12, 2011
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

import java.util.Set;

import org.sejda.common.collection.NullSafeSet;
import org.sejda.conversion.BasePageRangeAdapter.PageRangeAdapter;
import org.sejda.model.pdf.page.PageRange;

/**
 * Adapter for a set of {@link PageRange}s, providing initialization from {@link String}
 * 
 * @author Eduard Weissmann
 * 
 */
public class PageRangeSetAdapter {

    private static final String SEPARATOR = ",";

    private final Set<PageRange> pageRangeSet = new NullSafeSet<>();

    public PageRangeSetAdapter(String rawString) {
        if (AdapterUtils.isAllPages(rawString)) {
            pageRangeSet.add(new PageRange(1));
            return;
        }

        String[] tokens = AdapterUtils.splitAndTrim(rawString, SEPARATOR);
        for (String eachToken : tokens) {
            pageRangeSet.add(new PageRangeAdapter(eachToken).getPageRange());
        }
    }

    public Set<PageRange> getPageRangeSet() {
        return pageRangeSet;
    }
}