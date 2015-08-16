/*
 * Created on 08/set/2011
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
package org.sejda.model;

import static org.junit.Assert.assertEquals;

import java.awt.Point;

import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.model.rotation.Rotation;

/**
 * @author Andrea Vacondio
 * 
 */
public class RectangularBoxTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeTop() {
        RectangularBox.newInstance(10, 0, -1, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeBottom() {
        RectangularBox.newInstance(-10, 0, 10, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeLeft() {
        RectangularBox.newInstance(0, -1, 10, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeRight() {
        RectangularBox.newInstance(0, 0, 10, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLeftGreaterRight() {
        RectangularBox.newInstance(0, 11, 10, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBottomGreaterTop() {
        RectangularBox.newInstance(11, 0, 10, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullBottomLeft() {
        RectangularBox.newInstanceFromPoints(null, new Point(10, 10));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullTopRight() {
        RectangularBox.newInstanceFromPoints(new Point(10, 10), null);
    }

    @Test
    public void testRotate() {
        RectangularBox victim = RectangularBox.newInstance(0, 1, 10, 9);
        victim.rotate(Rotation.DEGREES_270);
        assertEquals(1, victim.getBottom());
        assertEquals(0, victim.getLeft());
        assertEquals(9, victim.getTop());
        assertEquals(10, victim.getRight());
    }

    @Test
    public void testEquals() {
        RectangularBox eq1 = RectangularBox.newInstance(0, 1, 10, 9);
        RectangularBox eq2 = RectangularBox.newInstanceFromPoints(new Point(1, 0), new Point(9, 10));
        RectangularBox eq3 = RectangularBox.newInstance(0, 1, 10, 9);
        RectangularBox diff = RectangularBox.newInstance(0, 2, 10, 9);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }
}
