/*
 * Created on 18/set/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sejda.TestUtils;

/**
 * Test unit for the Bounds class
 * 
 * @author Andrea Vacondio
 * 
 */
public class PageRangeTest {

    @Test
    public void testIntersect() {
        PageRange base = new PageRange(0, 10);
        PageRange noIntersection = new PageRange(11, 12);
        PageRange intersecion = new PageRange(5, 15);
        PageRange included = new PageRange(2, 5);
        assertTrue(base.intersects(intersecion));
        assertTrue(base.intersects(included));
        assertFalse(base.intersects(noIntersection));
    }

    @Test
    public void testEquals() {
        PageRange eq1 = new PageRange(1, 10);
        PageRange eq2 = new PageRange(1, 10);
        PageRange eq3 = new PageRange(1, 10);
        PageRange diff = new PageRange(1, 9);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void testUnbounded() {
        PageRange victim = new PageRange(10);
        assertTrue(victim.isUnbounded());
    }

    @Test
    public void testGetPages() {
        PageRange victim = new PageRange(1, 9);
        assertEquals(9, victim.getPages(50).size());
        PageRange victim2 = new PageRange(10);
        assertEquals(16, victim2.getPages(25).size());
    }

    @Test
    public void testToString() {
        assertEquals("1-2", new PageRange(1, 2).toString());
        assertEquals("2", new PageRange(2, 2).toString());
        assertEquals("2-", new PageRange(2).toString());
    }

    @Test
    public void testContains() {
        assertTrue(new PageRange(1,1).contains(1));
        assertTrue(new PageRange(1,2).contains(1));
        assertTrue(new PageRange(1,2).contains(2));

        assertTrue(new PageRange(1).contains(1));
        assertTrue(new PageRange(1).contains(2));

        assertFalse(new PageRange(2).contains(1));
        assertFalse(new PageRange(2, 3).contains(1));
        assertFalse(new PageRange(2, 3).contains(4));
    }
}
