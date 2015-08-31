/*
 * Created on 10/giu/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.split;

import org.sejda.model.exception.TaskException;

/**
 * Strategy used by the by the split implementations to know when it's time to close the ongoing output and open a new one.
 * 
 * @author Andrea Vacondio
 */
public interface NextOutputStrategy {

    /**
     * Ensures that the strategy implementation is in a valid state.
     * 
     * @throws TaskException
     *             if not in a valid state.
     */
    void ensureIsValid() throws TaskException;

    /**
     * @param page
     *            the current processing page
     * @return true if the splitter should open a new output, false otherwise.
     * @throws TaskException
     *             if an error occurs while verifying if the page is an open page
     */
    boolean isOpening(Integer page) throws TaskException;

    /**
     * @param page
     *            the current processing page
     * @return true if the splitter should close the current output, false otherwise.
     * @throws TaskException
     *             if an error occurs while verifying if the page is an close page
     */
    boolean isClosing(Integer page) throws TaskException;
}
