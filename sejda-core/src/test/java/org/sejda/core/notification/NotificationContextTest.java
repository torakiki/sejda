/*
 * Created on 25/apr/2010
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
package org.sejda.core.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.mockito.ArgumentCaptor;
import org.sejda.core.Sejda;
import org.sejda.core.notification.context.GlobalNotificationContext;
import org.sejda.core.notification.context.NotificationContext;
import org.sejda.core.notification.context.ThreadLocalNotificationContext;
import org.sejda.model.notification.EventListener;
import org.sejda.model.notification.event.PercentageOfWorkDoneChangedEvent;
import org.sejda.model.notification.event.TaskExecutionFailedEvent;
import org.sejda.model.notification.event.TaskExecutionStartedEvent;
import org.sejda.model.task.NotifiableTaskMetadata;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * @author Andrea Vacondio
 */
@Isolated
public class NotificationContextTest {

    private List<NotificationContext> contexts = new ArrayList<NotificationContext>();

    @BeforeEach
    public void setUp() {
        System.setProperty(Sejda.USER_CONFIG_FILE_PROPERTY_NAME, "sejda-test.xml");
        contexts.add(GlobalNotificationContext.getContext());
        contexts.add(ThreadLocalNotificationContext.getContext());
    }

    @Test
    public void testAddAndClear() {
        for (NotificationContext victim : contexts) {
            victim.clearListeners();
            assertEquals(0, victim.size());
            testNotificationContextAddListener(victim);
            testNotificationContextClear(victim);
            testNotificationContextRemoveListener(victim);
        }
    }

    @Test
    public void testNotificationContextNotify() {
        for (NotificationContext victim : contexts) {
            testNotificationContextNotify(victim);
            testNotificationContextUndetermined(victim);
        }
    }

    private void testNotificationContextNotify(NotificationContext victim) {
        EventListener<TaskExecutionStartedEvent> listener = mock(EventListener.class);
        EventListener<PercentageOfWorkDoneChangedEvent> secondListener = mock(EventListener.class);
        EventListener<TaskExecutionFailedEvent> thirdListener = mock(EventListener.class);
        victim.addListener(TaskExecutionStartedEvent.class, listener);
        victim.addListener(PercentageOfWorkDoneChangedEvent.class, secondListener);
        victim.addListener(TaskExecutionFailedEvent.class, thirdListener);
        BigDecimal value = new BigDecimal("32");
        PercentageOfWorkDoneChangedEvent event = new PercentageOfWorkDoneChangedEvent(value,
                NotifiableTaskMetadata.NULL);
        assertFalse(event.isUndetermined());
        victim.notifyListeners(event);
        victim.notifyListeners(new TaskExecutionStartedEvent(NotifiableTaskMetadata.NULL));
        ArgumentCaptor<PercentageOfWorkDoneChangedEvent> argument = ArgumentCaptor.forClass(
                PercentageOfWorkDoneChangedEvent.class);
        verify(secondListener).onEvent(argument.capture());
        assertEquals(value, argument.getValue().getPercentage());
        assertFalse(argument.getValue().isUndetermined());
        verify(listener).onEvent(any());
        verify(thirdListener, never()).onEvent(any());
    }

    private void testNotificationContextUndetermined(NotificationContext victim) {
        EventListener<PercentageOfWorkDoneChangedEvent> listener = mock(EventListener.class);
        ArgumentCaptor<PercentageOfWorkDoneChangedEvent> argument = ArgumentCaptor.forClass(
                PercentageOfWorkDoneChangedEvent.class);
        victim.addListener(PercentageOfWorkDoneChangedEvent.class, listener);
        PercentageOfWorkDoneChangedEvent event = new PercentageOfWorkDoneChangedEvent(
                PercentageOfWorkDoneChangedEvent.UNDETERMINED, NotifiableTaskMetadata.NULL);
        assertTrue(event.isUndetermined());
        victim.notifyListeners(event);
        verify(listener).onEvent(argument.capture());
        assertTrue(argument.getValue().isUndetermined());
    }

    private void testNotificationContextAddListener(NotificationContext victim) {
        EventListener<TaskExecutionStartedEvent> listener = mock(EventListener.class);
        victim.addListener(TaskExecutionStartedEvent.class, listener);
        assertEquals(1, victim.size());
        EventListener<PercentageOfWorkDoneChangedEvent> listener2 = mock(EventListener.class);
        victim.addListener(PercentageOfWorkDoneChangedEvent.class, listener2);
        assertEquals(2, victim.size());
    }

    private void testNotificationContextRemoveListener(NotificationContext victim) {
        EventListener<TaskExecutionStartedEvent> listener = new TestListenerStart();
        victim.addListener(listener);
        assertEquals(1, victim.size());
        victim.removeListener(listener);
        assertEquals(0, victim.size());
    }

    /**
     * @param victim
     */
    private void testNotificationContextClear(NotificationContext victim) {
        assertFalse(0 == victim.size());
        victim.clearListeners();
        assertEquals(0, victim.size());
    }

    public static class TestListenerStart implements EventListener<TaskExecutionStartedEvent> {
        @Override
        public void onEvent(TaskExecutionStartedEvent event) {
            //nothing
        }
    }
}
