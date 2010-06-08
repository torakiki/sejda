/*
 * Created on 31/mag/2010
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
package org.sejda.core.exception;

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

}
