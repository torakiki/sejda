/*
 * Created on 05/nov/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
