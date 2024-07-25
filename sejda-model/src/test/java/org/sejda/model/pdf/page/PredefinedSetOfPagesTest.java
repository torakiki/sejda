/*
 * Created on 03/ago/2011
 * Copyright 2010 Sober Lemur S.r.l. and Sejda BV.
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
package org.sejda.model.pdf.page;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Andrea Vacondio
 * 
 */
public class PredefinedSetOfPagesTest {

    @Test
    public void getPages() {
        assertEquals(10, PredefinedSetOfPages.ALL_PAGES.getPages(10).size());
        assertEquals(5, PredefinedSetOfPages.EVEN_PAGES.getPages(10).size());
        assertEquals(5, PredefinedSetOfPages.ODD_PAGES.getPages(10).size());
        assertEquals(0, PredefinedSetOfPages.NONE.getPages(0).size());
    }

    @Test
    public void includes() {
        assertTrue(PredefinedSetOfPages.ALL_PAGES.includes(2));
        assertTrue(PredefinedSetOfPages.EVEN_PAGES.includes(2));
        assertTrue(PredefinedSetOfPages.ODD_PAGES.includes(3));

        assertFalse(PredefinedSetOfPages.ODD_PAGES.includes(2));
        assertFalse(PredefinedSetOfPages.NONE.includes(2));
        assertFalse(PredefinedSetOfPages.EVEN_PAGES.includes(3));
    }
}
