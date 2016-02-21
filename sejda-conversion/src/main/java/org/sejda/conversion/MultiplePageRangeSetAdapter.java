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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.sejda.model.pdf.page.PageRange;

/**
 * Adapter for a list of sets of {@link PageRange}s, providing initialization from {@link String}
 * 
 * @author Eduard Weissmann
 * 
 */
public class MultiplePageRangeSetAdapter {
    private final List<Set<PageRange>> listOfPageRangeSets = new ArrayList<>();

    public MultiplePageRangeSetAdapter(String rawString) {
        String[] tokens = AdapterUtils.splitAndTrim(rawString);
        for (String eachToken : tokens) {
            listOfPageRangeSets.add(new PageRangeSetAdapter(eachToken).getPageRangeSet());
        }
    }

    public List<Set<PageRange>> ranges() {
        return listOfPageRangeSets;
    }
}
