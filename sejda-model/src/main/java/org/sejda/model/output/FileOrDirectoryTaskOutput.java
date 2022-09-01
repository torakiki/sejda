/*
 * Copyright 2017 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.model.output;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sejda.model.exception.TaskOutputVisitException;

public class FileOrDirectoryTaskOutput extends AbstractTaskOutput implements SingleOrMultipleTaskOutput {

    private File file;

    public FileOrDirectoryTaskOutput(File file) {
        if (file == null) {
            throw new IllegalArgumentException("A not null file or directory instance is expected");
        }
        this.file = file;
    }

    @Override
    public File getDestination() {
        return file;
    }

    @Override
    public void accept(TaskOutputDispatcher writer) throws TaskOutputVisitException {
        try {
            writer.dispatch(this);
        } catch (IOException e) {
            throw new TaskOutputVisitException("Exception dispatching the file or directory task output.", e);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append(file).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(file).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof FileOrDirectoryTaskOutput output)) {
            return false;
        }
        return new EqualsBuilder().appendSuper(super.equals(other)).append(file, output.file).isEquals();
    }

    /**
     * Creates a {@link FileOrDirectoryTaskOutput} making sure the input file is a valid file instance
     * 
     * @param file
     * @return the created instance
     */
    public static FileOrDirectoryTaskOutput file(File file) {
        if (file == null || (file.exists() && !file.isFile())) {
            throw new IllegalArgumentException("A valid instance is expected (not null or existing file).");
        }
        return new FileOrDirectoryTaskOutput(file);
    }

    /**
     * Creates a {@link FileOrDirectoryTaskOutput} making sure the input file is a valid directory instance
     * 
     * @param directory
     * @return the created instance
     */
    public static FileOrDirectoryTaskOutput directory(File directory) {
        if (directory == null || !directory.isDirectory()) {
            throw new IllegalArgumentException("A not null directory instance is expected. Path: " + directory);
        }
        return new FileOrDirectoryTaskOutput(directory);
    }
}
