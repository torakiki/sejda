/*
 * Created on 28/mag/2010
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
package org.sejda.core.service;

import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;

import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.core.validation.DefaultValidationContext;
import org.sejda.model.exception.InvalidTaskParametersException;
import org.sejda.model.exception.TaskException;
import org.sejda.model.parameter.base.TaskParameters;
import org.sejda.model.task.NotifiableTaskMetadata;
import org.sejda.model.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of the {@link TaskExecutionService}.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class DefaultTaskExecutionService implements TaskExecutionService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultTaskExecutionService.class);

    private final SejdaContext context = new DefaultSejdaContext();

    public void execute(TaskParameters parameters) {
        StopWatch stopWatch = new StopWatch();
        Task<? extends TaskParameters> task = null;
        LOG.trace("Starting exectution for {}", parameters);
        try {
            validate(parameters);
            task = context.getTask(parameters);
            LOG.info("Starting task ({}) execution.", task);
            preExecution(task, stopWatch);
            actualExecution(parameters, task);
            postExecution(task, stopWatch);
            LOG.info("Task ({}) executed in {}", task,
                    DurationFormatUtils.formatDurationWords(stopWatch.getTime(), true, true));
        } catch (InvalidTaskParametersException i) {
            LOG.error("Task execution failed due to invalid parameters.", i);
            executionFailed(i, task);
        } catch (TaskException e) {
            LOG.error(String.format("Task (%s) execution failed.", task), e);
            executionFailed(e, task);
        } catch (RuntimeException e) {
            executionFailed(e, task);
            throw e;
        }
    }

    private void executionFailed(Exception e, Task<?> task) {
        if (task == null) {
            notifyEvent(NotifiableTaskMetadata.NULL).taskFailed(e);
        } else {
            notifyEvent(task.getNotifiableTaskMetadata()).taskFailed(e);
        }
    }

    private void validate(TaskParameters parameters) throws InvalidTaskParametersException {
        if (context.isValidation()) {
            LOG.debug("Validating parameters.");
            Set<ConstraintViolation<TaskParameters>> violations = DefaultValidationContext.getContext().getValidator()
                    .validate(parameters);
            if (!violations.isEmpty()) {
                StringBuilder sb = new StringBuilder(String.format("Input parameters (%s) are not valid: ", parameters));
                for (ConstraintViolation<TaskParameters> violation : violations) {
                    sb.append(String.format("\"(%s=%s) %s\" ", violation.getPropertyPath(),
                            violation.getInvalidValue(), violation.getMessage()));
                }
                throw new InvalidTaskParametersException(sb.toString());
            }
        } else {
            LOG.info("Validation skipped.");
        }
    }

    /**
     * operations needed before the actual execution
     */
    private void preExecution(Task<?> task, StopWatch stopWatch) {
        stopWatch.start();
        notifyEvent(task.getNotifiableTaskMetadata()).taskStarted();
    }

    /**
     * operations needed after the actual execution
     */
    private void postExecution(Task<?> task, StopWatch stopWatch) {
        stopWatch.stop();
        notifyEvent(task.getNotifiableTaskMetadata()).taskCompleted(stopWatch.getTime());
    }

    /**
     * actual execution of the task
     * 
     * @param parameters
     * @param task
     * @throws TaskException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void actualExecution(TaskParameters parameters, Task task) throws TaskException {
        try {
            task.before(parameters);
            task.execute(parameters);
        } finally {
            try {
                task.after();
            } catch (RuntimeException e) {
                LOG.warn("An unexpected error occurred during the execution of the 'after' phase.", e);
            }
        }
    }
}
