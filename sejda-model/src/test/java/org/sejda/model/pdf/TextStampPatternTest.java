/*
 * Copyright 2015 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.model.pdf;

import static org.junit.Assert.assertEquals;
import static org.sejda.model.pdf.TextStampPattern.dateNow;

import org.junit.Test;

public class TextStampPatternTest {

    @Test
    public void testPageNumbers() {
        assertEquals("Page III - 3", new TextStampPattern().withPage(3, 17).build("Page [PAGE_ROMAN] - [PAGE_ARABIC]"));
    }

    @Test
    public void testPagesOfTotal() {
        assertEquals("Page 3 of 17", new TextStampPattern().withPage(3, 17).build("Page [PAGE_OF_TOTAL]"));
        assertEquals("Page 3 of 17", new TextStampPattern().withPage(3, 17).build("Page [PAGE_ARABIC] of [TOTAL_PAGES_ARABIC]"));
        assertEquals("Page III of XVII", new TextStampPattern().withPage(3, 17).build("Page [PAGE_ROMAN] of [TOTAL_PAGES_ROMAN]"));
    }

    @Test
    public void testDate() {
        String result = new TextStampPattern().build("FooBar [DATE]");
        String expected = "FooBar " + dateNow();
        assertEquals(expected, result);
    }

    @Test
    public void testBatesNumbering() {
        String result = new TextStampPattern().withPage(3, 17).withBatesSequence("000002").build("Case XYZ [BATES_NUMBER]");
        assertEquals("Case XYZ 000002", result);
    }
}
