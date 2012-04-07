/*
 * Created on 01/giu/2010
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
     * 
     * @param executionTime
     *            number of millis for the task to complete
     */
    void taskCompleted(long executionTime);

    /**
     * Notifies about a started task
     */
    void taskStarted();

    /**
     * Notifies about a task progress which is undetermined
     */
    void progressUndetermined();

    /**
     * Notifies about a certain amount of steps completed: <br />
     * <code>
     * notifyEvent().stepsCompleted(2).outOf(10);
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
     * notifyEvent().stepsCompleted(new BigDecimal("2").outOf(10);
     * </code>
     * 
     * @param completed
     *            number of steps completed
     * @return the {@link OngoingNotification} to perform the notification
     */
    OngoingNotification stepsCompleted(BigDecimal completed);
}
