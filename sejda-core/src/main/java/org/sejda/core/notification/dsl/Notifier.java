/*
 * Created on 01/giu/2010
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
     * Notifies about a task
     * 
     * @param warning
     *            warning warning message
     */
    void taskWarning(String warning);

    /**
     * Notifies about a task warning
     * 
     * @param warning
     *            warning message
     * @param e
     *            exception causing the warning
     */
    void taskWarning(String warning, Exception e);

    /**
     * Notifies about a task progress which is undetermined
     */
    void progressUndetermined();

    /**
     * Notifies about a certain amount of steps completed: <code>
     * notifyEvent().stepsCompleted(2).outOf(10);
     * </code>
     * 
     * @param completed
     *            number of steps completed
     * @return the {@link OngoingNotification} to perform the notification
     */
    OngoingNotification stepsCompleted(int completed);

    /**
     * Notifies about a certain amount of steps completed: <code>
     * notifyEvent().stepsCompleted(new BigDecimal("2").outOf(10);
     * </code>
     * 
     * @param completed
     *            number of steps completed
     * @return the {@link OngoingNotification} to perform the notification
     */
    OngoingNotification stepsCompleted(BigDecimal completed);
}
