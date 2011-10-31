/*
 * Created on 05/mag/2010
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
