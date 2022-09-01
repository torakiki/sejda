/*
 * Created on 12/mag/2010
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

import org.sejda.model.parameter.base.TaskParameters;
import org.sejda.model.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Default implementation holding the tasks information providing accessory methods.
 * 
 * @author Andrea Vacondio
 * 
 */
@SuppressWarnings("rawtypes")
class DefaultTasksRegistry implements TasksRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultTasksRegistry.class);

    private Map<Class<? extends TaskParameters>, Class<? extends Task>> tasksMap = new HashMap<>();


    @Override
    public Class<? extends Task> getTask(Class<? extends TaskParameters> parametersClass) {
        Class<? extends Task> retVal = tasksMap.get(parametersClass);
        if (retVal == null) {
            LOG.info("Unable to find a match for the input parameter class {}, searching for an assignable one",
                    parametersClass);
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
        LOG.warn("Unable to find an assignable match for the input parameter class {}", parametersClass);
        return null;
    }

    @Override
    public void addTask(Class<? extends TaskParameters> parameterClass, Class<? extends Task> taskClass) {
        synchronized (tasksMap) {
            tasksMap.put(parameterClass, taskClass);
        }
    }

    @Override
    public Map<Class<? extends TaskParameters>, Class<? extends Task>> getTasks() {
        return Collections.unmodifiableMap(tasksMap);
    }

}
