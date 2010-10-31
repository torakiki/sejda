/*
 * Created on 12/mag/2010
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
package org.sejda.core.manipulation;

import org.sejda.core.context.AbstractApplicationContext;
import org.sejda.core.exception.TaskException;
import org.sejda.core.exception.TaskNotFoundException;
import org.sejda.core.manipulation.model.parameter.TaskParameters;
import org.sejda.core.manipulation.model.task.Task;

/**
 * Default implementation of the {@link TaskExecutionContext}
 * 
 * @author Andrea Vacondio
 * 
 */
public class DefaultTaskExecutionContext extends AbstractApplicationContext implements TaskExecutionContext {

    @SuppressWarnings("unchecked")
    public Task<? extends TaskParameters> getTask(TaskParameters parameters) throws TaskException {
        Class<? extends TaskParameters> parametersClass = parameters.getClass();
        Class<? extends Task> taskClass = getTasksRegistry().getTask(parametersClass);
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
