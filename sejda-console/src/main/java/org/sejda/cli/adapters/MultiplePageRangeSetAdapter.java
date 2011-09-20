/*
 * Created on Sep 4, 2011
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sejda.core.manipulation.model.pdf.page.PageRange;

/**
 * Adapter for a list of sets of {@link PageRange}s, providing initialization from {@link String}
 * 
 * @author Eduard Weissmann
 * 
 */
public class MultiplePageRangeSetAdapter {
    /**
     * Tokens separator in the user input
     */
    private static final String SEPARATOR = ":";

    private final List<Set<PageRange>> listOfPageRangeSets = new ArrayList<Set<PageRange>>();

    public MultiplePageRangeSetAdapter(String rawString) {
        String[] tokens = AdapterUtils.splitAndTrim(rawString, SEPARATOR);
        for (String eachToken : tokens) {
            listOfPageRangeSets.add(new PageRangeSetAdapter(eachToken).getPageRangeSet());
        }
    }

    public Iterator<Set<PageRange>> iterator() {
        return listOfPageRangeSets.iterator();
    }
}
