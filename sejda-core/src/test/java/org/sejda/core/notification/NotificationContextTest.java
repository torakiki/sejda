/*
 * Created on 25/apr/2010
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
