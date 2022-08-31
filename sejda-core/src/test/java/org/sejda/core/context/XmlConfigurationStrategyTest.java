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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sejda.core.notification.strategy.AsyncNotificationStrategy;
import org.sejda.core.notification.strategy.SyncNotificationStrategy;
import org.sejda.model.exception.ConfigurationException;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test unit
 *
 * @author Andrea Vacondio
 */
public class XmlConfigurationStrategyTest {

    private ConfigurationStreamProvider provider;

    @BeforeEach
    public void setUp() {
        provider = mock(ConfigurationStreamProvider.class);
    }

    @Test
    public void testPositiveConstuctor() throws ConfigurationException, IOException {
        var stream = getClass().getResourceAsStream("/sejda-test.xml");
        when(provider.getConfigurationStream()).thenReturn(stream);
        XmlConfigurationStrategy victim = XmlConfigurationStrategy.newInstance(provider);
        assertEquals(SyncNotificationStrategy.class, victim.getNotificationStrategy());
        assertEquals(1, victim.getTasksMap().size());
        assertTrue(victim.isValidation());
        assertTrue(victim.isIgnoreXmlConfiguration());
    }

    @Test
    public void testNegativeConstuctorWrongTask() throws ConfigurationException {
        InputStream stream = getClass().getResourceAsStream("/failing-task-sejda-config.xml");
        when(provider.getConfigurationStream()).thenReturn(stream);
        var e = assertThrows(ConfigurationException.class, () -> XmlConfigurationStrategy.newInstance(provider));
        assertEquals("The configured class java.lang.String is not a subtype of interface org.sejda.model.task.Task",
                e.getMessage());
    }

    @Test
    public void testNegativeConstuctorWrongParam() throws ConfigurationException {
        InputStream stream = getClass().getResourceAsStream("/failing-param-sejda-config.xml");
        when(provider.getConfigurationStream()).thenReturn(stream);
        var e = assertThrows(ConfigurationException.class, () -> XmlConfigurationStrategy.newInstance(provider));
        assertEquals(
                "The configured class java.lang.String is not a subtype of interface org.sejda.model.parameter.base.TaskParameters",
                e.getMessage());
    }

    @Test
    public void testNegativeNotFoundClassParam() throws ConfigurationException {
        InputStream stream = getClass().getResourceAsStream("/failing-no-param-class-sejda-config.xml");
        when(provider.getConfigurationStream()).thenReturn(stream);
        var e = assertThrows(ConfigurationException.class, () -> XmlConfigurationStrategy.newInstance(provider));
        assertEquals("Unable to find the configured bla.bla.not.existing.Class", e.getMessage());
    }

    @Test
    public void testPositiveNoValidation() throws ConfigurationException, IOException {
        var stream = getClass().getResourceAsStream("/sejda-no-validation.xml");
        when(provider.getConfigurationStream()).thenReturn(stream);
        XmlConfigurationStrategy victim = XmlConfigurationStrategy.newInstance(provider);
        assertFalse(victim.isValidation());
    }

    @Test
    public void testPositiveDefaultValidation() throws ConfigurationException, IOException {
        var stream = getClass().getResourceAsStream("/sejda-default-validation.xml");
        when(provider.getConfigurationStream()).thenReturn(stream);
        XmlConfigurationStrategy victim = XmlConfigurationStrategy.newInstance(provider);
        assertFalse(victim.isValidation());
        assertFalse(victim.isIgnoreXmlConfiguration());
    }

    @Test
    public void testPositiveAsyncNotification() throws ConfigurationException, IOException {
        var stream = getClass().getResourceAsStream("/sejda-async-notification.xml");
        when(provider.getConfigurationStream()).thenReturn(stream);
        XmlConfigurationStrategy victim = XmlConfigurationStrategy.newInstance(provider);
        assertEquals(AsyncNotificationStrategy.class, victim.getNotificationStrategy());
    }

    public void testStreamIsClosed() throws ConfigurationException, IOException {
        var stream = getClass().getResourceAsStream("/sejda-default-validation.xml");
        when(provider.getConfigurationStream()).thenReturn(stream);
        XmlConfigurationStrategy victim = XmlConfigurationStrategy.newInstance(provider);
        var e = assertThrows(IOException.class, () -> stream.read());
        assertThat(e.getMessage(), containsString("its closed"));
    }
}
