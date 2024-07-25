/*
 * Created on 29/ago/2010
 *
 * Copyright 2010 Sober Lemur S.r.l. and Sejda BV.
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

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.sejda.core.support.io.model.PopulatedFileOutput;
import org.sejda.model.output.DirectoryTaskOutput;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.output.FileOrDirectoryTaskOutput;
import org.sejda.model.output.FileTaskOutput;
import org.sejda.model.task.TaskExecutionContext;

/**
 * Multiple writer default implementation
 * 
 * @author Andrea Vacondio
 * 
 */
class DefaultMultipleOutputWriter implements MultipleOutputWriter {

    private Map<String, File> multipleFiles = new HashMap<>();
    private final ExistingOutputPolicy existingOutputPolicy;
    private final TaskExecutionContext executionContext;

    DefaultMultipleOutputWriter(ExistingOutputPolicy existingOutputPolicy, TaskExecutionContext executionContext) {
        this.existingOutputPolicy = defaultIfNull(existingOutputPolicy, ExistingOutputPolicy.FAIL);
        this.executionContext = executionContext;
    }

    @Override
    public void dispatch(FileTaskOutput output) throws IOException {
        throw new IOException("Unsupported FileTaskOutput for a multiple output task.");
    }

    @Override
    public void dispatch(DirectoryTaskOutput output) throws IOException {
        OutputWriterHelper.moveToDirectory(multipleFiles, output.getDestination(), existingOutputPolicy,
                executionContext);
    }

    @Override
    public void dispatch(FileOrDirectoryTaskOutput output) throws IOException {
        if (multipleFiles.size() > 1 || output.getDestination().isDirectory()) {
            OutputWriterHelper.moveToDirectory(multipleFiles, output.getDestination(), existingOutputPolicy,
                    executionContext);
        } else {
            OutputWriterHelper.moveToFile(multipleFiles, output.getDestination(), existingOutputPolicy,
                    executionContext);
        }
    }

    /**
     * adds the input {@link PopulatedFileOutput} to the collection of files awaiting to be flushed.
     * 
     * @param fileOutput
     */
    @Override
    public void addOutput(PopulatedFileOutput fileOutput) {
        if (nonNull(multipleFiles.putIfAbsent(fileOutput.getName(), fileOutput.getFile()))) {
            // we already have a file with the same name, this shouldn't happen but could happen in split by text or bookmarks
            int count = 1;
            String basename = getBaseName(fileOutput.getName());
            String extension = getExtension(fileOutput.getName());

            while (nonNull(
                    multipleFiles.putIfAbsent(format("%s(%d).%s", basename, count, extension), fileOutput.getFile()))
                    && count < 100) {
                count++;
            }
        }
    }

}
