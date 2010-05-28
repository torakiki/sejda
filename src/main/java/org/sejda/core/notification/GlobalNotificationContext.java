/*
 * Created on 18/apr/2010
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

import org.sejda.core.notification.scope.SimpleEventListenerHoldingStrategy;

/**
 * Notification context holding a list of listeners registered globally. Registered listeners on a particular event will
 * be notified about events of that type thrown globally in the current VM.
 * 
 * @author Andrea Vacondio
 */
public final class GlobalNotificationContext extends AbstractNotificationContext {

    private static GlobalNotificationContext instance = null;

    private GlobalNotificationContext() {
        super(new SimpleEventListenerHoldingStrategy());
    }

    public static synchronized GlobalNotificationContext getContext() {
        if (instance == null) {
            instance = new GlobalNotificationContext();
        }
        return instance;
    }
}
