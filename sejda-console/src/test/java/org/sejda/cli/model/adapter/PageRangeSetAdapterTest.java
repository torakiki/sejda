/*
 * Created on 15/ott/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.sejda.conversion.PageRangeSetAdapter;
import org.sejda.model.pdf.page.PageRange;

/**
 * @author Andrea Vacondio
 * 
 */
public class PageRangeSetAdapterTest {

    @Test
    public void insertionOrder() {
        PageRange[] inputRanges = { new PageRange(4), new PageRange(6, 9), new PageRange(1, 2) };
        doTestInsertionOrder(inputRanges);
        ArrayUtils.reverse(inputRanges);
        doTestInsertionOrder(inputRanges);
    }

    private void doTestInsertionOrder(PageRange... pageRanges) {
        String inputString = StringUtils.join(pageRanges, ',');
        List<PageRange> ranges = new ArrayList<PageRange>(new PageRangeSetAdapter(inputString).getPageRangeSet());
        for (int i = 0; i < pageRanges.length; i++) {
            assertEquals(pageRanges[i], ranges.get(i));
        }
    }
}
