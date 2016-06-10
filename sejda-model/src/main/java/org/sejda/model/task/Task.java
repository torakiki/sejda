/*
 * Created on 27/apr/2010
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
package org.sejda.model.task;

import org.sejda.model.exception.TaskException;
import org.sejda.model.parameter.base.TaskParameters;

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
     * Called before the actual execution of the task. Can be used to perform additional validation or initialization and to deny the execution in case some requirements are not
     * met throwing a {@link TaskException}.
     * 
     * @param parameters
     *            the parameters to be executed
     * @param context
     * @throws TaskException
     *             in case of unexpected errors
     * 
     */
    void before(T parameters, TaskExecutionContext context) throws TaskException;

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
