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

import org.sejda.conversion.exception.ConversionException;
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
        return e instanceof ConsoleException || e instanceof ConversionException;
    }

    /**
     * @param e
     * @return true if the specified {@link Throwable} is an exception that is considered expected by sejda-core, and therefore can be considered as expected by the console also.
     */
    public static boolean isExpectedTaskException(Throwable e) {
        return org.apache.commons.lang3.exception.ExceptionUtils.indexOfType(e, TaskException.class) > 0;
    }
}
