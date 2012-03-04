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
import java.io.OutputStream;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sejda.model.validation.constraint.IsFile;

/**
 * {@link File} output destination.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class FileOutput implements TaskOutput {

    @IsFile
    private final File file;

    private FileOutput(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public File getDirectory() {
        return null;
    }

    public OutputStream getStream() {
        return null;
    }

    public OutputType getOutputType() {
        return OutputType.FILE_OUTPUT;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(getOutputType()).append(getFile()).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getFile()).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof FileOutput)) {
            return false;
        }
        FileOutput output = (FileOutput) other;
        return new EqualsBuilder().append(getFile(), output.getFile()).isEquals();
    }

    /**
     * Creates a new instance of a {@link FileOutput} using the input file.
     * 
     * @param file
     * @return the newly created instance
     * @throws IllegalArgumentException
     *             if the input file is null or not a file
     */
    public static FileOutput newInstance(File file) {
        if (file == null || (file.exists() && !file.isFile())) {
            throw new IllegalArgumentException("A valid instance is expected (not null or existing file).");
        }
        return new FileOutput(file);
    }
}
