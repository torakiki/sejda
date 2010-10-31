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

import org.sejda.core.context.AbstractApplicationContext;
import org.sejda.core.exception.NotificationContextException;
import org.sejda.core.notification.EventListener;
import org.sejda.core.notification.event.AbstractNotificationEvent;
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

    public void notifyListeners(AbstractNotificationEvent event) {
        synchronized (holder) {
            if (holder.size() > 0) {
                for (EventListener listener : holder.get(event)) {
                    strategy.notifyListener(listener, event);
                }
            }
        }
    }

    public void addListener(EventListener<? extends AbstractNotificationEvent> listener) throws NotificationContextException {
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
