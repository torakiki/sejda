/*
 * Created on 27/jul/2011
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
package org.sejda.model.outline;

import org.sejda.model.exception.TaskException;

/**
 * Statefull component providing a subset of the document outline.
 * 
 * @author Andrea Vacondio
 * @param <T>
 *            generic type for the outline returned by the provider.
 */
public interface OutlineSubsetProvider<T> {

    /**
     * Sets the start page from which the component will provide outline.
     * 
     * @param startPage
     */
    void startPage(int startPage);

    /**
     * 
     * @param endPage
     *            end page till which the component will provide bookmarks.
     * @return the document outline from start page to the provided end page.
     * @throws TaskException
     *             if the start page is not set or the end page is before the start.
     */
    T getOutlineUntillPage(int endPage) throws TaskException;

    /**
     * 
     * @param endPage
     *            end page till which the component will provide bookmarks.
     * @param offset
     *            page numbers should be shifted
     * @return the document outline from start page to the provided end page with the offset applied.
     * @throws TaskException
     *             if the start page is not set or the end page is before the start.
     */
    T getOutlineUntillPageWithOffset(int endPage, int offset) throws TaskException;

    /**
     * @param offset
     *            page numbers should be shifted
     * @return the whole document outline with the offset applied.
     */
    T getOutlineWithOffset(int offset);

}
