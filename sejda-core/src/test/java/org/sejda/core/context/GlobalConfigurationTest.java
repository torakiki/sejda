package org.sejda.core.context;

import org.junit.Assert;
import org.junit.Test;
import org.sejda.core.Sejda;

/**
 * Test for the GlobalConfiguration
 * 
 * @author Andrea Vacondio
 * 
 */
public class GlobalConfigurationTest {

    @Test
    public void testConstructor() {
        System.setProperty(Sejda.USER_CONFIG_FILE_PROPERTY_NAME, "sejda-test.xml");
        GlobalConfiguration config = GlobalConfiguration.getInstance();
        Assert.assertTrue(config.isValidation());
        Assert.assertEquals(1, config.getTasksRegistry().getTasks().size());
    }
}
