/*
 * Created on 05/nov/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
 * A pdf source that can be used as input for a pdf manipulation.
 * 
 * @param <T>
 *            the generic type for the source
 * @author Andrea Vacondio
 * 
 */
public interface PdfSource<T> {

    /**
     * @return the source
     */
    T getSource();

    /**
     * @return the name of the source
     */
    String getName();

    /**
     * @return password needed to open the source. Can be null.
     */
    String getPassword();

    /**
     * Updates the password needed to open the source. Can be null.
     */
    void setPassword(String password);

    /**
     * Dispatch method to open the source.
     * 
     * @param <R>
     *            generic type as result of the open action.
     * @param opener
     * @return result of the open action as a type defined by the dispatcher.
     * @throws TaskIOException
     *             in case of error opening the source.
     */
    <R> R open(PdfSourceOpener<R> opener) throws TaskIOException;
}
