/*
 * Created on 29/mag/2010
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
package org.sejda.core.manipulation.model.rotation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test unit for the page rotation
 * 
 * @author Andrea Vacondio
 * 
 */
public class PageRotationTest {

    @Test
    public void testAccept() {
        PageRotation victim = new PageRotation(2, Rotation.DEGREES_180);
        assertTrue(victim.accept(2));
        assertFalse(victim.accept(1));

        victim = new PageRotation(Rotation.DEGREES_270, RotationType.EVEN_PAGES);
        assertTrue(victim.accept(2));
        assertFalse(victim.accept(1));

        victim = new PageRotation(Rotation.DEGREES_270, RotationType.ODD_PAGES);
        assertTrue(victim.accept(1));
        assertFalse(victim.accept(2));

        victim = new PageRotation(Rotation.DEGREES_270, RotationType.ALL_PAGES);
        assertTrue(victim.accept(2));
        assertTrue(victim.accept(1));

    }


}
