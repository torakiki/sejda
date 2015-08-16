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
