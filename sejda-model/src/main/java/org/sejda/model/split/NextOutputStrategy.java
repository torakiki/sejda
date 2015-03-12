/*
 * Created on 10/giu/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.split;

import org.sejda.model.exception.TaskExecutionException;

/**
 * Strategy used by the by the split implementations to know when it's time to close the ongoing output and open a new one.
 * 
 * @author Andrea Vacondio
 */
public interface NextOutputStrategy {

    /**
     * Ensures that the strategy implementation is in a valid state.
     * 
     * @throws TaskExecutionException
     *             if not in a valid state.
     */
    void ensureIsValid() throws TaskExecutionException;

    /**
     * @param page
     *            the current processing page
     * @return true if the splitter should open a new output, false otherwise.
     */
    boolean isOpening(Integer page);

    /**
     * @param page
     *            the current processing page
     * @return true if the splitter should close the current output, false otherwise.
     */
    boolean isClosing(Integer page);
}
