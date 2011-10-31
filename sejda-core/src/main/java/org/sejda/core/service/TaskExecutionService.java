/*
 * Created on 28/mag/2010
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
package org.sejda.core.service;

import org.sejda.model.parameter.base.TaskParameters;

/**
 * Service interface to perform the actual execution of a task.
 * @author Andrea Vacondio
 *
 */
public interface TaskExecutionService {

    /**
     * Perform the actual execution of a proper {@link org.sejda.model.task.Task} able to execute the input {@link TaskParameters}
     * @param parameters task parameters
     */
    void execute(TaskParameters parameters);
}
