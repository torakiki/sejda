/*
 * Created on 29/mag/2010
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

import org.sejda.core.notification.context.GlobalNotificationContext;
import org.sejda.core.notification.context.ThreadLocalNotificationContext;
import org.sejda.core.notification.event.AbstractNotificationEvent;
import org.sejda.core.notification.event.PercentageOfWorkDoneChangedEvent;
import org.sejda.core.notification.event.TaskExecutionCompletedEvent;
import org.sejda.core.notification.event.TaskExecutionFailedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An DSL class that can be used to notify all the global and local listeners about an event. All the listeners registered on the {@link GlobalNotificationContext} and on the
 * {@link ThreadLocalNotificationContext} will be notified.
 * 
 * @author Andrea Vacondio
 * @see org.sejda.core.notification.context.NotificationContext#notifyListeners(AbstractNotificationEvent)
 * 
 */
public final class ApplicationEventsNotifier implements Notifier, OngoingNotification {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationEventsNotifier.class);

    private BigDecimal percentage = BigDecimal.ZERO;

    private ApplicationEventsNotifier() {
        // on purpose
    }

    /**
     * Entry point to create a notification using DSL.
     * <p>
     * Examples: <br />
     * <code>
     * notifyEvent().stepsCompleted(2).on(10);
     * </code> <br />
     * <code>
     * notifyEvent().taskCompleted();
     * </code>
     * </p>
     * 
     * @return the notifier
     */
    public static Notifier notifyEvent() {
        return new ApplicationEventsNotifier();
    }

    public void taskFailed(Exception e) {
        notifyListeners(new TaskExecutionFailedEvent(e));
    }

    public void taskCompleted() {
        notifyListeners(new TaskExecutionCompletedEvent());
    }

    public void taskStarted() {
        notifyListeners(new PercentageOfWorkDoneChangedEvent());
    }

    public OngoingNotification stepsCompleted(int completed) {
        this.percentage = new BigDecimal(completed);
        return this;
    }

    public OngoingNotification stepsCompleted(BigDecimal completed) {
        this.percentage = completed;
        return this;
    }

    public void outOf(int total) {
        outOf(new BigDecimal(total));
    }

    public void outOf(BigDecimal total) {
        notifyListeners(new PercentageOfWorkDoneChangedEvent(percentage.multiply(
                PercentageOfWorkDoneChangedEvent.MAX_PERGENTAGE).divide(total)));

    }

    /**
     * Notifies all the global and local listeners about the input event.
     * 
     * @param event
     */
    private void notifyListeners(AbstractNotificationEvent event) {
        LOG.debug("Notifing event {}", event);
        GlobalNotificationContext.getContext().notifyListeners(event);
        ThreadLocalNotificationContext.getContext().notifyListeners(event);
    }

}
