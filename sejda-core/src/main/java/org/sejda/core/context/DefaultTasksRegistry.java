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
package org.sejda.core.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.sejda.model.parameter.base.TaskParameters;
import org.sejda.model.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation holding the tasks information providing accessory methods.
 * 
 * @author Andrea Vacondio
 * 
 */
@SuppressWarnings("rawtypes")
class DefaultTasksRegistry implements TasksRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultTasksRegistry.class);

    private Map<Class<? extends TaskParameters>, Class<? extends Task>> tasksMap;

    DefaultTasksRegistry() {
        this.tasksMap = new HashMap<Class<? extends TaskParameters>, Class<? extends Task>>();
    }

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

    public void addTask(Class<? extends TaskParameters> parameterClass, Class<? extends Task> taskClass) {
        synchronized (tasksMap) {
            tasksMap.put(parameterClass, taskClass);
        }
    }

    public Map<Class<? extends TaskParameters>, Class<? extends Task>> getTasks() {
        return Collections.unmodifiableMap(tasksMap);
    }

}
