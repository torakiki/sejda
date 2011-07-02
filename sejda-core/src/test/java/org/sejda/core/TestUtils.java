package org.sejda.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

/**
 * Test utilitites
 * 
 * @author Andrea Vacondio
 * 
 */
public final class TestUtils {

    private TestUtils() {
        // util
    }

    /**
     * Sets the given property to the given instance at the given value.
     * 
     * @param instance
     * @param propertyName
     * @param propertyValue
     */
    public static void setProperty(Object instance, String propertyName, Object propertyValue) {
        Field field;
        try {
            field = instance.getClass().getDeclaredField(propertyName);
            field.setAccessible(true);
            field.set(instance, propertyValue);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(String.format("Unable to set field %s", propertyName), e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(String.format("Unable to set field %s", propertyName), e);
        }
    }

    /**
     * Test that the equals and hashCode implementations respect the general rules being reflexive, transitive and symmetric.
     * 
     * @param <T>
     * @param eq1
     *            equal instance
     * @param eq2
     *            equal instance
     * @param eq3
     *            equal instance
     * @param diff
     *            not equal instance
     */
    public static <T> void testEqualsAndHashCodes(T eq1, T eq2, T eq3, T diff) {
        // reflexive
        assertTrue(eq1.equals(eq1));
        assertTrue(eq1.hashCode() == eq1.hashCode());

        // symmetric
        assertTrue(eq1.equals(eq2));
        assertTrue(eq2.equals(eq1));
        assertTrue(eq1.hashCode() == eq2.hashCode());
        assertFalse(eq2.equals(diff));
        assertFalse(diff.equals(eq2));
        assertFalse(diff.hashCode() == eq2.hashCode());

        // transitive
        assertTrue(eq1.equals(eq2));
        assertTrue(eq2.equals(eq3));
        assertTrue(eq1.equals(eq3));
        assertTrue(eq1.hashCode() == eq2.hashCode());
        assertTrue(eq2.hashCode() == eq3.hashCode());
        assertTrue(eq1.hashCode() == eq3.hashCode());
    }
}
