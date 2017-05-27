package org.sejda.model.validation.validator;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.*;

public class NotNegativeNumberValidatorTest {

    private NotNegativeNumberValidator victim = new NotNegativeNumberValidator();

    @Test
    public void testNull() {
        assertTrue(victim.isValid(null, null));
    }

    @Test
    public void testValidBigDecimal() {
        assertTrue(victim.isValid(BigDecimal.ONE, null));
        assertTrue(victim.isValid(BigDecimal.ZERO, null));
    }

    @Test
    public void testInvalidBigDecimal() {
        assertFalse(victim.isValid(new BigDecimal("-1.111"), null));
    }

    @Test
    public void testValidBigInteger() {
        assertTrue(victim.isValid(BigInteger.ONE, null));
        assertTrue(victim.isValid(BigInteger.ZERO, null));
    }

    @Test
    public void testInvalidBigInteger() {
        assertFalse(victim.isValid(new BigInteger("-3"), null));
    }

    @Test
    public void testValidLong() {
        assertTrue(victim.isValid(1l, null));
        assertTrue(victim.isValid(0l, null));
    }

    @Test
    public void testInvalidLong() {
        assertFalse(victim.isValid(-1l, null));
    }

    @Test
    public void testValiFloat() {
        assertTrue(victim.isValid(0.3f, null));
        assertTrue(victim.isValid(0f, null));
    }

    @Test
    public void testInvalidFloat() {
        assertFalse(victim.isValid(-0.1f, null));
    }

    @Test
    public void testValiDouble() {
        assertTrue(victim.isValid(0.3d, null));
        assertTrue(victim.isValid(0d, null));
    }

    @Test
    public void testInvaliddouble() {
        assertFalse(victim.isValid(-0.1d, null));
    }

}