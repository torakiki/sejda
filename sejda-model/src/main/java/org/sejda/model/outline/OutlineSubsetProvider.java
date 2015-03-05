/*
 * Created on 27/jul/2011
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
