/*
 * Created on 22/ago/2011
 * Copyright 2011 Sober Lemur S.r.l. and Sejda BV.
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

/**
 * {@link File} output destination.
 * 
 * @author Andrea Vacondio
 * 
 */
public class FileTaskOutput extends AbstractTaskOutput implements SingleTaskOutput {

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

    @Override
    public File getDestination() {
        return file;
    }

    @Override
    public void accept(TaskOutputDispatcher writer) throws TaskOutputVisitException {
        try {
            writer.dispatch(this);
        } catch (IOException e) {
            throw new TaskOutputVisitException("Exception dispatching the file task output.", e);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append(getDestination()).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(getDestination()).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof FileTaskOutput output)) {
            return false;
        }
        return new EqualsBuilder().appendSuper(super.equals(other)).append(file, output.getDestination()).isEquals();
    }

}
