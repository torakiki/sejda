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

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * Default console {@link UncaughtExceptionHandler} that doesnt print stacktraces to System.err for expected uncaught exceptions.<br/>
 * Since the console needs to exit with a corresponding error code when an exception occurs, all exceptions (even expected) are propagated to main(), but not all need to end up
 * being printed to System.err
 * 
 * @author Eduard Weissmann
 * 
 */
public class DefaultUncaughtExceptionHandler implements UncaughtExceptionHandler {

    public void uncaughtException(Thread t, Throwable e) {
        if (!isExpectedException(e)) {
            System.err.print("Exception in thread \"" + t.getName() + "\" ");
            e.printStackTrace(System.err);
        }
    }

    /**
     * @param e
     * @return true if the {@link Throwable} specified is an expected exception
     */
    private boolean isExpectedException(Throwable e) {
        return ExceptionUtils.isExpectedConsoleException(e) || ExceptionUtils.isExpectedTaskException(e);
    }

}
