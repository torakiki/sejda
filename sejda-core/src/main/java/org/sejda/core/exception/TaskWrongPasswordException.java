/*
 * Created on 20/giu/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.core.exception;

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
