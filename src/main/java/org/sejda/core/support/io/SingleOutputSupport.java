/*
 * Created on 19/giu/2010
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
package org.sejda.core.support.io;

import org.sejda.core.exception.TaskIOException;
import org.sejda.core.manipulation.model.output.AbstractPdfOutput;
import org.sejda.core.support.io.model.PopulatedFileOutput;

/**
 * DSL interface to expose methods a single output task needs (tasks generating a single file as output).
 * 
 * <pre>
 * {@code
 * PopulatedFileOutput singleOut = file(tmpFile).name("newName");
 * AbstractPdfOutput output = ...
 * boolean overwrite = ...
 * singleOutput().flushSingleOutput(singleOut, output, overwrite);
 * }
 * </pre>
 * 
 * @author Andrea Vacondio
 * 
 */
public interface SingleOutputSupport {

    /**
     * flush of the given {@link PopulatedFileOutput} to the output destination and deletes it.
     * 
     * @param fileOutput
     *            the input file that will be written to the destination.
     * @param output
     *            the destination.
     * @param overwrite
     *            if true overwrite the destination of already exists.
     * @throws TaskIOException
     *             in case of error
     */
    void flushSingleOutput(PopulatedFileOutput fileOutput, AbstractPdfOutput output, boolean overwrite)
            throws TaskIOException;

}
