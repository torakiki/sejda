/*
 * Created on 27/apr/2010
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
package org.sejda.core.manipulation;

import org.sejda.core.context.ApplicationContext;
import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.model.parameter.TaskParameters;
import org.sejda.core.manipulation.model.task.Task;

/**
 * Interface providing configuration for the tasks executions.
 * 
 * @author Andrea Vacondio
 * 
 */
public interface TaskExecutionContext extends ApplicationContext {

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

}
