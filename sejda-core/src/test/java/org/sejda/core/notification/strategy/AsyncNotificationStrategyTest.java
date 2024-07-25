/*
 * Created on 02/mag/2010
 *
 * Copyright 2010 Sober Lemur S.r.l. and Sejda BV.
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

import org.junit.jupiter.api.Test;
import org.sejda.model.notification.EventListener;
import org.sejda.model.notification.event.TaskExecutionStartedEvent;
import org.sejda.model.task.NotifiableTaskMetadata;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

/**
 * Test unit for {@link AsyncNotificationStrategy}
 *
 * @author Andrea Vacondio
 */
public class AsyncNotificationStrategyTest {

    private AsyncNotificationStrategy victim = new AsyncNotificationStrategy();

    @Test
    public void testNotifyEvent() {
        EventListener<TaskExecutionStartedEvent> listener = mock(EventListener.class);
        victim.notifyListener(listener, new TaskExecutionStartedEvent(NotifiableTaskMetadata.NULL));
        verify(listener, timeout(100)).onEvent(any());
    }
}
