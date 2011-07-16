/*
 * Created on 29/mag/2010
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
package org.sejda.core.manipulation.model.input;

import java.io.InputStream;

import javax.validation.constraints.NotNull;

import org.sejda.core.exception.TaskIOException;

/**
 * {@link PdfSource} from a {@link InputStream}
 * 
 * @author Andrea Vacondio
 * 
 */
public final class PdfStreamSource extends PdfSource {

    @NotNull
    private final InputStream stream;

    private PdfStreamSource(InputStream stream, String name, String password) {
        super(name, password);
        this.stream = stream;
    }

    public InputStream getStream() {
        return stream;
    }

    @Override
    public <T> T open(PdfSourceOpener<T> opener) throws TaskIOException {
        return opener.open(this);
    }

    /**
     * Creates a new instance of the pdf source where a password is NOT required to open the source.
     * 
     * @param stream
     *            input pdf stream
     * @param name
     * @return a newly created instance
     */
    public static PdfStreamSource newInstanceNoPassword(InputStream stream, String name) {
        return PdfStreamSource.newInstanceWithPassword(stream, name, null);
    }

    /**
     * Creates a new instance of the pdf source where a password is required to open the source.
     * 
     * @param stream
     *            input pdf stream
     * @param name
     * @param password
     * @return a newly created instance
     * @throws IllegalArgumentException
     *             if the input stream or the input name are blank.
     */
    public static PdfStreamSource newInstanceWithPassword(InputStream stream, String name, String password) {
        if (stream == null) {
            throw new IllegalArgumentException("A not null stream instance and a not blank name are expected.");
        }
        return new PdfStreamSource(stream, name, password);
    }
}
