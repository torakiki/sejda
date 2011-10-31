/*
 * Created on Oct 12, 2011
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

import org.sejda.model.exception.TaskException;

/**
 * Exception utils
 * 
 * @author Eduard Weissmann
 * 
 */
public final class ExceptionUtils {

    private ExceptionUtils() {
        // don't instantiate
    }

    /**
     * @param e
     * @return true if specified {@link Throwable} e is an exception that is considered expected by sejda-console (validation failure, for example - something that is expected to
     *         occur if input is incorrect, not an unexpected failure)
     */
    public static boolean isExpectedConsoleException(Throwable e) {
        return e instanceof ConsoleException;
    }

    /**
     * @param e
     * @return true if the specified {@link Throwable} is an exception that is considered expected by sejda-core, and therefore can be considered as expected by the console also.
     */
    public static boolean isExpectedTaskException(Throwable e) {
        return org.apache.commons.lang3.exception.ExceptionUtils.indexOfType(e, TaskException.class) > 0;
    }
}
