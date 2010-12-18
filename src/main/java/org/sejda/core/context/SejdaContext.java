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

import org.sejda.core.manipulation.registry.TasksRegistry;
import org.sejda.core.notification.strategy.NotificationStrategy;

/**
 * Parent Context interface. Provides a configuration for the application.
 * 
 * @author Andrea Vacondio
 * 
 */
public interface SejdaContext {

    /**
     * @return the configured strategy to use during listeners notification.
     */
    Class<? extends NotificationStrategy> getNotificationStrategy();

    /**
     * @return the registry of the configured tasks
     */
    TasksRegistry getTasksRegistry();

    /**
     * @return true if validation should be performed on parameters instance during the task execution or false if incoming parameters instances are already validate externally.
     */
    boolean isValidation();
}
