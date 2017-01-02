/*
 * Created on 10 giu 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.model.task;

import static java.util.Objects.isNull;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.sejda.model.exception.TaskCancelledException;
import org.sejda.model.exception.TaskNonLenientExecutionException;
import org.sejda.model.parameter.base.TaskParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mutable context holding a task execution
 * 
 * @author Andrea Vacondio
 */
public class TaskExecutionContext {
    private static final Logger LOG = LoggerFactory.getLogger(TaskExecutionContext.class);

    private StopWatch stopWatch = new StopWatch();
    private NotifiableTaskMetadata taskMetadata;
    private boolean cancelled = false;
    private Task<? extends TaskParameters> task;
    private boolean lenient;
    private int outputDocumentsCounter = 0;

    public TaskExecutionContext(Task<? extends TaskParameters> task, boolean lenient) {
        if (isNull(task)) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        this.taskMetadata = new NotifiableTaskMetadata(task);
        this.task = task;
        this.lenient = lenient;
    }

    public NotifiableTaskMetadata notifiableTaskMetadata() {
        return taskMetadata;
    }

    public void cancelTask() {
        this.cancelled = true;
    }

    public void assertTaskNotCancelled() throws TaskCancelledException {
        if (cancelled)
            throw new TaskCancelledException();
    }

    @SuppressWarnings("rawtypes")
    public Task task() {
        return task;
    }

    public void taskStart() {
        stopWatch.start();
    }

    public void taskEnded() {
        stopWatch.stop();
        LOG.info("Task ({}) executed in {}", task,
                DurationFormatUtils.formatDurationWords(stopWatch.getTime(), true, true));
    }

    public long executionTime() {
        return stopWatch.getTime();
    }

    public int incrementAndGetOutputDocumentsCounter() {
        return ++outputDocumentsCounter;
    }

    /**
     * 
     * @param e
     *            the exception the lenient task can recover from
     * @throws TaskNonLenientExecutionException
     */
    public void assertTaskIsLenient(Exception e) throws TaskNonLenientExecutionException {
        if (!lenient) {
            throw new TaskNonLenientExecutionException(e);
        }
    }
}
