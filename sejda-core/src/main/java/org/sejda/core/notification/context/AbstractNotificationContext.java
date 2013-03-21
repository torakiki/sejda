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

import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.notification.strategy.NotificationStrategy;
import org.sejda.core.notification.strategy.SyncNotificationStrategy;
import org.sejda.model.notification.EventListener;
import org.sejda.model.notification.event.AbstractNotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract notification context implementing common context functionalities.
 * 
 * @author Andrea Vacondio
 */
abstract class AbstractNotificationContext implements NotificationContext {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractNotificationContext.class);

    private final EventListenerHoldingStrategy holder;
    private final NotificationStrategy strategy;

    protected AbstractNotificationContext(EventListenerHoldingStrategy holder) {
        this.holder = holder;
        this.strategy = getStrategy();
    }

    public void notifyListeners(AbstractNotificationEvent event) {
        synchronized (holder) {
            if (holder.size() > 0) {
                for (EventListener<? extends AbstractNotificationEvent> listener : holder.get(event)) {
                    strategy.notifyListener(listener, event);
                }
            }
        }
    }

    public <T extends AbstractNotificationEvent> void addListener(EventListener<T> listener) {
        synchronized (holder) {
            LOG.trace("Adding event listener: {}", listener);
            holder.add(listener);
        }
    }

    public <T extends AbstractNotificationEvent> void addListener(Class<T> eventClass, EventListener<T> listener) {
        synchronized (holder) {
            LOG.trace("Adding event listener {} on event {}", listener, eventClass);
            holder.add(eventClass, listener);
        }
    }

    public <T extends AbstractNotificationEvent> boolean removeListener(EventListener<T> listener) {
        synchronized (holder) {
            LOG.trace("Removing event listener: {}", listener);
            return holder.remove(listener);
        }
    }

    public void clearListeners() {
        synchronized (holder) {
            holder.clear();
        }
    }

    public int size() {
        return holder.size();
    }

    /**
     * @return a new instance of the configured notification strategy
     */
    private NotificationStrategy getStrategy() {
        try {
            return new DefaultSejdaContext().getNotificationStrategy().newInstance();
        } catch (InstantiationException e) {
            LOG.warn("An error occur while instantiating a new NotificationStrategy. Default strategy will be used.", e);
        } catch (IllegalAccessException e) {
            LOG.warn(
                    "Unable to access constructor for the configured NotificationStrategy. Default strategy will be used.",
                    e);
        }
        return new SyncNotificationStrategy();
    }
}
