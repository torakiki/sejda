/*
 * Created on 01/giu/2010
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
package org.sejda.core.notification.dsl;

import java.math.BigDecimal;

/**
 * DSL interface to expose notification methods.
 * 
 * @author Andrea Vacondio
 * 
 */
public interface Notifier {

    /**
     * Notifies about a failed task
     * 
     * @param e
     *            failure exception
     */
    void taskFailed(Exception e);

    /**
     * Notifies about a completed task
     */
    void taskCompleted();

    /**
     * Notifies about a started task
     */
    void taskStarted();

    /**
     * Notifies about a certain amount of steps completed: <br />
     * <code>
     * notifyEvent().stepsCompleted(2).on(10);
     * </code>
     * 
     * @param completed
     *            number of steps completed
     * @return the {@link OngoingNotification} to perform the notification
     */
    OngoingNotification stepsCompleted(int completed);

    /**
     * Notifies about a certain amount of steps completed: <br />
     * <code>
     * notifyEvent().stepsCompleted(new BigDecimal("2").on(10);
     * </code>
     * 
     * @param completed
     *            number of steps completed
     * @return the {@link OngoingNotification} to perform the notification
     */
    OngoingNotification stepsCompleted(BigDecimal completed);
}
