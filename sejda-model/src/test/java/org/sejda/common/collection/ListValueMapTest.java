/*
 * Created on 27/gen/2012
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Andrea Vacondio
 * 
 */
public class ListValueMapTest {

    @Test
    public void testMultiMap() {
        String value1 = "val1";
        String value2 = "val2";
        ListValueMap<Integer, String> victim = new ListValueMap<Integer, String>();
        assertEquals(0, victim.size());
        victim.put(1, value1);
        assertEquals(1, victim.size());
        assertNotNull(victim.get(1));
        victim.put(1, value2);
        assertEquals(2, victim.get(1).size());
        assertTrue(victim.remove(1, value2));
        assertFalse(victim.remove(2, value2));
        assertEquals(1, victim.size());
        victim.clear();
        assertEquals(0, victim.size());
    }
}
