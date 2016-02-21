/*
 * Created on 24/apr/2010
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
package org.sejda.core.notification.context;

import java.util.List;

import org.sejda.common.collection.ListValueMap;
import org.sejda.core.support.util.ReflectionUtils;
import org.sejda.model.exception.NotificationContextException;
import org.sejda.model.notification.EventListener;
import org.sejda.model.notification.event.AbstractNotificationEvent;

/**
 * Holds a list of listeners associated to the event class they are listening to.
 * 
 * @author Andrea Vacondio
 * 
 */
class SimpleEventListenerHoldingStrategy implements EventListenerHoldingStrategy {

    private final ListValueMap<Class<? extends AbstractNotificationEvent>, EventListener<? extends AbstractNotificationEvent>> listeners;

    SimpleEventListenerHoldingStrategy() {
        listeners = new ListValueMap<>();
    }

    @Override
    public <T extends AbstractNotificationEvent> void add(EventListener<T> listener) {
        Class<T> eventClass = getListenerEventClass(listener);
        listeners.put(eventClass, listener);
    }

    @Override
    public <T extends AbstractNotificationEvent> void add(Class<T> eventClass, EventListener<T> listener) {
        listeners.put(eventClass, listener);
    }

    @Override
    public <T extends AbstractNotificationEvent> boolean remove(EventListener<T> listener) {
        Class<T> eventClass = getListenerEventClass(listener);
        return listeners.remove(eventClass, listener);
    }

    private <T extends AbstractNotificationEvent> Class<T> getListenerEventClass(EventListener<T> listener) {
        @SuppressWarnings("unchecked")
        Class<T> eventClass = ReflectionUtils.inferParameterClass(listener.getClass(), "onEvent");
        if (eventClass == null) {
            throw new NotificationContextException("Unable to infer the listened event class.");
        }
        return eventClass;
    }

    @Override
    public void clear() {
        listeners.clear();
    }

    @Override
    public List<EventListener<? extends AbstractNotificationEvent>> get(AbstractNotificationEvent event) {
        return listeners.get(event.getClass());
    }

    @Override
    public int size() {
        return listeners.size();
    }

}
