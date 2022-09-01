/*
 * Created on 12/ago/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.input;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.TestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Andrea Vacondio
 * 
 */
public class PdfMergeInputTest {
    private PdfSource<?> source;

    @BeforeEach
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
        List<PageRange> ranges = new ArrayList<>();
        ranges.add(new PageRange(5, 8));
        ranges.add(new PageRange(10, 11));
        victim.addAllPageRanges(ranges);
        assertEquals(6, victim.getPages(20).size());
    }
}
