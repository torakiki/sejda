/*
 * Created on 10/set/2011
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
package org.sejda.model.validation.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.model.RectangularBox;

/**
 * @author Andrea Vacondio
 * 
 */
public class NotNegativeCoordinatesValidatorTest {

    private NotNegativeCoordinatesValidator victim = new NotNegativeCoordinatesValidator();
    private RectangularBox box;

    @Before
    public void setUp() {
        box = RectangularBox.newInstance(0, 0, 10, 10);
    }

    @Test
    public void testNull() {
        assertTrue(victim.isValid(null, null));
    }

    @Test
    public void testTopNegative() {
        TestUtils.setProperty(box, "top", -1);
        assertFalse(victim.isValid(box, null));
    }

    @Test
    public void testBottomNegative() {
        TestUtils.setProperty(box, "bottom", -1);
        assertFalse(victim.isValid(box, null));
    }

    @Test
    public void testLeftNegative() {
        TestUtils.setProperty(box, "left", -1);
        assertFalse(victim.isValid(box, null));
    }

    @Test
    public void testRightNegative() {
        TestUtils.setProperty(box, "right", -1);
        assertFalse(victim.isValid(box, null));
    }

    @Test
    public void testValid() {
        assertTrue(victim.isValid(box, null));
    }
}
