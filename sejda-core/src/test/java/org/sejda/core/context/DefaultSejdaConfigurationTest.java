package org.sejda.core.context;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;
import org.sejda.core.Sejda;
import org.sejda.core.service.ChildTestTaskParameter;
import org.sejda.core.service.TestTaskParameter;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskNotFoundException;
import org.sejda.model.parameter.base.TaskParameters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Test for the GlobalConfiguration
 *
 * @author Andrea Vacondio
 */
@Isolated
public class DefaultSejdaConfigurationTest {

    @Test
    @ResourceLock(Resources.SYSTEM_PROPERTIES)
    public void testConstructor() {
        System.setProperty(Sejda.USER_CONFIG_FILE_PROPERTY_NAME, "sejda-test.xml");
        var config = new DefaultSejdaConfiguration();
        assertTrue(config.isValidation());
        assertEquals(1, config.getTasksRegistry().getTasks().size());
    }

    @Test
    @ResourceLock(Resources.SYSTEM_PROPERTIES)
    public void testGetTaskPositive() throws TaskException {
        System.setProperty(Sejda.USER_CONFIG_FILE_PROPERTY_NAME, "sejda-test.xml");
        var config = new DefaultSejdaConfiguration();
        var task = config.getTask(new TestTaskParameter());
        assertNotNull(task);
    }

    @Test
    @ResourceLock(Resources.SYSTEM_PROPERTIES)
    public void testGetTaskPositiveNearest() throws TaskException {
        System.setProperty(Sejda.USER_CONFIG_FILE_PROPERTY_NAME, "sejda-test.xml");
        var config = new DefaultSejdaConfiguration();
        var task = config.getTask(new ChildTestTaskParameter());
        assertNotNull(task);
    }

    @Test
    @ResourceLock(Resources.SYSTEM_PROPERTIES)
    public void testGetTaskNegative() {
        System.setProperty(Sejda.USER_CONFIG_FILE_PROPERTY_NAME, "sejda-test.xml");
        var config = new DefaultSejdaConfiguration();
        assertThrows(TaskNotFoundException.class, () -> config.getTask(mock(TaskParameters.class)));
    }

    @Test
    @ResourceLock(Resources.SYSTEM_PROPERTIES)
    public void testNoValidation() {
        System.setProperty(Sejda.USER_CONFIG_FILE_PROPERTY_NAME, "sejda-no-validation.xml");
        var config = new DefaultSejdaConfiguration();
        assertFalse(config.isValidation());
    }
}
