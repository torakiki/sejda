/*
 * Created on 11 set 2015
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Andrea Vacondio
 *
 */
public class LookupTableTest {
    private LookupTable<String> victim;

    @Before
    public void setUp() {
        victim = new LookupTable<>();
    }

    @Test
    public void cleanIsEmpty() {
        assertTrue(victim.isEmpty());
        victim.addLookupEntry("this", "that");
        assertFalse(victim.isEmpty());
        victim.clear();
        assertTrue(victim.isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void nullFirst() {
        victim.addLookupEntry(null, "not null");
    }

    @Test(expected = NullPointerException.class)
    public void nullSecond() {
        victim.addLookupEntry("not null", null);
    }

    @Test
    public void lookup() {
        victim.addLookupEntry("this", "that");
        victim.addLookupEntry("this1", "that1");
        victim.addLookupEntry("this2", "that2");
        assertEquals("that2", victim.lookup("this2"));
    }

    @Test
    public void first() {
        victim.addLookupEntry("this", "that");
        victim.addLookupEntry("this1", "that1");
        victim.addLookupEntry("this2", "that2");
        assertEquals("that", victim.first());
    }

    @Test
    public void firstNull() {
        assertNull(victim.first());
    }

    @Test
    public void values() {
        victim.addLookupEntry("this", "that");
        victim.addLookupEntry("this1", "that1");
        victim.addLookupEntry("this2", "that2");
        assertEquals(3, victim.values().size());
    }

    @Test
    public void keys() {
        victim.addLookupEntry("this", "that");
        victim.addLookupEntry("this1", "that1");
        victim.addLookupEntry("this", "that2");
        assertEquals(2, victim.values().size());
    }
}
