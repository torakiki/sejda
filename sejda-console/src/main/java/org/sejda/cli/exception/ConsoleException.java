/*
 * Created on Oct 10, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
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
