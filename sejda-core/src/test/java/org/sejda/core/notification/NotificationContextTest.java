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
import static org.sejda.core.TestListenerFactory.newPercentageListener;
import static org.sejda.core.TestListenerFactory.newStartListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sejda.core.TestListenerFactory.TestListenerPercentage;
import org.sejda.core.TestListenerFactory.TestListenerStart;
import org.sejda.core.notification.context.GlobalNotificationContext;
import org.sejda.core.notification.context.NotificationContext;
import org.sejda.core.notification.context.ThreadLocalNotificationContext;
import org.sejda.model.exception.NotificationContextException;
import org.sejda.model.notification.event.PercentageOfWorkDoneChangedEvent;
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
    public void testAddAndClear() throws NotificationContextException {
        for (NotificationContext victim : contexts) {
            victim.clearListeners();
            assertEquals(0, victim.size());
            testNotificationContextAddListener(victim);
            testNotificationContextClear(victim);
            testNotificationContextRemoveListener(victim);
        }
    }

    @Test
    public void testNotificationContextNotify() throws NotificationContextException {
        for (NotificationContext victim : contexts) {
            testNotificationContextNotify(victim);
        }
    }

    private void testNotificationContextNotify(NotificationContext victim) throws NotificationContextException {
        TestListenerPercentage listener = newPercentageListener();
        victim.addListener(listener);
        BigDecimal value = new BigDecimal("32");
        PercentageOfWorkDoneChangedEvent event = new PercentageOfWorkDoneChangedEvent(value,
                NotifiableTaskMetadata.NULL);
        assertFalse(event.isUndetermined());
        victim.notifyListeners(event);
        assertEquals(value, listener.getPercentage());
    }

    private void testNotificationContextAddListener(NotificationContext victim) throws NotificationContextException {
        victim.addListener(newStartListener());
        assertEquals(1, victim.size());
        victim.addListener(newPercentageListener());
        assertEquals(2, victim.size());
    }

    private void testNotificationContextRemoveListener(NotificationContext victim) throws NotificationContextException {
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
