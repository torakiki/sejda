/*
 * Created on 30/mag/2010
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
package org.sejda.model.output;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sejda.model.exception.TaskOutputVisitException;
import org.sejda.model.validation.constraint.Directory;

/**
 * Directory output destination.
 * 
 * @author Andrea Vacondio
 * 
 */
public class DirectoryTaskOutput implements MultipleTaskOutput<File> {

    @Directory
    private final File directory;

    /**
     * Creates a new instance of a {@link DirectoryTaskOutput} using the input directory
     * 
     * @param directory
     * @throws IllegalArgumentException
     *             if the input directory is null or not a directory
     */
    public DirectoryTaskOutput(File directory) {
        if (directory == null || !directory.isDirectory()) {
            throw new IllegalArgumentException("A not null directory instance is expected.");
        }
        this.directory = directory;
    }

    public File getDestination() {
        return directory;
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
        return new ToStringBuilder(this).append(directory).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(directory).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DirectoryTaskOutput)) {
            return false;
        }
        DirectoryTaskOutput output = (DirectoryTaskOutput) other;
        return new EqualsBuilder().append(directory, output.getDestination()).isEquals();
    }
}
