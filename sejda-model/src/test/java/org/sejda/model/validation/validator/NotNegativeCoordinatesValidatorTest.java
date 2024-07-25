/*
 * Created on 10/set/2011
 * Copyright 2011 Sober Lemur S.r.l. and Sejda BV.
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

import org.junit.jupiter.api.Test;
import org.sejda.model.RectangularBox;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Andrea Vacondio
 */
public class NotNegativeCoordinatesValidatorTest {

    private NotNegativeCoordinatesValidator victim = new NotNegativeCoordinatesValidator();

    @Test
    public void testNull() {
        assertTrue(victim.isValid(null, null));
    }

    @Test
    public void testBottomNegative() {
        var box = RectangularBox.newInstance(-1, 0, 10, 10);
        assertFalse(victim.isValid(box, null));
    }

    @Test
    public void testLeftNegative() {
        var box = RectangularBox.newInstance(0, -1, 10, 10);
        assertFalse(victim.isValid(box, null));
    }

    @Test
    public void testValid() {
        var box = RectangularBox.newInstance(0, 0, 10, 10);
        assertTrue(victim.isValid(box, null));
    }
}
