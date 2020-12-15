/*
 * Created on 01/mag/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * This file is part of the Sejda source code
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.core.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.input.ProxyInputStream;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sejda.core.notification.strategy.AsyncNotificationStrategy;
import org.sejda.core.notification.strategy.SyncNotificationStrategy;
import org.sejda.model.exception.ConfigurationException;

/**
 * Test unit
 * 
 * @author Andrea Vacondio
 * 
 */
public class XmlConfigurationStrategyTest {

    @Rule
    public ExpectedException expected = ExpectedException.none();
    private ConfigurationStreamProvider provider;

    @Before
    public void setUp() {
        provider = mock(ConfigurationStreamProvider.class);
    }

    @Test
    public void testPositiveConstuctor() throws ConfigurationException {
        InputStreamWithStatus stream = new InputStreamWithStatus(
                getClass().getClassLoader().getResourceAsStream("sejda-test.xml"));
        when(provider.getConfigurationStream()).thenReturn(stream);
        XmlConfigurationStrategy victim = XmlConfigurationStrategy.newInstance(provider);
        assertEquals(SyncNotificationStrategy.class, victim.getNotificationStrategy());
        assertEquals(1, victim.getTasksMap().size());
        assertTrue(victim.isValidation());
        assertTrue(victim.isIgnoreXmlConfiguration());
        assertTrue(stream.closed);
    }

    @Test
    public void testNegativeConstuctorWrongTask() throws ConfigurationException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("failing-task-sejda-config.xml");
        expected.expectMessage(
                "The configured class java.lang.String is not a subtype of interface org.sejda.model.task.Task");
        when(provider.getConfigurationStream()).thenReturn(stream);
        XmlConfigurationStrategy.newInstance(provider);
    }

    @Test
    public void testNegativeConstuctorWrongParam() throws ConfigurationException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("failing-param-sejda-config.xml");
        expected.expectMessage(
                "The configured class java.lang.String is not a subtype of interface org.sejda.model.parameter.base.TaskParameters");
        when(provider.getConfigurationStream()).thenReturn(stream);
        XmlConfigurationStrategy.newInstance(provider);
    }

    @Test
    public void testNegativeNotFoundClassParam() throws ConfigurationException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("failing-no-param-class-sejda-config.xml");
        expected.expectMessage("Unable to find the configured bla.bla.not.existing.Class");
        when(provider.getConfigurationStream()).thenReturn(stream);
        XmlConfigurationStrategy.newInstance(provider);
    }

    @Test
    public void testPositiveNoValidation() throws ConfigurationException {
        InputStreamWithStatus stream = new InputStreamWithStatus(
                getClass().getClassLoader().getResourceAsStream("sejda-no-validation.xml"));
        when(provider.getConfigurationStream()).thenReturn(stream);
        XmlConfigurationStrategy victim = XmlConfigurationStrategy.newInstance(provider);
        assertTrue(stream.closed);
        assertFalse(victim.isValidation());
    }

    @Test
    public void testPositiveDefaultValidation() throws ConfigurationException {
        InputStreamWithStatus stream = new InputStreamWithStatus(
                getClass().getClassLoader().getResourceAsStream("sejda-default-validation.xml"));
        when(provider.getConfigurationStream()).thenReturn(stream);
        XmlConfigurationStrategy victim = XmlConfigurationStrategy.newInstance(provider);
        assertTrue(stream.closed);
        assertFalse(victim.isValidation());
        assertFalse(victim.isIgnoreXmlConfiguration());
    }

    @Test
    public void testPositiveAsyncNotification() throws ConfigurationException {
        InputStreamWithStatus stream = new InputStreamWithStatus(
                getClass().getClassLoader().getResourceAsStream("sejda-async-notification.xml"));
        when(provider.getConfigurationStream()).thenReturn(stream);
        XmlConfigurationStrategy victim = XmlConfigurationStrategy.newInstance(provider);
        assertTrue(stream.closed);
        assertEquals(AsyncNotificationStrategy.class, victim.getNotificationStrategy());
    }

    public static class InputStreamWithStatus extends ProxyInputStream {
        boolean closed = false;

        public InputStreamWithStatus(InputStream is) {
            super(is);
        }

        @Override
        public void close() throws IOException {
            closed = true;
            super.close();
        }
    }
}
