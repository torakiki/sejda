/*
 * Created on 27 feb 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.model.input;

import org.sejda.model.exception.TaskIOException;

/**
 * source that can be used as input for a pdf manipulation.
 * 
 * @param <T>
 *            the generic type for the source
 * @author Andrea Vacondio
 *
 */
public interface Source<T> {

    /**
     * @return the source
     */
    T getSource();

    /**
     * @return the name of the source
     */
    String getName();

    /**
     * @param <R>
     *            generic type as result of the dispatch action.
     * @param dispatcher
     * @return result of the dispatch action as a type defined by the dispatcher.
     * @throws TaskIOException
     */
    <R> R dispatch(SourceDispatcher<R> dispatcher) throws TaskIOException;
}
