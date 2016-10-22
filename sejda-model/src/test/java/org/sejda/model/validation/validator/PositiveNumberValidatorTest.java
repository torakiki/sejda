/*
 * Created on 26/mar/2013
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

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

/**
 * @author Andrea Vacondio
 * 
 */
public class PositiveNumberValidatorTest {
    private PositiveNumberValidator victim = new PositiveNumberValidator();

    @Test
    public void testNull() {
        assertTrue(victim.isValid(null, null));
    }

    @Test
    public void testValidBigDecimal() {
        assertTrue(victim.isValid(BigDecimal.ONE, null));
    }

    @Test
    public void testInvalidBigDecimal() {
        assertFalse(victim.isValid(BigDecimal.ZERO, null));
    }

    @Test
    public void testValidBigInteger() {
        assertTrue(victim.isValid(BigInteger.ONE, null));
    }

    @Test
    public void testInvalidBigInteger() {
        assertFalse(victim.isValid(BigInteger.ZERO, null));
    }

    @Test
    public void testValidLong() {
        assertTrue(victim.isValid(1l, null));
    }

    @Test
    public void testInvalidLong() {
        assertFalse(victim.isValid(-1l, null));
    }

    @Test
    public void testValiFloat() {
        assertTrue(victim.isValid(0.3f, null));
    }

    @Test
    public void testInvalidFloat() {
        assertFalse(victim.isValid(-0.1f, null));
    }

    @Test
    public void testValiDouble() {
        assertTrue(victim.isValid(0.3d, null));
    }

    @Test
    public void testInvaliddouble() {
        assertFalse(victim.isValid(-0.1d, null));
    }

}
