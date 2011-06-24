/*
 * Created on 18/apr/2010
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
