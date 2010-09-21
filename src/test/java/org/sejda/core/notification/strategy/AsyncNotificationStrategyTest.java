/*
 * Created on 02/mag/2010
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
package org.sejda.core.notification.strategy;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.Matchers;
import org.sejda.core.notification.EventListener;
import org.sejda.core.notification.event.AbstractNotificationEvent;
import org.sejda.core.notification.event.TaskExecutionCompletedEvent;

/**
 * Test unit for {@link AsyncNotificationStrategy}
 * 
 * @author Andrea Vacondio
 * 
 */
public class AsyncNotificationStrategyTest {

    private AsyncNotificationStrategy victim = new AsyncNotificationStrategy();

    @Test
    @SuppressWarnings("unchecked")
    public void testNotifyEvent() throws InterruptedException {
        EventListener listener = mock(EventListener.class);
        victim.notifyListener(listener, new TaskExecutionCompletedEvent());
        // ugly but needed to give time for the async notification
        Thread.sleep(1000);
        verify(listener, times(1)).onEvent(Matchers.any(AbstractNotificationEvent.class));
    }
}
