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
package org.sejda.core.manipulation.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.sejda.core.manipulation.model.parameter.TaskParameters;
import org.sejda.core.manipulation.model.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation holding the tasks information in a Map
 * 
 * @author Andrea Vacondio
 * 
 */
@SuppressWarnings("unchecked")
public class DefaultTasksRegistry implements TasksRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultTasksRegistry.class);

    private Map<Class<? extends TaskParameters>, Class<? extends Task>> tasksMap;

    public DefaultTasksRegistry() {
        this.tasksMap = new HashMap<Class<? extends TaskParameters>, Class<? extends Task>>();
    }

    public Class<? extends Task> getTask(Class<? extends TaskParameters> parametersClass) {
        Class<? extends Task> retVal = tasksMap.get(parametersClass);
        if (retVal == null) {
            LOG.info(String.format(
                    "Unable to find a match for the input parameter class %s, searching for an assignable one",
                    parametersClass));
            retVal = findNearestTask(parametersClass);
        }
        return retVal;
    }

    /**
     * @param parametersClass
     * @return finds the nearest class able to execute the input parameter
     */
    private Class<? extends Task> findNearestTask(Class<? extends TaskParameters> parametersClass) {
        for (Entry<Class<? extends TaskParameters>, Class<? extends Task>> entry : tasksMap.entrySet()) {
            if (entry.getKey().isAssignableFrom(parametersClass)) {
                return entry.getValue();
            }
        }
        LOG.warn(String.format("Unable to find an assignable match for the input parameter class %s", parametersClass));
        return null;
    }

    public void addTask(Class<? extends TaskParameters> parameterClass, Class<? extends Task> taskClass) {
        tasksMap.put(parameterClass, taskClass);
    }

    public TasksRegistry clone() {
        TasksRegistry clone = new DefaultTasksRegistry();
        for (Entry<Class<? extends TaskParameters>, Class<? extends Task>> entry : tasksMap.entrySet()) {
            clone.addTask(entry.getKey(), entry.getValue());
        }
        return clone;
    }

}
