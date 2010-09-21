/*
 * Created on 19/giu/2010
 * Copyright (C) 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.sejda.core.support.io;

import static org.sejda.core.support.io.model.OutputDestination.destination;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.sejda.core.exception.TaskIOException;
import org.sejda.core.manipulation.model.output.AbstractPdfOutput;
import org.sejda.core.manipulation.model.output.OutputType;
import org.sejda.core.manipulation.model.output.PdfDirectoryOutput;
import org.sejda.core.manipulation.model.output.PdfStreamOutput;
import org.sejda.core.support.io.model.Destination;
import org.sejda.core.support.io.model.PopulatedFileOutput;

/**
 * Provides support methods to handle output files. Can hold one or multiple output files and write them to the destination.
 * 
 * @author Andrea Vacondio
 * 
 */
class OutputWriterSupport {

    private static final String BUFFER_NAME = "SejdaTmpBuffer";

    private Map<String, File> multipleFiles;
    private OutputWriter outputWriter;

    public OutputWriterSupport() {
        this.multipleFiles = new HashMap<String, File>();
        this.outputWriter = new OutputWriter();
    }

    /**
     * writes to the given destination throwing an exception if the given destination is a file destination
     * 
     * @param output
     * @param overwrite
     * @throws TaskIOException
     */
    protected void writeToNonFileDestination(AbstractPdfOutput output, boolean overwrite) throws TaskIOException {
        if (OutputType.FILE_OUTPUT.equals(output.getOutputType())) {
            throw new TaskIOException("Unsupported file ouput for a multiple output task.");
        } else if (OutputType.DIRECTORY_OUTPUT.equals(output.getOutputType())) {
            write(destination((PdfDirectoryOutput) output).overwriting(overwrite));
        } else {
            write(destination((PdfStreamOutput) output));
        }
    }

    /**
     * Writes the stored files to the destination
     * 
     * @param destination
     * @throws TaskIOException
     */
    void write(Destination destination) throws TaskIOException {
        outputWriter.executeCopy(multipleFiles, destination);
    }

    /**
     * adds the input {@link PopulatedFileOutput} to the collection of files awaiting to be flushed.
     * 
     * @param fileOutput
     */
    void add(PopulatedFileOutput fileOutput) {
        fileOutput.getFile().deleteOnExit();
        multipleFiles.put(fileOutput.getName(), fileOutput.getFile());
    }

    /**
     * clear the collection of files awaiting to be flushed
     */
    void clear() {
        multipleFiles.clear();
    }

    /**
     * @return a temporary pdf file
     * @throws TaskIOException
     */
    public static File createTemporaryPdfBuffer() throws TaskIOException {
        try {
            return File.createTempFile(BUFFER_NAME, ".pdf");
        } catch (IOException e) {
            throw new TaskIOException("Unable to create temporary buffer", e);
        }
    }
}
