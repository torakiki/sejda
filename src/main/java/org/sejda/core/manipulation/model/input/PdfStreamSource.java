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

/**
 * {@link PdfSource} from a {@link InputStream}
 * 
 * @author Andrea Vacondio
 * 
 */
public class PdfStreamSource extends PdfSource {

    @NotNull
    private final InputStream stream;

    public PdfStreamSource(InputStream stream, String name) {
        this(stream, name, null);
    }

    public PdfStreamSource(InputStream stream, String name, String password) {
        super(name, password);
        this.stream = stream;
    }

    public InputStream getStream() {
        return stream;
    }

    @Override
    public PdfSourceType getSourceType() {
        return PdfSourceType.STREAM_SOURCE;
    }
}
