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
package org.sejda.core.notification;


/**
 * Local notification context factory. Contains methods to return the {@link NotificationContext} binded to the local thread. Registered listeners on a particular event will be
 * notified about events of that type thrown by tasks executed by the current thread.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class ThreadLocalNotificationContextFactory {

    private ThreadLocalNotificationContextFactory() {
        // hide
    }

    private static ThreadLocal<? extends AbstractNotificationContext> threadLocal = new ThreadLocal<DefaultNotificationContext>() {
        protected DefaultNotificationContext initialValue() {
            return new DefaultNotificationContext();
        }
    };

    public static NotificationContext getLocalNotificationContext() {
        return threadLocal.get();
    }

}
