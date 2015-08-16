/*
 * Created on 05/mag/2010
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

import java.util.Map;

import org.sejda.model.parameter.base.TaskParameters;
import org.sejda.model.task.Task;

/**
 * Registry of the configured tasks
 * 
 * @author Andrea Vacondio
 * 
 */
@SuppressWarnings("rawtypes")
interface TasksRegistry {

    /**
     * Search among the configured tasks the implementation that better can execute the input parameters class.
     * 
     * @param parametersClass
     * @return the most suitable {@link Task} class for the input parameters class or null if nothing can be found.
     */

    Class<? extends Task> getTask(Class<? extends TaskParameters> parametersClass);

    /**
     * add to the registry the input task class responsible for carrying out the parameter class.
     * 
     * @param parameterClass
     * @param taskClass
     */
    void addTask(Class<? extends TaskParameters> parameterClass, Class<? extends Task> taskClass);

    /**
     * @return an unmodifiable version of the configured tasks
     */
    Map<Class<? extends TaskParameters>, Class<? extends Task>> getTasks();

}
