/*
 * Created on 12/mag/2010
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

import org.sejda.core.configuration.GlobalConfiguration;
import org.sejda.core.context.AbstractApplicationContext;
import org.sejda.core.exception.TaskException;
import org.sejda.core.exception.TaskNotFoundException;
import org.sejda.core.manipulation.model.parameter.TaskParameters;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.manipulation.registry.TasksRegistry;

/**
 * Default implementation of the {@link TaskExecutionContext}
 * 
 * @author Andrea Vacondio
 * 
 */
public class DefaultTaskExecutionContext extends AbstractApplicationContext implements TaskExecutionContext {

    private TasksRegistry registry = GlobalConfiguration.getInstance().getTaskRegistry();

    @SuppressWarnings("unchecked")
    public Task<? extends TaskParameters> getTask(TaskParameters parameters) throws TaskException {
        Class<? extends TaskParameters> parametersClass = parameters.getClass();
        Class<? extends Task> taskClass = registry.getTask(parametersClass);
        if (taskClass == null) {
            throw new TaskNotFoundException(String.format("Unable to find a Task class able to execute %s",
                    parametersClass));
        }
        try {
            return taskClass.newInstance();
        } catch (InstantiationException e) {
            throw new TaskException("Error instantiating the task", e);
        } catch (IllegalAccessException e) {
            throw new TaskException("Error instantiating the task", e);
        }
    }

}
