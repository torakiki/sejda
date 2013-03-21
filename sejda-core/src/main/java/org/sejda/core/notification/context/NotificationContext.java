/*
 * Created on 17/apr/2010
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
 * 
 */
package org.sejda.core.notification.context;

import org.sejda.model.notification.EventListener;
import org.sejda.model.notification.event.AbstractNotificationEvent;

/**
 * Interface providing notification configuration. {@link EventListener} can be registered to be notified.
 * 
 * @author Andrea Vacondio
 * 
 */
public interface NotificationContext {

    /**
     * Adds the input listeners to the context.
     * 
     * @param <T>
     *            type of the event
     * @param listener
     * @throws org.sejda.model.exception.NotificationContextException
     *             if unable to infer the listened {@link AbstractNotificationEvent} subclass
     */
    <T extends AbstractNotificationEvent> void addListener(EventListener<T> listener);

    /**
     * Adds the input listeners to the context to listen on the input event class.
     * 
     * @param <T>
     *            type of the event
     * @param eventClass
     * @param listener
     */
    <T extends AbstractNotificationEvent> void addListener(Class<T> eventClass, EventListener<T> listener);

    /**
     * Remove the input listener from the context .
     * 
     * @param listener
     * @return true if the listener was found and removed, false otherwise
     * @throws org.sejda.model.exception.NotificationContextException
     *             if unable to infer the listened {@link AbstractNotificationEvent} subclass
     */
    <T extends AbstractNotificationEvent> boolean removeListener(EventListener<T> listener);

    /**
     * Clears the list of listeners for this context
     */
    void clearListeners();

    /**
     * Notifies the listeners about the input event
     * 
     * @param event
     */
    void notifyListeners(AbstractNotificationEvent event);

    /**
     * @return the number of the registered listeners
     */
    int size();

}
