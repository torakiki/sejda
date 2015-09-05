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

    @Override
    public void notifyListeners(AbstractNotificationEvent event) {
        synchronized (holder) {
            if (holder.size() > 0) {
                for (EventListener<? extends AbstractNotificationEvent> listener : holder.get(event)) {
                    strategy.notifyListener(listener, event);
                }
            }
        }
    }

    @Override
    public <T extends AbstractNotificationEvent> void addListener(EventListener<T> listener) {
        synchronized (holder) {
            LOG.trace("Adding event listener: {}", listener);
            holder.add(listener);
        }
    }

    @Override
    public <T extends AbstractNotificationEvent> void addListener(Class<T> eventClass, EventListener<T> listener) {
        synchronized (holder) {
            LOG.trace("Adding event listener {} on event {}", listener, eventClass);
            holder.add(eventClass, listener);
        }
    }

    @Override
    public <T extends AbstractNotificationEvent> boolean removeListener(EventListener<T> listener) {
        synchronized (holder) {
            LOG.trace("Removing event listener: {}", listener);
            return holder.remove(listener);
        }
    }

    @Override
    public void clearListeners() {
        synchronized (holder) {
            holder.clear();
        }
    }

    @Override
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
