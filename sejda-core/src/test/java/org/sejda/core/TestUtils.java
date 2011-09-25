package org.sejda.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.junit.Assert;
import org.junit.Ignore;
import org.sejda.core.manipulation.model.parameter.base.TaskParameters;
import org.sejda.core.validation.DefaultValidationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test utilitites
 * 
 * @author Andrea Vacondio
 * 
 */
@Ignore
public final class TestUtils {

    private static final Logger LOG = LoggerFactory.getLogger(TestUtils.class);

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

    public static void assertInvalidParameters(TaskParameters parameters) {
        Set<ConstraintViolation<TaskParameters>> violations = DefaultValidationContext.getContext().getValidator()
                .validate(parameters);
        for (ConstraintViolation<TaskParameters> violation : violations) {
            LOG.debug("{}: {}", violation.getPropertyPath(), violation.getMessage());
        }
        Assert.assertFalse(violations.isEmpty());
    }

    public static void assertValidParameters(TaskParameters parameters) {
        Set<ConstraintViolation<TaskParameters>> violations = DefaultValidationContext.getContext().getValidator()
                .validate(parameters);
        Assert.assertFalse(violations.isEmpty());
    }

}
