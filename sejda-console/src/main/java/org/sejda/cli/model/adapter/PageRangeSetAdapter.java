/*
 * Created on Sep 12, 2011
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

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.sejda.core.manipulation.model.pdf.page.PageRange;

/**
 * Adapter for a set of {@link PageRange}s, providing intialization from {@link String}
 * 
 * @author Eduard Weissmann
 * 
 */
public class PageRangeSetAdapter {

    private static final String SEPARATOR = ",";
    private static final String ALL = "all";

    private final Set<PageRange> pageRangeSet = new HashSet<PageRange>();

    public PageRangeSetAdapter(String rawString) {
        if (isAllPages(rawString)) {
            return;
        }

        String[] tokens = AdapterUtils.splitAndTrim(rawString, SEPARATOR);
        for (String eachToken : tokens) {
            pageRangeSet.add(new PageRangeAdapter(eachToken).getPageRange());
        }
    }

    private boolean isAllPages(String rawString) {
        return StringUtils.equalsIgnoreCase(ALL, rawString);
    }

    public Set<PageRange> getPageRangeSet() {
        return pageRangeSet;
    }
}