package org.sejda.core.context;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for the GlobalConfiguration
 * 
 * @author Andrea Vacondio
 * 
 */
public class GlobalConfigurationTest {

    @Test
    public void testConstructor() {
        GlobalConfiguration config = GlobalConfiguration.getInstance();
        Assert.assertTrue(config.isValidation());
        Assert.assertEquals(1, config.getTaskRegistry().getTasks().size());
    }
}
