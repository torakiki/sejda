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
package org.sejda.core.manipulation.service;

import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang.time.StopWatch;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.core.exception.InvalidTaskParametersException;
import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.model.parameter.TaskParameters;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.validation.DefaultValidationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;

/**
 * Default implementation of the {@link TaskExecutionService}.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class DefaultTaskExecutionService implements TaskExecutionService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultTaskExecutionService.class);

    private TaskExecutionContext context = new DefaultTaskExecutionContext();
    private SejdaContext sejdaContext = new DefaultSejdaContext();

    public void execute(TaskParameters parameters) {
        StopWatch stopWatch = new StopWatch();
        preExecution(stopWatch);
        Task<? extends TaskParameters> task = null;
        try {
            validate(parameters);
            task = context.getTask(parameters);
            LOG.info("Starting task ({}) execution.", task);
            actualExecution(parameters, task);
            postExecution(stopWatch);
            LOG.info("Task ({}) executed in {}", task, DurationFormatUtils.formatDurationWords(stopWatch.getTime(),
                    true, true));
        } catch (InvalidTaskParametersException i) {
            LOG.warn("Task execution failed due to invalid parameters.", i);
            executionFailed(i);
        } catch (TaskException e) {
            LOG.warn(String.format("Task (%s) execution failed.", task), e);
            executionFailed(e);
        } catch (RuntimeException e) {
            executionFailed(e);
            throw e;
        }
    }

    /**
     * @param e
     */
    private void executionFailed(Exception e) {
        notifyEvent().taskFailed(e);
    }

    private void validate(TaskParameters parameters) throws InvalidTaskParametersException {
        if (sejdaContext.isValidation()) {
            LOG.debug("Validating parameters ({}).", parameters);
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
    private void preExecution(StopWatch stopWatch) {
        stopWatch.start();
        // notification of the starting task
        notifyEvent().taskStarted();
    }

    /**
     * operations needed after the actual execution
     */
    private void postExecution(StopWatch stopWatch) {
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
    @SuppressWarnings({ "rawtypes", "unchecked" })
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
