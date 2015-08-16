/*
 * Created on 18/apr/2010
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
package org.sejda.core.context;

import org.sejda.core.notification.strategy.NotificationStrategy;
import org.sejda.model.exception.TaskException;
import org.sejda.model.parameter.base.TaskParameters;
import org.sejda.model.task.Task;

/**
 * Context where Sejda tasks are executed.
 * 
 * @author Andrea Vacondio
 * 
 */
public interface SejdaContext {

    /**
     * @return the configured strategy to use during listeners notification.
     */
    Class<? extends NotificationStrategy> getNotificationStrategy();

    /**
     * Search among the configured tasks the implementation that better can execute the input parameters. Returns an instance of the found class.
     * 
     * @param parameters
     *            Input parameters you want to search a task for.
     * @return the most suitable {@link Task} instance for the input parameters class.
     * @throws TaskException
     *             if no task able to execute the input parameters class is found, or if an error occur while reflective instantiating the {@link Task}.
     */
    Task<? extends TaskParameters> getTask(TaskParameters parameters) throws TaskException;

    /**
     * @return true if validation should be performed on parameters instance during the task execution or false if incoming parameters instances are already validate externally.
     */
    boolean isValidation();
}
