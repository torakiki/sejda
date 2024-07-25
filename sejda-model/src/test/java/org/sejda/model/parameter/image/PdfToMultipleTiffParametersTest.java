/*
 * Created on 18/set/2011
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
package org.sejda.model.parameter.image;

import org.junit.jupiter.api.Test;
import org.sejda.model.image.ImageColorType;
import org.sejda.model.image.TiffCompressionType;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.TestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Andrea Vacondio
 * 
 */
public class PdfToMultipleTiffParametersTest {

    @Test
    public void testEquals() {
        PdfToMultipleTiffParameters eq1 = new PdfToMultipleTiffParameters(ImageColorType.GRAY_SCALE);
        PdfToMultipleTiffParameters eq2 = new PdfToMultipleTiffParameters(ImageColorType.GRAY_SCALE);
        PdfToMultipleTiffParameters eq3 = new PdfToMultipleTiffParameters(ImageColorType.GRAY_SCALE);
        PdfToMultipleTiffParameters diff = new PdfToMultipleTiffParameters(ImageColorType.BLACK_AND_WHITE);
        diff.setCompressionType(TiffCompressionType.JPEG_TTN2);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void testGetPageSelection() {
        PdfToMultipleTiffParameters victim = new PdfToMultipleTiffParameters(ImageColorType.GRAY_SCALE);
        assertTrue(victim.getPageSelection().isEmpty());
        PdfToMultipleTiffParameters victim2 = new PdfToMultipleTiffParameters(ImageColorType.GRAY_SCALE);
        victim2.addPageRange(new PageRange(12));
        assertFalse(victim2.getPageSelection().isEmpty());
    }

    @Test
    public void getPages() {
        PdfToMultipleTiffParameters victim = new PdfToMultipleTiffParameters(ImageColorType.GRAY_SCALE);
        assertEquals(10, victim.getPages(10).size());
        PdfToMultipleTiffParameters victim2 = new PdfToMultipleTiffParameters(ImageColorType.GRAY_SCALE);
        victim2.addPageRange(new PageRange(12));
        assertEquals(4, victim2.getPages(15).size());
    }
}
