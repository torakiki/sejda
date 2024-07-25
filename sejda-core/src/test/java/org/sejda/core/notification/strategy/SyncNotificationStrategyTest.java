/*
 * Created on 27/gen/2012
 * Copyright 2011 Sober Lemur S.r.l. and Sejda BV.
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
import org.sejda.model.notification.event.AbstractNotificationEvent;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Andrea Vacondio
 * 
 */
public class SyncNotificationStrategyTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testNotify() {
        AbstractNotificationEvent event = mock(AbstractNotificationEvent.class);
        SyncNotificationStrategy victim = new SyncNotificationStrategy();
        victim.notifyListener(null, event);
        EventListener<AbstractNotificationEvent> listener = mock(EventListener.class);
        victim.notifyListener(listener, event);
        verify(listener).onEvent(event);
    }
}
