/*
 * Created on 27/gen/2012
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.sejda.model.notification.EventListener;
import org.sejda.model.notification.event.AbstractNotificationEvent;

/**
 * @author Andrea Vacondio
 * 
 */
public class SyncNotificationStrategyTest {

    private AbstractNotificationEvent event;

    @Before
    public void setUp() {
        event = mock(AbstractNotificationEvent.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testNotify() {
        SyncNotificationStrategy victim = new SyncNotificationStrategy();
        victim.notifyListener(null, event);
        EventListener listener = mock(EventListener.class);
        victim.notifyListener(listener, event);
        verify(listener).onEvent(event);
    }
}
