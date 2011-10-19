/*
 * Created on 12/ago/2011
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
package org.sejda.core.manipulation.model.input;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sejda.core.TestUtils;
import org.sejda.core.manipulation.model.pdf.page.PageRange;

/**
 * @author Andrea Vacondio
 * 
 */
public class PdfMergeInputTest {
    private PdfSource source;

    @Before
    public void setUp() {
        source = Mockito.mock(PdfSource.class);
    }

    @Test
    public void testEqual() {
        PageRange range = new PageRange(10);
        PdfMergeInput eq1 = new PdfMergeInput(source);
        eq1.addPageRange(range);
        PdfMergeInput eq2 = new PdfMergeInput(source);
        eq2.addPageRange(range);
        PdfMergeInput eq3 = new PdfMergeInput(source);
        eq3.addPageRange(range);
        PdfMergeInput diff = new PdfMergeInput(source);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void isAllPages() {
        PdfMergeInput victim = new PdfMergeInput(source);
        assertTrue(victim.isAllPages());
        victim.addPageRange(new PageRange(10));
        assertFalse(victim.isAllPages());
    }

    @Test
    public void getPages() {
        PdfMergeInput victim = new PdfMergeInput(source);
        List<PageRange> ranges = new ArrayList<PageRange>();
        ranges.add(new PageRange(5, 8));
        ranges.add(new PageRange(10, 11));
        victim.addAllPageRanges(ranges);
        assertEquals(6, victim.getPages(20).size());
    }
}
