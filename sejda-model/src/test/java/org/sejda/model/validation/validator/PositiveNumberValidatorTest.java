/*
 * Created on 26/mar/2013
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

}
