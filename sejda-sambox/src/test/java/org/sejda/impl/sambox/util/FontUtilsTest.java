/*
 * Created on 13/nov/2012
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
package org.sejda.impl.sambox.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.sejda.impl.sambox.util.FontUtils.canDisplay;
import static org.sejda.impl.sambox.util.FontUtils.findFontFor;
import static org.sejda.impl.sambox.util.FontUtils.fontOrFallback;
import static org.sejda.impl.sambox.util.FontUtils.getStandardType1Font;

import org.junit.Test;
import org.sejda.model.pdf.StandardType1Font;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.font.PDType1Font;

/**
 * @author Andrea Vacondio
 * 
 */
public class FontUtilsTest {

    @Test
    public void testGetStandardType1Fontg() {
        assertEquals(PDType1Font.COURIER, getStandardType1Font(StandardType1Font.CURIER));
        assertEquals(PDType1Font.COURIER_BOLD, getStandardType1Font(StandardType1Font.CURIER_BOLD));
        assertEquals(PDType1Font.COURIER_BOLD_OBLIQUE, getStandardType1Font(StandardType1Font.CURIER_BOLD_OBLIQUE));
        assertEquals(PDType1Font.COURIER_OBLIQUE, getStandardType1Font(StandardType1Font.CURIER_OBLIQUE));
        assertEquals(PDType1Font.HELVETICA, getStandardType1Font(StandardType1Font.HELVETICA));
        assertEquals(PDType1Font.HELVETICA_BOLD, getStandardType1Font(StandardType1Font.HELVETICA_BOLD));
        assertEquals(PDType1Font.HELVETICA_BOLD_OBLIQUE,
                getStandardType1Font(StandardType1Font.HELVETICA_BOLD_OBLIQUE));
        assertEquals(PDType1Font.HELVETICA_OBLIQUE, getStandardType1Font(StandardType1Font.HELVETICA_OBLIQUE));
        assertEquals(PDType1Font.TIMES_BOLD, getStandardType1Font(StandardType1Font.TIMES_BOLD));
        assertEquals(PDType1Font.TIMES_BOLD_ITALIC, getStandardType1Font(StandardType1Font.TIMES_BOLD_ITALIC));
        assertEquals(PDType1Font.TIMES_ITALIC, getStandardType1Font(StandardType1Font.TIMES_ITALIC));
        assertEquals(PDType1Font.TIMES_ROMAN, getStandardType1Font(StandardType1Font.TIMES_ROMAN));
        assertEquals(PDType1Font.SYMBOL, getStandardType1Font(StandardType1Font.SYMBOL));
        assertEquals(PDType1Font.ZAPF_DINGBATS, getStandardType1Font(StandardType1Font.ZAPFDINGBATS));
    }

    @Test
    public void testCanDisplay() {
        assertTrue(canDisplay("Chuck", getStandardType1Font(StandardType1Font.HELVETICA)));
        assertFalse(canDisplay("कसौटी", getStandardType1Font(StandardType1Font.HELVETICA)));
        assertFalse(canDisplay("Chuck", null));
    }

    @Test
    public void testFindFontFor() {
        assertEquals("NotoSansThai", findFontFor(new PDDocument(), "ทดสอบ").getName());
        assertNull(findFontFor(new PDDocument(), "測試"));
    }

    @Test
    public void testFontOrFallbackPositive() {
        PDType1Font expected = getStandardType1Font(StandardType1Font.HELVETICA_BOLD_OBLIQUE);
        assertEquals(expected, fontOrFallback("Chuck", expected, () -> getStandardType1Font(StandardType1Font.CURIER)));
    }

    @Test
    public void testFontOrFallbackNegative() {
        PDType1Font expected = getStandardType1Font(StandardType1Font.CURIER);
        assertEquals(expected, fontOrFallback("कसौटी", getStandardType1Font(StandardType1Font.HELVETICA_BOLD_OBLIQUE),
                () -> expected));
    }

    @Test
    public void testFontOrFallbackNullSipplier() {
        PDType1Font expected = getStandardType1Font(StandardType1Font.CURIER);
        assertEquals(expected, fontOrFallback("कसौटी", expected, null));
    }
}
