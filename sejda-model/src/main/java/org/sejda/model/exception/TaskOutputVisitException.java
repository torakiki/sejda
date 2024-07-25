/*
 * Created on 01/mag/2012
 * Copyright 2011 Sober Lemur S.r.l. and Sejda BV.
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
 * Exception thrown when visiting a TaskOutput
 * 
 * @author Andrea Vacondio
 * 
 */
public class TaskOutputVisitException extends TaskException {

    private static final long serialVersionUID = -5954396440602246066L;

    public TaskOutputVisitException() {
        super();
    }

    public TaskOutputVisitException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskOutputVisitException(String message) {
        super(message);
    }

    public TaskOutputVisitException(Throwable cause) {
        super(cause);
    }
}
