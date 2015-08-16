/*
 * Created on 16/jul/2011
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.input;

import org.sejda.model.exception.TaskIOException;

/**
 * Double Dispatch interface to open a {@link PdfSource}.
 * 
 * @author Andrea Vacondio
 * @param <T>
 *            the type returned by the open action.
 * @see <a href="http://java-x.blogspot.com/2006/05/double-dispatch-in-java.html">double dispatch</a>
 */
public interface PdfSourceOpener<T> {

    /**
     * Opens the input {@link PdfURLSource}.
     * 
     * @param source
     * @return generic result of the open action.
     * @throws TaskIOException
     *             if an error occurs opening the source
     */
    T open(PdfURLSource source) throws TaskIOException;

    /**
     * Opens the input {@link PdfFileSource}.
     * 
     * @param source
     * @return generic result of the open action.
     * @throws TaskIOException
     *             if an error occurs opening the source
     */

    T open(PdfFileSource source) throws TaskIOException;

    /**
     * Opens the input {@link PdfStreamSource}.
     * 
     * @param source
     * @return generic result of the open action.
     * @throws TaskIOException
     *             if an error occurs opening the source
     */

    T open(PdfStreamSource source) throws TaskIOException;
}
