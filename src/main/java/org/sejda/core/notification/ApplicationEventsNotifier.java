/*
 * Created on 29/mag/2010
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
package org.sejda.core.notification;

import org.sejda.core.notification.context.GlobalNotificationContext;
import org.sejda.core.notification.context.ThreadLocalNotificationContext;
import org.sejda.core.notification.event.AbstractEvent;

/**
 * An helper class that can be used as a shortcut to notify all the global and local listeners about an event. All the listeners registered on the {@link GlobalNotificationContext}
 * and on the {@link ThreadLocalNotificationContext} will be notified.
 * 
 * @author Andrea Vacondio
 * @see org.sejda.core.notification.context.NotificationContext#notifyListeners(AbstractEvent)
 * 
 */
public class ApplicationEventsNotifier {

    /**
     * Notifies all the global and local listeners about the input event.
     * 
     * @param event
     */
    public void notifyListeners(AbstractEvent event) {
        GlobalNotificationContext.getContext().notifyListeners(event);
        ThreadLocalNotificationContext.getContext().notifyListeners(event);
    }

}
