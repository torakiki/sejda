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
