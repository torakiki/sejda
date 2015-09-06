/*
 * Created on 22/ago/2011
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
package org.sejda.common.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * @author Andrea Vacondio
 * 
 */
public class NullSafeSetTest {

    @Test
    public void testAdd() {
        Set<String> victim = new NullSafeSet<String>();
        assertFalse(victim.add(null));
        assertTrue(victim.isEmpty());
        assertTrue(victim.add("test"));
        assertEquals(1, victim.size());
    }

    @Test
    public void testAddWithDelegate() {
        Set<String> victim = new NullSafeSet<String>(new HashSet<>());
        assertFalse(victim.add(null));
        assertTrue(victim.isEmpty());
        assertTrue(victim.add("test"));
        assertEquals(1, victim.size());
    }
}
