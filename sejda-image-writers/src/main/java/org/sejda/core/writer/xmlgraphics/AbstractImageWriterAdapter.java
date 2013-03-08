/*
 * Created on 19/set/2011
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
package org.sejda.core.writer.xmlgraphics;

import static org.sejda.common.ComponentsUtility.nullSafeClose;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import org.apache.xmlgraphics.image.codec.util.SeekableOutputStream;
import org.sejda.core.writer.model.ImageWriter;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.parameter.image.AbstractPdfToImageParameters;

/**
 * Abstract implementation of an adapter for an xml graphics image writer
 * 
 * @param <T>
 *            task parameter
 * @author Andrea Vacondio
 * 
 */
abstract class AbstractImageWriterAdapter<T extends AbstractPdfToImageParameters> implements ImageWriter<T> {

    private OutputStream outputDestination;

    public void openWriteDestination(File destination, T params) throws TaskIOException {
        try {
            openWriteDestination(new SeekableOutputStream(new RandomAccessFile(destination, "rw")), params);
        } catch (FileNotFoundException e) {
            throw new TaskIOException("Unable to find destination file.", e);
        }
    }

    public void setOutputStream(OutputStream destination) {
        if (destination == null) {
            throw new SejdaRuntimeException("Destination for the ImageWriter cannot be null");
        }
        this.outputDestination = destination;
    }

    public void closeDestination() throws TaskIOException {
        try {
            nullSafeClose(outputDestination);
        } catch (IOException e) {
            throw new TaskIOException(e);
        }
    }

    /**
     * 
     * @return the opened write destination or null if not opened
     */
    OutputStream getOutputDestination() {
        return outputDestination;
    }

    public void close() throws IOException {
        nullSafeClose(getOutputDestination());
    }
}
