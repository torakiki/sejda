/*
 * Created on 19/giu/2010
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
package org.sejda.core.support.io;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.sejda.core.support.io.model.PopulatedFileOutput;
import org.sejda.model.output.*;
import org.sejda.model.task.TaskExecutionContext;

/**
 * Provides support methods to handle output files. Can hold one or multiple output files and write them to the destination.
 * 
 * @author Andrea Vacondio
 * 
 */
abstract class BaseOutputWriter implements TaskOutputDispatcher {

    Map<String, File> multipleFiles;
    private ExistingOutputPolicy existingOutputPolicy = ExistingOutputPolicy.FAIL;
    private final TaskExecutionContext executionContext;

    public BaseOutputWriter(ExistingOutputPolicy existingOutputPolicy, TaskExecutionContext executionContext) {
        this.multipleFiles = new HashMap<>();
        this.existingOutputPolicy = ObjectUtils.defaultIfNull(existingOutputPolicy, ExistingOutputPolicy.FAIL);
        this.executionContext = executionContext;
    }

    @Override
    public void dispatch(FileTaskOutput output) throws IOException {
        OutputWriterHelper.moveToFile(multipleFiles, output.getDestination(), existingOutputPolicy, executionContext);

    }

    @Override
    public void dispatch(DirectoryTaskOutput output) throws IOException {
        OutputWriterHelper.moveToDirectory(multipleFiles, output.getDestination(), existingOutputPolicy,
                executionContext);

    }

    @Override
    public void dispatch(FileOrDirectoryTaskOutput output) throws IOException {
        if(multipleFiles.size() > 1 || output.getDestination().isDirectory()) {
            OutputWriterHelper.moveToDirectory(multipleFiles, output.getDestination(), existingOutputPolicy,
                    executionContext);
        } else {
            OutputWriterHelper.moveToFile(multipleFiles, output.getDestination(), existingOutputPolicy, executionContext);
        }
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

}
