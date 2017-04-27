/*
 * Created on 30/mag/2010
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
package org.sejda.model.output;

import java.io.File;

import org.sejda.model.exception.TaskOutputVisitException;

/**
 * Represents task output destination where results of a manipulation will be written.
 * 
 * @author Andrea Vacondio
 * 
 * 
 */
public interface TaskOutput {

    /**
     * @return the output destination for the task
     */
    File getDestination();

    /**
     * Accept a dispatcher dispatching the correct method implementation
     * 
     * @param dispatcher
     * @throws TaskOutputVisitException
     *             in case of error
     */
    void accept(TaskOutputDispatcher dispatcher) throws TaskOutputVisitException;

}
