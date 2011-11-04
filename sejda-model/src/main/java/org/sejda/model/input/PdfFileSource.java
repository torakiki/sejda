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
package org.sejda.model.input;

import java.io.File;

import org.sejda.model.exception.TaskIOException;
import org.sejda.model.validation.constraint.PdfFile;

/**
 * {@link AbstractPdfSource} from a {@link File}
 * 
 * @author Andrea Vacondio
 * 
 */
public final class PdfFileSource extends AbstractPdfSource {

    @PdfFile
    private final File file;

    private PdfFileSource(File file, String password) {
        super(file.getName(), password);
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    @Override
    public <T> T open(PdfSourceOpener<T> opener) throws TaskIOException {
        return opener.open(this);
    }

    /**
     * Creates a new instance of the pdf source where a password is NOT required to open the source.
     * 
     * @param file
     *            input pdf file
     * @return a newly created instance
     */
    public static PdfFileSource newInstanceNoPassword(File file) {
        return PdfFileSource.newInstanceWithPassword(file, null);
    }

    /**
     * Creates a new instance of the pdf source where a password is required to open the source.
     * 
     * @param file
     *            input pdf file
     * @param password
     * @return a newly created instance
     */
    public static PdfFileSource newInstanceWithPassword(File file, String password) {
        if (file == null || !file.isFile()) {
            throw new IllegalArgumentException("A not null File instance that isFile is expected.");
        }
        return new PdfFileSource(file, password);
    }

}
