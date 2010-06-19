/*
 * Created on 25/apr/2010
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
package org.sejda.core.notification;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sejda.core.exception.NotificationContextException;
import org.sejda.core.notification.context.GlobalNotificationContext;
import org.sejda.core.notification.context.NotificationContext;
import org.sejda.core.notification.context.ThreadLocalNotificationContext;
import org.sejda.core.notification.event.PercentageOfWorkDoneChangedEvent;

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
            testNotificationContextAddListener(victim);
            testNotificationContextClear(victim);
        }
    }

    @Test
    public void testNotificationContextNotify() throws NotificationContextException {
        for (NotificationContext victim : contexts) {
            testNotificationContextNotify(victim);
        }
    }

    private void testNotificationContextNotify(NotificationContext victim) throws NotificationContextException {
        TestListenerPercentage listener = new TestListenerPercentage();
        victim.addListener(listener);
        BigDecimal value = new BigDecimal("32");
        PercentageOfWorkDoneChangedEvent event = new PercentageOfWorkDoneChangedEvent(value);
        assertFalse(event.isUndetermined());
        victim.notifyListeners(event);
        assertEquals(value, listener.getPercentage());
    }

    private void testNotificationContextAddListener(NotificationContext victim) throws NotificationContextException {
        victim.addListener(new TestListenerPercentage());
        assertEquals(1, victim.size());
        victim.addListener(new ChildTestListenerPercentage());
        assertEquals(2, victim.size());
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
