/*
 * Created on 22 ott 2016
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
package org.sejda.model.validation.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Dimension;

import org.junit.Test;

/**
 * @author Andrea Vacondio
 *
 */
public class PositiveDimensionValidatorTest {
    private PositiveDimensionValidator victim = new PositiveDimensionValidator();

    @Test
    public void testNull() {
        assertTrue(victim.isValid(null, null));
    }

    @Test
    public void positive() {
        assertTrue(victim.isValid(new Dimension(10, 20), null));
    }

    @Test
    public void negative() {
        assertFalse(victim.isValid(new Dimension(-10, -20), null));
    }

    @Test
    public void negativeWidth() {
        assertFalse(victim.isValid(new Dimension(-10, 20), null));
    }

    @Test
    public void negativeHeight() {
        assertFalse(victim.isValid(new Dimension(10, -20), null));
    }

}
