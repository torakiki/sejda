/*
 * Created on 28 feb 2016
 * Copyright 2015 Sober Lemur S.r.l. and Sejda BV.
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
package org.sejda.model.parameter.base;

import java.util.List;

import org.sejda.model.input.Source;

/**
 * Parameters with multiple {@link Source} input
 * 
 * @author Andrea Vacondio
 *
 */
public interface MultipleSourceTaskParameter {
    /**
     * adds the input source to the source list.
     * 
     * @param input
     */
    void addSource(Source<?> input);

    /**
     * @return a view of the source list
     */
    List<Source<?>> getSourceList();
}
