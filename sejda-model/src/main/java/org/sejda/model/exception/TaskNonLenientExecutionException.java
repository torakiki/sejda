/*
 * Created on 02 gen 2017
 * Copyright 2015 Sober Lemur S.r.l. and Sejda BV.
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.model.exception;

/**
 * Exception signaling a task execution failure due to a recoverable exception thrown during a NON lenient task execution. Repeating the task execution leniently would likely
 * overcome this exception.
 * 
 * @author Andrea Vacondio
 *
 */
public class TaskNonLenientExecutionException extends TaskExecutionException {

    private static final long serialVersionUID = -3283178318931486615L;

    public TaskNonLenientExecutionException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    public TaskNonLenientExecutionException(String message) {
        super(message);
    }
}
