package org.sejda.core.context;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.sejda.core.Sejda;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test for the GlobalConfiguration
 * 
 * @author Andrea Vacondio
 * 
 */
@Isolated
public class GlobalConfigurationTest {

    @Test
    public void testConstructor() {
        System.setProperty(Sejda.USER_CONFIG_FILE_PROPERTY_NAME, "sejda-test.xml");
        GlobalConfiguration config = GlobalConfiguration.getInstance();
        assertTrue(config.isValidation());
        assertEquals(1, config.getTasksRegistry().getTasks().size());
    }
}
