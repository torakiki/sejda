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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.sejda.core.TestListenerFactory.newGeneralListener;
import static org.sejda.core.TestListenerFactory.newPercentageListener;
import static org.sejda.core.TestListenerFactory.newStartListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sejda.core.Sejda;
import org.sejda.core.TestListenerFactory.TestListenerAny;
import org.sejda.core.TestListenerFactory.TestListenerPercentage;
import org.sejda.core.TestListenerFactory.TestListenerStart;
import org.sejda.core.notification.context.GlobalNotificationContext;
import org.sejda.core.notification.context.NotificationContext;
import org.sejda.core.notification.context.ThreadLocalNotificationContext;
import org.sejda.model.notification.event.PercentageOfWorkDoneChangedEvent;
import org.sejda.model.notification.event.TaskExecutionFailedEvent;
import org.sejda.model.task.NotifiableTaskMetadata;

/**
 * @author Andrea Vacondio
 * 
 */
public class NotificationContextTest {

    private List<NotificationContext> contexts = new ArrayList<NotificationContext>();

    @Before
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
        TestListenerPercentage listener = newPercentageListener();
        TestListenerAny<PercentageOfWorkDoneChangedEvent> secondListener = newGeneralListener();
        TestListenerAny<TaskExecutionFailedEvent> thirdListener = newGeneralListener();
        victim.addListener(listener);
        victim.addListener(PercentageOfWorkDoneChangedEvent.class, secondListener);
        victim.addListener(TaskExecutionFailedEvent.class, thirdListener);
        BigDecimal value = new BigDecimal("32");
        PercentageOfWorkDoneChangedEvent event = new PercentageOfWorkDoneChangedEvent(value,
                NotifiableTaskMetadata.NULL);
        assertFalse(event.isUndetermined());
        victim.notifyListeners(event);
        assertEquals(value, listener.getPercentage());
        assertFalse(listener.isUndeterminate());
        assertTrue(secondListener.hasListened());
        assertFalse(thirdListener.hasListened());
    }

    private void testNotificationContextUndetermined(NotificationContext victim) {
        TestListenerPercentage listener = newPercentageListener();
        victim.addListener(listener);
        PercentageOfWorkDoneChangedEvent event = new PercentageOfWorkDoneChangedEvent(
                PercentageOfWorkDoneChangedEvent.UNDETERMINED, NotifiableTaskMetadata.NULL);
        assertTrue(event.isUndetermined());
        victim.notifyListeners(event);
        assertTrue(listener.isUndeterminate());
    }

    private void testNotificationContextAddListener(NotificationContext victim) {
        victim.addListener(newStartListener());
        assertEquals(1, victim.size());
        TestListenerAny<PercentageOfWorkDoneChangedEvent> listener = newGeneralListener();
        victim.addListener(PercentageOfWorkDoneChangedEvent.class, listener);
        assertEquals(2, victim.size());
    }

    private void testNotificationContextRemoveListener(NotificationContext victim) {
        TestListenerStart listener = newStartListener();
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
}
