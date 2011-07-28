/*
 * Created on 16/jul/2011
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
package org.sejda.core.manipulation.model.input;

import org.sejda.core.exception.TaskIOException;

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
