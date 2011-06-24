/*
 * Created on 24/apr/2010
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
 * Exception related to the NotificationContext features
 * 
 * @author Andrea Vacondio
 * 
 */
public class NotificationContextException extends Exception {

    private static final long serialVersionUID = 117114938076275862L;

    public NotificationContextException() {
        super();
    }

    public NotificationContextException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotificationContextException(String message) {
        super(message);
    }

    public NotificationContextException(Throwable cause) {
        super(cause);
    }

}
