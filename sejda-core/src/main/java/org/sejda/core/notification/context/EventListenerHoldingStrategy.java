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

import org.sejda.model.notification.EventListener;
import org.sejda.model.notification.event.AbstractNotificationEvent;

/**
 * Strategy used to hold the list of registered listeners.
 * 
 * @author Andrea Vacondio
 * 
 */
interface EventListenerHoldingStrategy {
    /**
     * Adds the input {@link EventListener} to listen on the event type inferred from it's declaration.
     * 
     * @param <T>
     *            type of the event listened
     * @param listener
     * @throws org.sejda.model.exception.NotificationContextException
     *             if an error occurs inferring the type of the event
     */
    <T extends AbstractNotificationEvent> void add(EventListener<T> listener);

    /**
     * Adds the input {@link EventListener} to listen on the input event class.
     * 
     * @param <T>
     * @param eventClass
     *            event to listen for
     * @param listener
     */
    <T extends AbstractNotificationEvent> void add(Class<T> eventClass, EventListener<T> listener);

    /**
     * Removes the input listener from the input event.
     * 
     * @param <T>
     *            type of the event listened
     * @param listener
     * @return true if the listener was found and removed
     * @throws org.sejda.model.exception.NotificationContextException
     *             if an error occurs inferring the type of the event
     */
    <T extends AbstractNotificationEvent> boolean remove(EventListener<T> listener);

    /**
     * Clears the list of listeners
     */
    void clear();

    /**
     * @param event
     * @return the list of listeners held for the given {@link AbstractNotificationEvent}
     */
    List<EventListener<? extends AbstractNotificationEvent>> get(AbstractNotificationEvent event);

    /**
     * @return number of held listeners.
     */
    int size();
}
