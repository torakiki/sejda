/*
 * Copyright 2021 by Edi Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.model;

import org.junit.jupiter.api.Test;
import org.sejda.model.TestUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TopLeftRectangularBoxTest {
    
    @Test
    public void containsPoint() {
        TopLeftRectangularBox box = new TopLeftRectangularBox(10, 20, 5, 6);
        assertTrue(box.containsPoint(10, 20));
        assertTrue(box.containsPoint(11, 21));
        assertTrue(box.containsPoint(15, 26));

        assertFalse(box.containsPoint(9, 20));
        assertFalse(box.containsPoint(10, 19));
        assertFalse(box.containsPoint(9, 19));
        assertFalse(box.containsPoint(15, 27));
        assertFalse(box.containsPoint(16, 26));
        assertFalse(box.containsPoint(16, 27));
    }

    @Test
    public void testEquals() {
        var eq1 = new TopLeftRectangularBox(10, 20, 5, 6);
        var eq2 = new TopLeftRectangularBox(10, 20, 5, 6);
        var eq3 = new TopLeftRectangularBox(10, 20, 5, 6);
        var diff = new TopLeftRectangularBox(11, 30, 5, 6);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }
}
