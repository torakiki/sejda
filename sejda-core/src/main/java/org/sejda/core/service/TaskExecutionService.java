/*
 * Created on 28/mag/2010
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
package org.sejda.core.service;

import org.sejda.model.parameter.base.TaskParameters;
import org.sejda.model.task.CancellationOption;

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

    /**
     * Same as above, only also allows for cancelling a running task
     * @param parameters
     * @param cancellationOption
     */
    void execute(TaskParameters parameters, CancellationOption cancellationOption);
}
