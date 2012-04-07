/*
 * Created on 29/mag/2010
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
package org.sejda.core.notification.dsl;

import java.math.BigDecimal;

import org.sejda.core.notification.context.GlobalNotificationContext;
import org.sejda.core.notification.context.ThreadLocalNotificationContext;
import org.sejda.model.notification.event.AbstractNotificationEvent;
import org.sejda.model.notification.event.PercentageOfWorkDoneChangedEvent;
import org.sejda.model.notification.event.TaskExecutionCompletedEvent;
import org.sejda.model.notification.event.TaskExecutionFailedEvent;
import org.sejda.model.notification.event.TaskExecutionStartedEvent;
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
     * Examples: <br />
     * <code>
     * NotifiableTaskMetadata taskMetadata = ...
     * notifyEvent(taskMetadata).stepsCompleted(2).outOf(10);
     * </code> <br />
     * <code>
     * notifyEvent(taskMetadata).taskCompleted();
     * </code>
     * </p>
     * 
     * @return the notifier
     */
    public static Notifier notifyEvent(NotifiableTaskMetadata taskMetadata) {
        return new ApplicationEventsNotifier(taskMetadata);
    }

    public void taskFailed(Exception e) {
        notifyListeners(new TaskExecutionFailedEvent(e, taskMetadata));
    }

    public void taskCompleted(long executionTime) {
        notifyListeners(new TaskExecutionCompletedEvent(executionTime, taskMetadata));
    }

    public void taskStarted() {
        notifyListeners(new TaskExecutionStartedEvent(taskMetadata));
    }

    public void progressUndetermined() {
        notifyListeners(new PercentageOfWorkDoneChangedEvent(PercentageOfWorkDoneChangedEvent.UNDETERMINED,
                taskMetadata));
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
                PercentageOfWorkDoneChangedEvent.MAX_PERGENTAGE).divide(total, BigDecimal.ROUND_HALF_DOWN),
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
