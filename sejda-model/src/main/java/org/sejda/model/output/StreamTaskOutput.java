/*
 * Created on 30/mag/2010
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.model.output;

import java.io.IOException;
import java.io.OutputStream;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sejda.model.exception.TaskOutputVisitException;

/**
 * {@link OutputStream} output destination for a task.
 * 
 * @author Andrea Vacondio
 * 
 */
public class StreamTaskOutput implements MultipleTaskOutput<OutputStream>, SingleTaskOutput<OutputStream> {

    @NotNull
    private final OutputStream stream;

    /**
     * Creates a new instance of a {@link StreamTaskOutput} using the input stream
     * 
     * @param stream
     * @throws IllegalArgumentException
     *             if the input stream is null
     */
    public StreamTaskOutput(OutputStream stream) {
        if (stream == null) {
            throw new IllegalArgumentException("A not null stream instance is expected.");
        }
        this.stream = stream;
    }

    public OutputStream getDestination() {
        return stream;
    }

    public void accept(TaskOutputDispatcher writer) throws TaskOutputVisitException {
        try {
            writer.dispatch(this);
        } catch (IOException e) {
            throw new TaskOutputVisitException("Exception dispatching the stream task output.", e);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(stream).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof StreamTaskOutput)) {
            return false;
        }
        StreamTaskOutput output = (StreamTaskOutput) other;
        return new EqualsBuilder().append(stream, output.getDestination()).isEquals();
    }
}
