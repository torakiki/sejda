/*
 * Created on 08/mar/2013
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.model.parameter.image;

import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.model.pdf.page.PageRange;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Andrea Vacondio
 * 
 */
public class PdfToJpegParametersTest {
    @Test
    public void testEquals() {
        PdfToJpegParameters eq1 = new PdfToJpegParameters();
        PdfToJpegParameters eq2 = new PdfToJpegParameters();
        PdfToJpegParameters eq3 = new PdfToJpegParameters();
        PdfToJpegParameters diff = new PdfToJpegParameters();
        diff.setResolutionInDpi(120);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void testGetPageSelection() {
        PdfToJpegParameters victim = new PdfToJpegParameters();
        assertTrue(victim.getPageSelection().isEmpty());
        PdfToJpegParameters victim2 = new PdfToJpegParameters();
        victim2.addPageRange(new PageRange(12));
        assertFalse(victim2.getPageSelection().isEmpty());
    }

    @Test
    public void getPages() {
        PdfToJpegParameters victim = new PdfToJpegParameters();
        assertEquals(10, victim.getPages(10).size());
        PdfToJpegParameters victim2 = new PdfToJpegParameters();
        victim2.addPageRange(new PageRange(12));
        assertEquals(4, victim2.getPages(15).size());
    }
}
