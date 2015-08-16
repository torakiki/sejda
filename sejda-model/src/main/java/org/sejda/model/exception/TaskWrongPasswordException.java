/*
 * Created on 20/giu/2010
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
 * Exception thrown when a wrong password has been set and it's not possible to open the pdf document (and execute the task)
 * 
 * @author Andrea Vacondio
 * 
 */
public class TaskWrongPasswordException extends TaskIOException {

    private static final long serialVersionUID = -5517166148313118559L;

    /**
     * @param message
     * @param cause
     */
    public TaskWrongPasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public TaskWrongPasswordException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public TaskWrongPasswordException(Throwable cause) {
        super(cause);
    }

}
