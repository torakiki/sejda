/*
 * Created on 24/giu/2010
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
 * Exception thrown after validation of the task parameters if the validation fails
 * 
 * @author Andrea Vacondio
 * 
 */
public class InvalidTaskParametersException extends TaskException {

    private static final long serialVersionUID = 1046358680829746043L;

    public InvalidTaskParametersException() {
        super();
    }

    public InvalidTaskParametersException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidTaskParametersException(String message) {
        super(message);
    }

    public InvalidTaskParametersException(Throwable cause) {
        super(cause);
    }
}
