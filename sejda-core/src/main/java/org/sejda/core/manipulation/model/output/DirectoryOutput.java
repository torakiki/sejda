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
package org.sejda.core.manipulation.model.output;

import java.io.File;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.sejda.core.validation.constraint.Directory;

/**
 * Directory output destination.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class DirectoryOutput implements TaskOutput {

    @Directory
    private final File directory;

    private DirectoryOutput(File directory) {
        this.directory = directory;

    }

    public File getDirectory() {
        return directory;
    }

    public OutputType getOutputType() {
        return OutputType.DIRECTORY_OUTPUT;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(getOutputType()).append(directory).toString();
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
        if (!(other instanceof DirectoryOutput)) {
            return false;
        }
        DirectoryOutput output = (DirectoryOutput) other;
        return new EqualsBuilder().append(directory, output.getDirectory()).isEquals();
    }

    /**
     * Creates a new instance of a PdfOutput using the input directory
     * 
     * @param directory
     * @return the newly created instance
     * @throws IllegalArgumentException
     *             if the input directory is null or not a directory
     */
    public static DirectoryOutput newInstance(File directory) {
        if (directory == null || !directory.isDirectory()) {
            throw new IllegalArgumentException("A not null directory instance is expected.");
        }
        return new DirectoryOutput(directory);
    }
}
