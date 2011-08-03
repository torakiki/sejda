/*
 * Created on 03/jul/2011
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
package org.sejda.core.manipulation.model.task.itext.component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.model.pdf.PdfVersion;

import com.lowagie.text.pdf.PdfReader;

/**
 * Default pdf copier implementation using a {@link File} as output.
 * 
 * @author Andrea Vacondio
 * 
 */
public class DefaultPdfCopier extends AbstractPdfCopier {

    private OutputStream outputStream;

    /**
     * Creates a copier that writes to the given output file.
     * 
     * @param reader
     * @param outputFile
     * @param version
     *            version for the created pdf copy, if null the version number is taken from the input PdfReader.
     * @throws TaskException
     *             if the file is not found or an error occur opening the underlying copier.
     */
    public DefaultPdfCopier(PdfReader reader, File outputFile, PdfVersion version) throws TaskException {
        try {
            outputStream = new FileOutputStream(outputFile);
            open(reader, outputStream, version);
        } catch (FileNotFoundException e) {
            throw new TaskException(String.format("Unable to find the output file %s", outputFile.getPath()), e);
        }
    }

    @Override
    public void close() {
        super.close();
        IOUtils.closeQuietly(outputStream);
    }
}
