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
 * If no <b>sejda.xml</b> is found in the classpath a default configuration is loaded: <b>sejda.default.xml</b>
 * 
 * @author Andrea Vacondio
 * 
 */
public class DefaultSejdaContext implements SejdaContext {

    private static final String ERROR_INSTANTIATING_THE_TASK = "Error instantiating the task";

    @Override
    public final Class<? extends NotificationStrategy> getNotificationStrategy() {
        return GlobalConfiguration.getInstance().getNotificationStrategy();
    }

    @Override
    public boolean isValidation() {
        return GlobalConfiguration.getInstance().isValidation();
    }

    public boolean isIgnoreXmlConfiguration() {
        return GlobalConfiguration.getInstance().isIgnoreXmlConfiguration();
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Task<? extends TaskParameters> getTask(TaskParameters parameters) throws TaskException {
        Class<? extends TaskParameters> parametersClass = parameters.getClass();
        Class<? extends Task> taskClass = GlobalConfiguration.getInstance().getTasksRegistry().getTask(parametersClass);
        if (taskClass == null) {
            throw new TaskNotFoundException(
                    String.format("Unable to find a Task class able to execute %s", parametersClass));
        }
        try {
            Constructor<? extends Task> constructor = taskClass.getConstructor();
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | SecurityException e) {
            throw new TaskException(ERROR_INSTANTIATING_THE_TASK, e);
        } catch (NoSuchMethodException e) {
            throw new TaskException(String.format("The task %s doesn't define a public no-args contructor.", taskClass),
                    e);
        }
    }
}
