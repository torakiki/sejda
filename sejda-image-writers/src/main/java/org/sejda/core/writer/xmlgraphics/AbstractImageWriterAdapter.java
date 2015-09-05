/*
 * Created on 19/set/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

    @Override
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

    @Override
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

    @Override
    public void close() throws IOException {
        nullSafeClose(getOutputDestination());
    }
}
