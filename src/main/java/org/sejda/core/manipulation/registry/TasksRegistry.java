/*
 * Created on 05/mag/2010
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
package org.sejda.core.manipulation.registry;

import org.sejda.core.manipulation.model.Task;
import org.sejda.core.manipulation.model.TaskParameters;


/**
 * Registry of the configured tasks
 * 
 * @author Andrea Vacondio
 * 
 */
@SuppressWarnings("unchecked")
public interface TasksRegistry {

    /**
     * Search among the configured tasks the implementation that better can execute the input parameters class.
     * 
     * @param parametersClass
     * @return the most suitable {@link Task} class for the input parameters class or null if nothing can be found.
     */
    Class<? extends Task> getTask(Class<? extends TaskParameters> parametersClass);
    
    /**
     * add to the registry the input task class responsible for carrying out the parameter class.
     * @param parameterClass
     * @param taskClass
     */
    void addTask(Class<? extends TaskParameters> parameterClass, Class<? extends Task> taskClass);
    
    /**
     * Creates a copy of the instance
     * @return the copy
     */
    TasksRegistry clone();
}
