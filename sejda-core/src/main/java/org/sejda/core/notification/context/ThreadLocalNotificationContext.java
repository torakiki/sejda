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

/**
 * Local notification context factory. Contains methods to return the {@link NotificationContext} binded to the local thread. Registered listeners on a particular event will be
 * notified about events of that type thrown by tasks executed by the current thread.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class ThreadLocalNotificationContext {

    private ThreadLocalNotificationContext() {
        // hide
    }

    private static final ThreadLocal<? extends AbstractNotificationContext> THREAD_LOCAL_CONTEXT = new ThreadLocal<SimpleNotificationContext>() {
        @Override
        protected SimpleNotificationContext initialValue() {
            return new ThreadLocalNotificationContext.SimpleNotificationContext();
        }
    };

    public static NotificationContext getContext() {
        return THREAD_LOCAL_CONTEXT.get();
    }

    /**
     * Simple notification context holding a list of listeners as instance attribute.
     * 
     * @author Andrea Vacondio
     * 
     */
    private static class SimpleNotificationContext extends AbstractNotificationContext {

        SimpleNotificationContext() {
            super(new SimpleEventListenerHoldingStrategy());
        }

    }
}
