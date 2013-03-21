/*
 * Created on 24/apr/2010
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
        listeners = new ListValueMap<Class<? extends AbstractNotificationEvent>, EventListener<? extends AbstractNotificationEvent>>();
    }

    public <T extends AbstractNotificationEvent> void add(EventListener<T> listener) {
        Class<T> eventClass = getListenerEventClass(listener);
        listeners.put(eventClass, listener);
    }

    public <T extends AbstractNotificationEvent> void add(Class<T> eventClass, EventListener<T> listener) {
        listeners.put(eventClass, listener);
    }

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

    public void clear() {
        listeners.clear();
    }

    public List<EventListener<? extends AbstractNotificationEvent>> get(AbstractNotificationEvent event) {
        return listeners.get(event.getClass());
    }

    public int size() {
        return listeners.size();
    }

}
