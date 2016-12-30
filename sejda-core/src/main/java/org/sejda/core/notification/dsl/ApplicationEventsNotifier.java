/*
 * Created on 29/mag/2010
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
package org.sejda.core.notification.dsl;

import java.math.BigDecimal;

import org.sejda.core.notification.context.GlobalNotificationContext;
import org.sejda.core.notification.context.ThreadLocalNotificationContext;
import org.sejda.model.notification.event.AbstractNotificationEvent;
import org.sejda.model.notification.event.PercentageOfWorkDoneChangedEvent;
import org.sejda.model.notification.event.TaskExecutionCompletedEvent;
import org.sejda.model.notification.event.TaskExecutionFailedEvent;
import org.sejda.model.notification.event.TaskExecutionStartedEvent;
import org.sejda.model.notification.event.TaskExecutionWarningEvent;
import org.sejda.model.task.NotifiableTaskMetadata;
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
    private NotifiableTaskMetadata taskMetadata;

    private ApplicationEventsNotifier(NotifiableTaskMetadata taskMetadata) {
        this.taskMetadata = taskMetadata;
    }

    /**
     * Entry point to create a notification using DSL.
     * <p>
     * Examples: <code>
     * NotifiableTaskMetadata taskMetadata = ...
     * notifyEvent(taskMetadata).stepsCompleted(2).outOf(10);
     * </code> <code>
     * notifyEvent(taskMetadata).taskCompleted();
     * </code>
     * </p>
     * 
     * @return the notifier
     */
    public static Notifier notifyEvent(NotifiableTaskMetadata taskMetadata) {
        return new ApplicationEventsNotifier(taskMetadata);
    }

    @Override
    public void taskFailed(Exception e) {
        notifyListeners(new TaskExecutionFailedEvent(e, taskMetadata));
    }

    @Override
    public void taskCompleted(long executionTime) {
        notifyListeners(new TaskExecutionCompletedEvent(executionTime, taskMetadata));
    }

    @Override
    public void taskStarted() {
        notifyListeners(new TaskExecutionStartedEvent(taskMetadata));
    }

    @Override
    public void taskWarning(String warning) {
        LOG.warn(warning);
        notifyListeners(new TaskExecutionWarningEvent(warning, taskMetadata));
    }

    @Override
    public void taskWarning(String warning, Exception e) {
        LOG.warn(warning, e);
        notifyListeners(new TaskExecutionWarningEvent(warning, taskMetadata));
    }

    @Override
    public void progressUndetermined() {
        notifyListeners(
                new PercentageOfWorkDoneChangedEvent(PercentageOfWorkDoneChangedEvent.UNDETERMINED, taskMetadata));
    }

    @Override
    public OngoingNotification stepsCompleted(int completed) {
        this.percentage = new BigDecimal(completed);
        return this;
    }

    @Override
    public OngoingNotification stepsCompleted(BigDecimal completed) {
        this.percentage = completed;
        return this;
    }

    @Override
    public void outOf(int total) {
        outOf(new BigDecimal(total));
    }

    @Override
    public void outOf(BigDecimal total) {
        notifyListeners(new PercentageOfWorkDoneChangedEvent(percentage
                .multiply(PercentageOfWorkDoneChangedEvent.MAX_PERGENTAGE).divide(total, BigDecimal.ROUND_HALF_DOWN),
                taskMetadata));

    }

    /**
     * Notifies all the global and local listeners about the input event.
     * 
     * @param event
     */
    private void notifyListeners(AbstractNotificationEvent event) {
        LOG.trace("Notifing event {}", event);
        GlobalNotificationContext.getContext().notifyListeners(event);
        ThreadLocalNotificationContext.getContext().notifyListeners(event);
    }

}
