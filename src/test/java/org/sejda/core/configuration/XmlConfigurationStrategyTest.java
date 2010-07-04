/*
 * Created on 01/mag/2010
 * Copyright (C) 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.sejda.core.configuration;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

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
        assertTrue(victim.getTasksMap().size() == 1);
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
