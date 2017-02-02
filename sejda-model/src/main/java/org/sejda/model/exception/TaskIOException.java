/*
 * Created on 31/mag/2010
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
package org.sejda.model.exception;

/**
 * Exception thrown if an IOException is raised during the task execution
 * 
 * @author Andrea Vacondio
 * 
 */
public class TaskIOException extends TaskException {

    private static final long serialVersionUID = 5205172452237197631L;

    public TaskIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskIOException(String message) {
        super(message);
    }

    public TaskIOException(Throwable cause) {
        super(cause);
    }

    /**
     * @param condition
     * @param exceptionMessage
     * @throws TaskIOException
     *             with the given message if condition is false
     */
    public static void require(boolean condition, String exceptionMessage) throws TaskIOException {
        if (!condition) {
            throw new TaskIOException(exceptionMessage);
        }
    }
}
