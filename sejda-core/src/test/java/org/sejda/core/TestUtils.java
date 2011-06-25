package org.sejda.core;

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
}
