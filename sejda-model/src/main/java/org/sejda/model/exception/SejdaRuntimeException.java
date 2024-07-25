/*
 * Created on 01/mag/2010
 *
 * Copyright 2010 Sober Lemur S.r.l. and Sejda BV.
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
 * General Sejda runtime exception.
 * 
 * @author Andrea Vacondio
 * 
 */
public class SejdaRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1630506833274259591L;

    public SejdaRuntimeException() {
        super();
    }

    /**
     * @param message
     * @param cause
     */
    public SejdaRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public SejdaRuntimeException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public SejdaRuntimeException(Throwable cause) {
        super(cause);
    }

}
