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
