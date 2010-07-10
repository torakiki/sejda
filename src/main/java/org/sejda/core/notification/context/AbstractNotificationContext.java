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
package org.sejda.core.notification.context;

import org.sejda.core.context.AbstractApplicationContext;
import org.sejda.core.exception.NotificationContextException;
import org.sejda.core.notification.EventListener;
import org.sejda.core.notification.event.AbstractEvent;
import org.sejda.core.notification.scope.EventListenerHoldingStrategy;
import org.sejda.core.notification.strategy.NotificationStrategy;
import org.sejda.core.notification.strategy.SyncNotificationStrategy;
import org.sejda.core.support.util.ReflectionUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract notification context implementing common context functionalities.
 * 
 * @author Andrea Vacondio
 */
@SuppressWarnings("unchecked")
public abstract class AbstractNotificationContext extends AbstractApplicationContext implements NotificationContext {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractNotificationContext.class);

    private final EventListenerHoldingStrategy holder;
    private final NotificationStrategy strategy;
    
    protected AbstractNotificationContext(EventListenerHoldingStrategy holder) {
        this.holder = holder;
        this.strategy = getStrategy();
    }

    public void notifyListeners(AbstractEvent event) {
        synchronized (holder) {
            if (holder.size() > 0) {
                for (EventListener listener : holder.get(event)) {
                    strategy.notifyListener(listener, event);
                }
            }
        }
    }

    public void addListener(EventListener<? extends AbstractEvent> listener) throws NotificationContextException {
        synchronized (holder) {
            Class eventClass = ReflectionUtility.inferParameterClass(listener.getClass(), "onEvent");
            if (eventClass == null) {
                throw new NotificationContextException("Unable to infer the listened event class.");
            }
            holder.add(eventClass, listener);
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
            return getNotificationStrategy().newInstance();
        } catch (InstantiationException e) {
            LOG
                    .warn(
                            "An error occur while instantiating a new NotificationStrategy. Default strategy will be used.",
                            e);
        } catch (IllegalAccessException e) {
            LOG
                    .warn(
                            "Unable to access constructor for the configured NotificationStrategy. Default strategy will be used.",
                            e);
        }
        return new SyncNotificationStrategy();
    }
}
