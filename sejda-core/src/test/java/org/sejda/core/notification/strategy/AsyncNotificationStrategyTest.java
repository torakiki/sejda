/*
 * Created on 02/mag/2010
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
package org.sejda.core.notification.strategy;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.Matchers;
import org.sejda.core.manipulation.model.task.NotifiableTaskMetadata;
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
