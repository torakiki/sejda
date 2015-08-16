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

/**
 * Exception thrown when the arguments passed to the console are found to be invalid
 * 
 * @author Eduard Weissmann
 * 
 */
public class ArgumentValidationException extends ConsoleException {

    private static final long serialVersionUID = 1L;

    public ArgumentValidationException(String message) {
        super(message);
    }

    public ArgumentValidationException(Throwable cause) {
        super(cause);
    }

    public ArgumentValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
