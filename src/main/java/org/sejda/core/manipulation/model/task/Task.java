/*
 * Created on 27/apr/2010
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
package org.sejda.core.manipulation.model.task;

import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.model.parameter.TaskParameters;

/**
 * Model for a task to be executed. Can generically define the {@link TaskParameters} subclass used to parametrize the execution
 * 
 * @author Andrea Vacondio
 * 
 * @param <T>
 *            parameters type to be executed
 */
public interface Task<T extends TaskParameters> {

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
