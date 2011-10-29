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
package org.sejda.core.manipulation.model.task;

import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.model.parameter.base.TaskParameters;

/**
 * Interface to defines the task lifecycle. Generically defines the {@link TaskParameters} subclass used to parameterize the task execution. Implementing classes must define a
 * public no-args constructor that will be reflectively invoked when the {@link Task} is executed.
 * 
 * @author Andrea Vacondio
 * 
 * @param <T>
 *            parameters type to be executed
 */
public interface Task<T extends TaskParameters> {

    /**
     * @return The notifiable metadata for the task. All events sent for this task will include the task metadata.
     */
    NotifiableTaskMetadata getNotifiableTaskMetadata();

    /**
     * Called before the actual execution of the task. Can be used to perform additional validation or initialization and to deny the execution in case some requirements are not
     * met throwing a {@link TaskException}.
     * 
     * @param parameters
     *            the parameters to be executed
     * @throws TaskException
     *             in case of unexpected errors
     * 
     */
    void before(T parameters) throws TaskException;

    /**
     * Executes the task with the input parameters
     * 
     * @param parameters
     * @throws TaskException
     */
    void execute(T parameters) throws TaskException;

    /**
     * Called after the task is executed, can be used to close resources. This method is called in a finally block therefore it's always called even when the task execution throws
     * an exception.
     */
    void after();
}
