/*
 * Created on 26/ago/2011
 * Copyright 2011 Sober Lemur S.r.l. and Sejda BV.
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
package org.sejda.model.parameter;

import org.junit.jupiter.api.Test;
import org.sejda.model.TestUtils;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.pdf.page.PredefinedSetOfPages;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.sejda.model.pdf.page.PageRange.one;
/**
 * @author Andrea Vacondio
 * 
 */
public class ExtractPagesParametersTest {
    @Test
    public void testEquals() {
        ExtractPagesParameters eq1 = new ExtractPagesParameters(PredefinedSetOfPages.EVEN_PAGES);
        ExtractPagesParameters eq2 = new ExtractPagesParameters(PredefinedSetOfPages.EVEN_PAGES);
        ExtractPagesParameters eq3 = new ExtractPagesParameters(PredefinedSetOfPages.EVEN_PAGES);
        ExtractPagesParameters diff = new ExtractPagesParameters();
        diff.addPageRange(new PageRange(12));
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void testGetPageSelection() {
        ExtractPagesParameters victim = new ExtractPagesParameters(PredefinedSetOfPages.EVEN_PAGES);
        assertFalse(victim.hasPageSelection());
        ExtractPagesParameters victim2 = new ExtractPagesParameters();
        victim2.addPageRange(new PageRange(12));
        assertTrue(victim2.hasPageSelection());
    }

    @Test
    public void getPages() {
        ExtractPagesParameters victim = new ExtractPagesParameters(PredefinedSetOfPages.EVEN_PAGES);
        assertEquals(5, victim.getPages(10).size());
        ExtractPagesParameters victim2 = new ExtractPagesParameters();
        victim2.addPageRange(new PageRange(12));
        assertEquals(4, victim2.getPages(15).size());
    }

    @Test
    public void testInvalidParameters() {
        ExtractPagesParameters victim = new ExtractPagesParameters(PredefinedSetOfPages.ODD_PAGES);
        InputStream stream = mock(InputStream.class);
        PdfSource<InputStream> input = PdfStreamSource.newInstanceNoPassword(stream, "name");
        victim.addSource(input);
        TestUtils.assertInvalidParameters(victim);
    }

    @Test
    public void getPagesRangesNoSeparateFileForEachRange() {
        assertEquals(1, new ExtractPagesParameters(PredefinedSetOfPages.EVEN_PAGES).getPagesSets(10).size());
        ExtractPagesParameters victim = new ExtractPagesParameters();
        victim.addPageRange(new PageRange(2, 5));
        victim.addPageRange(one(8));
        assertEquals(1, victim.getPagesSets(10).size());
        victim.setInvertSelection(true);
        assertEquals(1, victim.getPagesSets(10).size());
    }

    @Test
    public void getPagesRangesSeparateFileForEachRange() {
        ExtractPagesParameters victim = new ExtractPagesParameters(PredefinedSetOfPages.EVEN_PAGES);
        victim.setSeparateFileForEachRange(true);
        assertEquals(1, victim.getPagesSets(10).size());
        victim = new ExtractPagesParameters();
        victim.setSeparateFileForEachRange(true);
        victim.addPageRange(new PageRange(2, 5));
        victim.addPageRange(one(8));
        assertEquals(2, victim.getPagesSets(10).size());
        victim.setInvertSelection(true);
        assertEquals(3, victim.getPagesSets(10).size());
        victim.addPageRange(one(10));
        assertEquals(3, victim.getPagesSets(10).size());
        victim = new ExtractPagesParameters();
        victim.setSeparateFileForEachRange(true);
        victim.setInvertSelection(true);
        victim.addPageRange(new PageRange(1, 5));
        assertEquals(0, victim.getPagesSets(5).size());
    }

    @Test
    public void nullSafeConstructor() {
        var victim = new ExtractPagesParameters(null);
        assertEquals(PredefinedSetOfPages.NONE, victim.getPredefinedSetOfPages());
    }
}
