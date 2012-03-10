/*
 * Created on 09/mar/2012
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
package org.sejda.model.output;

import org.sejda.model.exception.TaskIOException;

/**
 * Double-dispatch interface to dispatch to the correct implementation of a {@link TaskOutput}.
 * 
 * @author Andrea Vacondio
 * 
 */
public interface TaskOutputDispatcher {

    /**
     * writes to a {@link FileTaskOutput} destination.
     * 
     * @param output
     * @throws TaskIOException
     */
    void dispatch(FileTaskOutput output) throws TaskIOException;

    /**
     * writes to a {@link DirectoryTaskOutput} destination.
     * 
     * @param output
     * @throws TaskIOException
     */
    void dispatch(DirectoryTaskOutput output) throws TaskIOException;

    /**
     * writes to a {@link StreamTaskOutput} destination.
     * 
     * @param output
     * @throws TaskIOException
     */
    void dispatch(StreamTaskOutput output) throws TaskIOException;

}
