/*
 * Created on 18/apr/2010
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
 * Notification context holding a list of listeners registered globally. Registered listeners on a particular event will be notified about events of that type thrown globally in
 * the current VM.
 * 
 * @author Andrea Vacondio
 */
public final class GlobalNotificationContext extends AbstractNotificationContext {

    private GlobalNotificationContext() {
        super(new SimpleEventListenerHoldingStrategy());
    }

    public static NotificationContext getContext() {
        return GlobalNotificationContextHolder.NOTIFICATION_CONTEXT;
    }

    /**
     * Lazy initialization holder class
     * 
     * @author Andrea Vacondio
     * 
     */
    private static final class GlobalNotificationContextHolder {

        private GlobalNotificationContextHolder() {
            // hide constructor
        }

        static final GlobalNotificationContext NOTIFICATION_CONTEXT = new GlobalNotificationContext();
    }
}
