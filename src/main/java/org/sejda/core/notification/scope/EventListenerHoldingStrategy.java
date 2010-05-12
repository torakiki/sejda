/*
 * Created on 24/apr/2010
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
package org.sejda.core.notification.scope;

import java.util.List;

import org.sejda.core.notification.EventListener;
import org.sejda.core.notification.event.AbstractEvent;

/**
 * Strategy used to hold the list of registered listeners.
 * 
 * @author Andrea Vacondio
 * 
 */
public interface EventListenerHoldingStrategy {
    /**
     * Adds the input listeners on the input event
     * 
     * @param eventClass
     *            event class listened
     * @param listener
     */
    @SuppressWarnings("unchecked")
    void add(Class<? extends AbstractEvent> eventClass, EventListener listener);

    /**
     * Clears the list of listeners
     */
    void clear();

    /**
     * @param event
     * @return the list of listeners hold for the given {@link AbstractEvent}
     */
    @SuppressWarnings("unchecked")
    List<EventListener> get(AbstractEvent event);

    /**
     * @return number of hold listener
     */
    int size();
}
