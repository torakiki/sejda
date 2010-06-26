/*
 * Created on 28/mag/2010
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
package org.sejda.core.manipulation.service;

import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;

import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang.time.StopWatch;
import org.sejda.core.exception.InvalidTaskParametersException;
import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.DefaultTaskExecutionContext;
import org.sejda.core.manipulation.TaskExecutionContext;
import org.sejda.core.manipulation.model.parameter.TaskParameters;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.validation.DefaultValidationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of the {@link TaskExecutionService}.
 * <p>
 * This implementation is not synchronized and unpredictable behavior can be experienced if multiple threads try to execute different tasks on the same service instance.
 * </p>
 * 
 * @author Andrea Vacondio
 * 
 */
public class DefaultTaskExecutionService implements TaskExecutionService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultTaskExecutionService.class);

    private StopWatch stopWatch = new StopWatch();
    private TaskExecutionContext context = new DefaultTaskExecutionContext();

    @SuppressWarnings("unchecked")
    public void execute(TaskParameters parameters) {
        preExecution();
        Task task = null;
        try {
            validate(parameters);
            task = context.getTask(parameters);
            LOG.debug(String.format("Starting task (%s) execution.", task));
            actualExecution(parameters, task);
            postExecution();
            LOG.debug(String.format("Task (%s) executed in %s", task, DurationFormatUtils.formatDurationWords(stopWatch
                    .getTime(), true, true)));
        }catch(InvalidTaskParametersException i){
            LOG.info("Task execution failed due to invalid parameters.", i);
            executionFailed(i);
        }
        catch (TaskException e) {
            LOG.info(String.format("Task (%s) execution failed.", task), e);
            executionFailed(e);
        }
    }

    /**
     * @param e
     */
    private void executionFailed(TaskException e) {
        stopWatch.stop();
        notifyEvent().taskFailed(e);
    }

    private void validate(TaskParameters parameters) throws InvalidTaskParametersException {
        if (DefaultValidationContext.getContext().isValidation()) {
            LOG.debug(String.format("Validating parameters (%s).", parameters));
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
            LOG.debug("Validating skipped.");
        }
    }

    /**
     * operations needed before the actual execution
     */
    private void preExecution() {
        stopWatch.reset();
        stopWatch.start();
        // notification of the starting task
        notifyEvent().taskStarted();
    }

    /**
     * operations needed after the actual execution
     */
    private void postExecution() {
        stopWatch.stop();
        // notification about completion
        notifyEvent().taskCompleted();
    }

    /**
     * actual execution of the task
     * 
     * @param parameters
     * @param task
     * @throws TaskException
     */
    @SuppressWarnings("unchecked")
    private void actualExecution(TaskParameters parameters, Task task) throws TaskException {
        try {
            task.before(parameters);
            task.execute(parameters);
        } finally {
            task.after();
        }
    }

    // Test purpose
    /**
     * @param context
     *            the context to set
     */
    void setContext(TaskExecutionContext context) {
        this.context = context;
    }

}
