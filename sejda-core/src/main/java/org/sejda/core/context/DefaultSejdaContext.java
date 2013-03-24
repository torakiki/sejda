/*
 * Created on 18/apr/2010
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
package org.sejda.core.context;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.sejda.core.notification.strategy.NotificationStrategy;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskNotFoundException;
import org.sejda.model.parameter.base.TaskParameters;
import org.sejda.model.task.Task;

/**
 * Default implementation of the {@link SejdaContext}. It loads Sejda configuration from the xml configuration file specified by the system property <b>sejda.config.file</b> or
 * from the expected <b>sejda.xml</b> in the classpath.
 * 
 * @author Andrea Vacondio
 * 
 */
public class DefaultSejdaContext implements SejdaContext {

    private static final String ERROR_INSTANTIATING_THE_TASK = "Error instantiating the task";

    public final Class<? extends NotificationStrategy> getNotificationStrategy() {
        return GlobalConfiguration.getInstance().getNotificationStrategy();
    }

    public boolean isValidation() {
        return GlobalConfiguration.getInstance().isValidation();
    }

    public boolean isIgnoreXmlConfiguration() {
        return GlobalConfiguration.getInstance().isIgnoreXmlConfiguration();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Task<? extends TaskParameters> getTask(TaskParameters parameters) throws TaskException {
        Class<? extends TaskParameters> parametersClass = parameters.getClass();
        Class<? extends Task> taskClass = GlobalConfiguration.getInstance().getTasksRegistry().getTask(parametersClass);
        if (taskClass == null) {
            throw new TaskNotFoundException(String.format("Unable to find a Task class able to execute %s",
                    parametersClass));
        }
        try {
            Constructor<? extends Task> constructor = taskClass.getConstructor();
            return constructor.newInstance();
        } catch (InstantiationException e) {
            throw new TaskException(ERROR_INSTANTIATING_THE_TASK, e);
        } catch (IllegalAccessException e) {
            throw new TaskException(ERROR_INSTANTIATING_THE_TASK, e);
        } catch (SecurityException e) {
            throw new TaskException(ERROR_INSTANTIATING_THE_TASK, e);
        } catch (NoSuchMethodException e) {
            throw new TaskException(
                    String.format("The task %s doesn't define a public no-args contructor.", taskClass), e);
        } catch (InvocationTargetException e) {
            throw new TaskException(ERROR_INSTANTIATING_THE_TASK, e);
        }
    }
}
