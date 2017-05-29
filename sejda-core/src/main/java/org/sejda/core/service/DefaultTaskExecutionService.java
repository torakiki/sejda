/*
 * Created on 28/mag/2010
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
package org.sejda.core.service;

import static java.util.Optional.ofNullable;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.core.validation.DefaultValidationContext;
import org.sejda.model.exception.InvalidTaskParametersException;
import org.sejda.model.exception.TaskException;
import org.sejda.model.parameter.base.TaskParameters;
import org.sejda.model.task.CancellationOption;
import org.sejda.model.task.NotifiableTaskMetadata;
import org.sejda.model.task.TaskExecutionContext;
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

    @Override
    public void execute(TaskParameters parameters) {
        execute(parameters, new CancellationOption());
    }

    @Override
    public void execute(TaskParameters parameters, CancellationOption cancellationOption) {
        TaskExecutionContext executionContext = null;
        LOG.trace("Starting execution for {}", parameters);
        try {
            validateIfRequired(parameters);
            executionContext = new TaskExecutionContext(context.getTask(parameters), parameters.isLenient());
            cancellationOption.setExecutionContext(executionContext);
            LOG.info("Starting task ({}) execution.", executionContext.task());
            preExecution(executionContext);
            actualExecution(parameters, executionContext);
            postExecution(executionContext);
        } catch (InvalidTaskParametersException i) {
            LOG.error("Task execution failed due to invalid parameters: " + String.join(". ", i.getReasons()), i);
            executionFailed(i, executionContext);
        } catch (TaskException e) {
            LOG.error(String.format("Task (%s) execution failed.",
                    ofNullable(executionContext).map(c -> c.task().toString()).orElse("")), e);
            executionFailed(e, executionContext);
        } catch (RuntimeException e) {
            executionFailed(e, executionContext);
            throw e;
        }
    }

    private void executionFailed(Exception e, TaskExecutionContext executionContext) {
        if (executionContext == null) {
            notifyEvent(NotifiableTaskMetadata.NULL).taskFailed(e);
        } else {
            notifyEvent(executionContext.notifiableTaskMetadata()).taskFailed(e);
        }
    }

    private void validateIfRequired(TaskParameters parameters) throws InvalidTaskParametersException {
        if (context.isValidation()) {
            LOG.debug("Validating parameters.");
            validate(parameters);
        } else {
            LOG.info("Validation skipped.");
        }
    }

    public void validate(TaskParameters parameters) throws InvalidTaskParametersException {
        Set<ConstraintViolation<TaskParameters>> violations = DefaultValidationContext.getContext().getValidator()
                .validate(parameters);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder(
                    String.format("Input parameters (%s) are not valid: ", parameters));

            List<String> reasons = new ArrayList<>();
            for (ConstraintViolation<TaskParameters> violation : violations) {
                sb.append(String.format("\"(%s=%s) %s\" ", violation.getPropertyPath(), violation.getInvalidValue(),
                        violation.getMessage()));
                reasons.add(violation.getMessage());
            }
            throw new InvalidTaskParametersException(sb.toString(), reasons);
        }
    }

    /**
     * operations needed before the actual execution
     */
    private void preExecution(TaskExecutionContext context) {
        context.taskStart();
        notifyEvent(context.notifiableTaskMetadata()).taskStarted();
    }

    /**
     * operations needed after the actual execution
     */
    private void postExecution(TaskExecutionContext context) {
        context.taskEnded();
        notifyEvent(context.notifiableTaskMetadata()).taskCompleted(context.executionTime());
    }

    /**
     * actual execution of the task
     * 
     * @param parameters
     * @param task
     * @throws TaskException
     */
    @SuppressWarnings("unchecked")
    private void actualExecution(TaskParameters parameters, TaskExecutionContext executionContext)
            throws TaskException {
        try {
            executionContext.task().before(parameters, executionContext);
            executionContext.task().execute(parameters);
        } finally {
            try {
                executionContext.task().after();
            } catch (RuntimeException e) {
                LOG.warn("An unexpected error occurred during the execution of the 'after' phase.", e);
            }
        }
    }
}
