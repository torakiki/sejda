/*
 * Created on 03 feb 2017
 * Copyright 2017 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
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
public class PdfMixInputTest {
    private PdfSource<?> source;

    @BeforeEach
    public void setUp() {
        source = Mockito.mock(PdfSource.class);
    }

    @Test
    public void testEqual() {
        PdfMixInput eq1 = new PdfMixInput(source);
        PdfMixInput eq2 = new PdfMixInput(source);
        PdfMixInput eq3 = new PdfMixInput(source);
        PdfMixInput diff = new PdfMixInput(source, true, 5);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void isAllPages() {
        PdfMixInput victim = new PdfMixInput(source);
        assertTrue(victim.isAllPages());
        victim.addPageRange(new PageRange(10));
        assertFalse(victim.isAllPages());
    }

    @Test
    public void getPages() {
        PdfMixInput victim = new PdfMixInput(source);
        List<PageRange> ranges = new ArrayList<>();
        ranges.add(new PageRange(5, 8));
        ranges.add(new PageRange(10, 11));
        victim.addAllPageRanges(ranges);
        assertEquals(6, victim.getPages(20).size());
    }
}
