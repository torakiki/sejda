/*
 * Created on 29/ago/2010
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

import org.sejda.core.support.io.model.PopulatedFileOutput;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.output.OutputType;
import org.sejda.model.output.TaskOutput;

/**
 * Provides support methods to write multiple output {@link TaskOutput} for those tasks writing multiple outputs. Can hold multiple output temporary files created by a task and
 * write them to the destination when the task requires to flush.
 * 
 * <pre>
 * {@code
 * MultipleOutputWriterSupport outputWriter = new MultipleOutputWriterSupport();
 * outputWriter.addOutput(file(tmpFile).name("newName"));
 * ....
 * AbstractPdfOutput output = ...
 * boolean overwrite = ...
 * outputWriter.flushOutputs(output, overwrite);
 * }
 * </pre>
 * 
 * 
 * @author Andrea Vacondio
 * 
 */
class DefaultMultipleOutputWriter extends BaseOutputWriter implements MultipleOutputWriter {

    public void flushOutputs(TaskOutput output, boolean overwrite) throws TaskIOException {
        try {
            if (OutputType.FILE_OUTPUT.equals(output.getOutputType())) {
                throw new TaskIOException("Unsupported file ouput for a multiple output task.");
            }
            writeToNonFileDestination(output, overwrite);
        } finally {
            clear();
        }
    }

    public void addOutput(PopulatedFileOutput fileOutput) {
        add(fileOutput);
    }
}
