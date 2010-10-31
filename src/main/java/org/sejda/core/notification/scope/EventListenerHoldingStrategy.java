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
package org.sejda.core.notification.scope;

import java.util.List;

import org.sejda.core.notification.EventListener;
import org.sejda.core.notification.event.AbstractNotificationEvent;

/**
 * Strategy used to hold the list of registered listeners.
 * 
 * @author Andrea Vacondio
 * 
 */
public interface EventListenerHoldingStrategy {
    /**
     * Adds the input listeners on the input event
     * 
     * @param eventClass
     *            event class listened
     * @param listener
     */
    @SuppressWarnings("unchecked")
    void add(Class<? extends AbstractNotificationEvent> eventClass, EventListener listener);

    /**
     * Clears the list of listeners
     */
    void clear();

    /**
     * @param event
     * @return the list of listeners hold for the given {@link AbstractNotificationEvent}
     */
    @SuppressWarnings("unchecked")
    List<EventListener> get(AbstractNotificationEvent event);

    /**
     * @return number of hold listener
     */
    int size();
}
