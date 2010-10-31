/*
 * Created on 01/mag/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.core.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.sejda.core.exception.ConfigurationException;
import org.sejda.core.notification.strategy.SyncNotificationStrategy;

/**
 * Test unit
 * 
 * @author Andrea Vacondio
 * 
 */
public class XmlConfigurationStrategyTest {

    @Test
    public void testPositiveConstuctor() throws ConfigurationException, IOException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("default-sejda-config.xml");
        XmlConfigurationStrategy victim = new XmlConfigurationStrategy(stream);
        stream.close();
        assertEquals(SyncNotificationStrategy.class, victim.getNotificationStrategy());
        assertEquals(1, victim.getTasksMap().size());
    }

    @Test(expected = ConfigurationException.class)
    public void testNegativeConstuctor() throws IOException, ConfigurationException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("failing-sejda-config.xml");
        try {
            XmlConfigurationStrategy victim = new XmlConfigurationStrategy(stream);
            victim.toString();
            fail();
        } finally {
            stream.close();
        }

    }
}
