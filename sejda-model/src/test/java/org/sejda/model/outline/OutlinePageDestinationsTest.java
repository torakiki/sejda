/*
 * Created on 09/ago/2011
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
package org.sejda.model.outline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Andrea Vacondio
 * 
 */
public class OutlinePageDestinationsTest {

    @Test
    public void testAdd() {
        OutlinePageDestinations victim = new OutlinePageDestinations();
        assertTrue(victim.getPages().isEmpty());
        victim.addPage(1, "Title1");
        assertFalse(victim.getPages().isEmpty());
        assertEquals(1, victim.getPages().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNull() {
        OutlinePageDestinations victim = new OutlinePageDestinations();
        victim.addPage(null, "Title1");
    }

    @Test
    public void testGetPages() {
        OutlinePageDestinations victim = new OutlinePageDestinations();
        victim.addPage(1, "Title1");
        victim.addPage(2, "Title2");
        assertEquals(2, victim.getPages().size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnmodifiablePagesSet() {
        OutlinePageDestinations victim = new OutlinePageDestinations();
        victim.addPage(1, "Title1");
        victim.addPage(2, "Title2");
        victim.getPages().add(3);
    }

    public void testGetTitle() {
        OutlinePageDestinations victim = new OutlinePageDestinations();
        victim.addPage(1, "Title1");
        assertEquals("Title1", victim.getTitle(1));
    }
}
