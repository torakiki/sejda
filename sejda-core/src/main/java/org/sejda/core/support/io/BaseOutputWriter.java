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

import static org.sejda.core.support.io.OutputDestination.destination;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.sejda.core.support.io.model.PopulatedFileOutput;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.output.DirectoryOutput;
import org.sejda.model.output.OutputType;
import org.sejda.model.output.StreamOutput;
import org.sejda.model.output.TaskOutput;

/**
 * Provides support methods to handle output files. Can hold one or multiple output files and write them to the destination.
 * 
 * @author Andrea Vacondio
 * 
 */
class BaseOutputWriter {

    private Map<String, File> multipleFiles;

    public BaseOutputWriter() {
        this.multipleFiles = new HashMap<String, File>();
    }

    /**
     * writes to the given destination throwing an exception if the given destination is a file destination
     * 
     * @param output
     * @param overwrite
     * @throws TaskIOException
     */
    void writeToNonFileDestination(TaskOutput output, boolean overwrite) throws TaskIOException {
        if (OutputType.FILE_OUTPUT.equals(output.getOutputType())) {
            throw new TaskIOException("Unsupported file output for a multiple output task.");
        }

        if (OutputType.DIRECTORY_OUTPUT.equals(output.getOutputType())) {
            write(destination((DirectoryOutput) output).overwriting(overwrite));
        } else {
            write(destination((StreamOutput) output));
        }
    }

    /**
     * Writes the stored files to the destination
     * 
     * @param destination
     * @throws TaskIOException
     */
    void write(Destination destination) throws TaskIOException {
        OutputWriterHelper.executeCopyAndDelete(multipleFiles, destination);
    }

    /**
     * adds the input {@link PopulatedFileOutput} to the collection of files awaiting to be flushed.
     * 
     * @param fileOutput
     */
    void add(PopulatedFileOutput fileOutput) {
        fileOutput.getFile().deleteOnExit();
        multipleFiles.put(fileOutput.getName(), fileOutput.getFile());
    }

    /**
     * clear the collection of files awaiting to be flushed
     */
    void clear() {
        multipleFiles.clear();
    }
}
