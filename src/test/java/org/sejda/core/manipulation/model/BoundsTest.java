/*
 * Created on 18/set/2010
 * Copyright (C) 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.sejda.core.manipulation.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test unit for the Bounds class
 * 
 * @author Andrea Vacondio
 * 
 */
public class BoundsTest {

    @Test
    public void testIntersect() {
        Bounds base = new Bounds(0, 10);
        Bounds noIntersection = new Bounds(11, 12);
        Bounds intersecion = new Bounds(5, 15);
        Bounds included = new Bounds(2, 5);
        assertTrue(base.intersects(intersecion));
        assertTrue(base.intersects(included));
        assertFalse(base.intersects(noIntersection));
    }
}
