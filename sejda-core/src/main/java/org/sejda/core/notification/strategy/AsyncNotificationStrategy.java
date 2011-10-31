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
package org.sejda.core.notification.strategy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.sejda.model.notification.EventListener;
import org.sejda.model.notification.event.AbstractNotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Strategy to notify events asynchronously using a per thread single thread executor.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class AsyncNotificationStrategy implements NotificationStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(AsyncNotificationStrategy.class);

    /**
     * ThreadLocal returning a {@link ExecutorService} (the same instance for the same thread).
     */
    private static final ThreadLocal<ExecutorService> THREAD_LOCAL = new ThreadLocal<ExecutorService>() {
        @Override
        protected ExecutorService initialValue() {
            return Executors.newSingleThreadExecutor();
        }
    };

    @SuppressWarnings("rawtypes")
    public void notifyListener(final EventListener listener, final AbstractNotificationEvent event) {
        if (listener != null) {
            THREAD_LOCAL.get().execute(new NotifyRunnable(listener, event));
        }
    }

    /**
     * Runnable that can notify a listener of a given event.
     * 
     * @author Andrea Vacondio
     * 
     */
    @SuppressWarnings("rawtypes")
    private static final class NotifyRunnable implements Runnable {

        private final EventListener listener;
        private final AbstractNotificationEvent event;

        /**
         * @param listener
         * @param event
         */
        private NotifyRunnable(EventListener listener, AbstractNotificationEvent event) {
            this.listener = listener;
            this.event = event;
        }

        @SuppressWarnings("unchecked")
        public void run() {
            try {
                listener.onEvent(event);
            } catch (RuntimeException e) {
                LOG.error(String.format("An error occurred notifying event %s", event), e);
                throw e;
            }
        }
    }
}
