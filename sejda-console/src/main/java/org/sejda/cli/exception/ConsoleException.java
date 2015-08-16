/*
 * Created on Oct 10, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.cli.exception;

import org.sejda.model.exception.SejdaRuntimeException;

/**
 * General console exception
 * 
 * @author Eduard Weissmann
 * 
 */
public class ConsoleException extends SejdaRuntimeException {

    private static final long serialVersionUID = 1L;

    ConsoleException() {
        super();
    }

    /**
     * @param message
     * @param cause
     */
    public ConsoleException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public ConsoleException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    ConsoleException(Throwable cause) {
        super(cause);
    }

}
