/*
 * Created on Oct 12, 2011
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

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * Default console {@link UncaughtExceptionHandler} that doesnt print stacktraces to System.err for expected uncaught exceptions.
 * Since the console needs to exit with a corresponding error code when an exception occurs, all exceptions (even expected) are propagated to main(), but not all need to end up
 * being printed to System.err
 * 
 * @author Eduard Weissmann
 * 
 */
public class DefaultUncaughtExceptionHandler implements UncaughtExceptionHandler {

    @Override
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
