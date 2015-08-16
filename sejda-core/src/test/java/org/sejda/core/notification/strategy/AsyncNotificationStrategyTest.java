/*
 * Created on 02/mag/2010
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
package org.sejda.core.notification.strategy;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.Matchers;
import org.sejda.model.notification.EventListener;
import org.sejda.model.notification.event.AbstractNotificationEvent;
import org.sejda.model.notification.event.TaskExecutionCompletedEvent;
import org.sejda.model.task.NotifiableTaskMetadata;

/**
 * Test unit for {@link AsyncNotificationStrategy}
 * 
 * @author Andrea Vacondio
 * 
 */
public class AsyncNotificationStrategyTest {

    private AsyncNotificationStrategy victim = new AsyncNotificationStrategy();

    @Test
    @SuppressWarnings("rawtypes")
    public void testNotifyEvent() throws InterruptedException {
        EventListener listener = mock(EventListener.class);
        victim.notifyListener(listener, new TaskExecutionCompletedEvent(1L, NotifiableTaskMetadata.NULL));
        // FIXME
        // ugly but needed to give time for the async notification
        Thread.sleep(1000);
        verify(listener, times(1)).onEvent(Matchers.any(AbstractNotificationEvent.class));
    }
}
