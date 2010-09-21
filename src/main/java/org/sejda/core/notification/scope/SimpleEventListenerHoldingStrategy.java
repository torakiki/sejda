/*
 * Created on 24/apr/2010
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
package org.sejda.core.notification.scope;

import java.util.List;

import org.sejda.core.notification.EventListener;
import org.sejda.core.notification.event.AbstractNotificationEvent;
import org.sejda.core.support.ListValueMap;

/**
 * Holds a list of listeners as a property.
 * 
 * @author Andrea Vacondio
 * 
 */
@SuppressWarnings("unchecked")
public class SimpleEventListenerHoldingStrategy implements EventListenerHoldingStrategy {

    private ListValueMap<Class<? extends AbstractNotificationEvent>, EventListener> listeners;

    public SimpleEventListenerHoldingStrategy() {
        listeners = new ListValueMap<Class<? extends AbstractNotificationEvent>, EventListener>();
    }

    public void add(Class<? extends AbstractNotificationEvent> eventClass, EventListener listener) {
        listeners.put(eventClass, listener);
    }

    public void clear() {
        listeners.clear();
    }

    public List<EventListener> get(AbstractNotificationEvent event) {
        return listeners.get(event.getClass());
    }

    public int size() {
        return listeners.size();
    }

}
