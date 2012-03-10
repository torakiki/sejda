/*
 * Created on 30/mag/2010
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
package org.sejda.model.output;

import org.sejda.model.exception.TaskException;

/**
 * Represents task output destination where results of a manipulation will be written.
 * 
 * @param <T>
 *            the generic type for the output
 * @author Andrea Vacondio
 * 
 * 
 */
public interface TaskOutput<T> {

    /**
     * @return the output destination for the task
     */
    T getDestination();

    /**
     * Accept a dispatcher dispatching the correct method implementation
     * 
     * @param dispatcher
     * @throws TaskException
     *             in case of error
     */
    void accept(TaskOutputDispatcher dispatcher) throws TaskException;

}
