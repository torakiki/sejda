/*
 * Created on 27 feb 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.model.input;

import java.io.InputStream;

import javax.validation.constraints.NotNull;

import org.sejda.model.exception.TaskIOException;

/**
 * A {@link InputStream} source
 * 
 * @author Andrea Vacondio
 *
 */
public class StreamSource extends AbstractSource<InputStream> {
    @NotNull
    private final InputStream stream;

    private StreamSource(InputStream stream, String name) {
        super(name);
        this.stream = stream;
    }

    @Override
    public InputStream getSource() {
        return stream;
    }

    @Override
    public <R> R dispatch(SourceDispatcher<R> dispatcher) throws TaskIOException {
        return dispatcher.dispatch(this);
    }

    public static StreamSource newInstance(InputStream stream, String name) {
        if (stream == null) {
            throw new IllegalArgumentException("A not null stream instance and a not blank name are expected.");
        }
        return new StreamSource(stream, name);
    }

}
