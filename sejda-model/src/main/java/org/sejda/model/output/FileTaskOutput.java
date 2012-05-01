/*
 * Created on 22/ago/2011
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

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sejda.model.exception.TaskOutputVisitException;
import org.sejda.model.validation.constraint.IsFile;

/**
 * {@link File} output destination.
 * 
 * @author Andrea Vacondio
 * 
 */
public class FileTaskOutput implements SingleTaskOutput<File> {

    @IsFile
    private final File file;

    /**
     * Creates a new instance of a {@link FileTaskOutput} using the input file.
     * 
     * @param file
     * @throws IllegalArgumentException
     *             if the input file is null or not a file
     */
    public FileTaskOutput(File file) {
        if (file == null || (file.exists() && !file.isFile())) {
            throw new IllegalArgumentException("A valid instance is expected (not null or existing file).");
        }
        this.file = file;
    }

    public File getDestination() {
        return file;
    }

    public void accept(TaskOutputDispatcher writer) throws TaskOutputVisitException {
        try {
            writer.dispatch(this);
        } catch (IOException e) {
            throw new TaskOutputVisitException("Exception dispatching the file task output.", e);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(getDestination()).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getDestination()).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof FileTaskOutput)) {
            return false;
        }
        FileTaskOutput output = (FileTaskOutput) other;
        return new EqualsBuilder().append(file, output.getDestination()).isEquals();
    }

}
