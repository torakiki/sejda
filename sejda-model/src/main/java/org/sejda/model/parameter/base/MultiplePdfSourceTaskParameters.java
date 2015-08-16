/*
 * Created on Sep 2, 2011
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
package org.sejda.model.parameter.base;

import java.util.List;

import org.sejda.model.input.PdfSource;

/**
 * A parameter whose execution inputs consists of a list of pdf documents.
 * 
 * @author Eduard Weissmann
 * 
 */
public interface MultiplePdfSourceTaskParameters extends TaskParameters {

    /**
     * adds the input source to the source list.
     * 
     * @param input
     */
    void addSource(PdfSource<?> input);

    /**
     * @return a view of the source list
     */
    List<PdfSource<?>> getSourceList();
}
