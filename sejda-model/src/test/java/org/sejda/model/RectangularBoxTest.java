/*
 * Created on 08/set/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
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
