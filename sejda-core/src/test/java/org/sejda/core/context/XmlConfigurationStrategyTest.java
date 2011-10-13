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
package org.sejda.core.context;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.internal.matchers.Contains;
import org.sejda.core.exception.ConfigurationException;
import org.sejda.core.notification.strategy.SyncNotificationStrategy;

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
    public void testPositiveConstuctor() throws ConfigurationException, IOException {
        InputStream stream = spy(getClass().getClassLoader().getResourceAsStream("sejda.xml"));
        when(provider.getConfigurationStream()).thenReturn(stream);
        XmlConfigurationStrategy victim = XmlConfigurationStrategy.newInstance(provider);
        verify(stream, atLeastOnce()).close();
        assertEquals(SyncNotificationStrategy.class, victim.getNotificationStrategy());
        assertEquals(1, victim.getTasksMap().size());
    }

    @Test
    public void testNegativeConstuctorWrongTask() throws ConfigurationException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("failing-task-sejda-config.xml");
        expected.expectMessage(new Contains(
                "The configured class java.lang.String is not a subtype of interface org.sejda.core.manipulation.model.task.Task"));
        when(provider.getConfigurationStream()).thenReturn(stream);
        XmlConfigurationStrategy.newInstance(provider);
    }

    @Test
    public void testNegativeConstuctorWrongParam() throws ConfigurationException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("failing-param-sejda-config.xml");
        expected.expectMessage(new Contains(
                "The configured class java.lang.String is not a subtype of interface org.sejda.core.manipulation.model.parameter.base.TaskParameters"));
        when(provider.getConfigurationStream()).thenReturn(stream);
        XmlConfigurationStrategy.newInstance(provider);
    }

    @Test
    public void testNegativeNotFoundClassParam() throws ConfigurationException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("failing-no-param-class-sejda-config.xml");
        expected.expectMessage(new Contains("Unable to find the configured bla.bla.not.existing.Class"));
        when(provider.getConfigurationStream()).thenReturn(stream);
        XmlConfigurationStrategy.newInstance(provider);
    }
}
