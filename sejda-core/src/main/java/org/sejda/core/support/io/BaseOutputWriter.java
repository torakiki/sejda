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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.sejda.core.support.io.model.PopulatedFileOutput;
import org.sejda.model.output.DirectoryTaskOutput;
import org.sejda.model.output.FileTaskOutput;
import org.sejda.model.output.StreamTaskOutput;
import org.sejda.model.output.TaskOutputDispatcher;

/**
 * Provides support methods to handle output files. Can hold one or multiple output files and write them to the destination.
 * 
 * @author Andrea Vacondio
 * 
 */
class BaseOutputWriter implements TaskOutputDispatcher {

    private Map<String, File> multipleFiles;
    private boolean overwrite = false;

    public BaseOutputWriter(boolean overwrite) {
        this.multipleFiles = new HashMap<String, File>();
        this.overwrite = overwrite;
    }

    public void dispatch(FileTaskOutput output) throws IOException {
        OutputWriterHelper.copyToFile(multipleFiles, output.getDestination(), overwrite);

    }

    public void dispatch(DirectoryTaskOutput output) throws IOException {
        OutputWriterHelper.copyToDirectory(multipleFiles, output.getDestination(), overwrite);

    }

    public void dispatch(StreamTaskOutput output) throws IOException {
        OutputWriterHelper.copyToStream(multipleFiles, output.getDestination());
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
