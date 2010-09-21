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

import org.sejda.core.context.ApplicationContext;
import org.sejda.core.exception.NotificationContextException;
import org.sejda.core.notification.EventListener;
import org.sejda.core.notification.event.AbstractNotificationEvent;

/**
 * Interface providing notification configuration. {@link EventListener} can be registered to be notified.
 * 
 * @author Andrea Vacondio
 * 
 */
public interface NotificationContext extends ApplicationContext {

    /**
     * Adds the input listeners to the context
     * 
     * @param listener
     * @throws NotificationContextException
     *             if unable to infer the listened {@link AbstractNotificationEvent} subclass
     */
    void addListener(EventListener<? extends AbstractNotificationEvent> listener) throws NotificationContextException;

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
