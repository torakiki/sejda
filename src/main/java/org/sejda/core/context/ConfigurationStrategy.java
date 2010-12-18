/*
 * Created on 27/apr/2010
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

import org.sejda.core.manipulation.model.parameter.TaskParameters;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.notification.strategy.NotificationStrategy;

/**
 * Strategy used to load the configuration
 * 
 * @author Andrea Vacondio
 * 
 */
interface ConfigurationStrategy {

    /**
     * @return the notification strategy class to use
     */
    Class<? extends NotificationStrategy> getNotificationStrategy();

    /**
     * Retrieves all the configured {@link Task} stored in a map. The map key is the subclass of {@link TaskParameters} that the task can execute.
     * 
     * @return a map containing all the configured {@link Task}
     */
    @SuppressWarnings("unchecked")
    Map<Class<? extends TaskParameters>, Class<? extends Task>> getTasksMap();

    /**
     * 
     * @return true if the validation has to be performed by the framework, false otherwise (incoming requests are already validated externally)
     */
    boolean isValidation();
}
