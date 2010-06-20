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

import static org.sejda.core.support.io.handler.OutputDestination.destination;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.sejda.core.exception.TaskIOException;
import org.sejda.core.manipulation.model.output.AbstractPdfOutput;
import org.sejda.core.manipulation.model.output.OutputType;
import org.sejda.core.manipulation.model.output.PdfDirectoryOutput;
import org.sejda.core.manipulation.model.output.PdfFileOutput;
import org.sejda.core.manipulation.model.output.PdfStreamOutput;
import org.sejda.core.support.io.handler.Destination;
import org.sejda.core.support.io.handler.OutputWriter;

/**
 * Provides support methods for the tasks to handle output files. Can hold one or multiple output files and write them to the destination when the tasks require to flush the
 * output. An extending class can call the {@link OutputWriterSupport#multipleOutputs()} method to have access to the {@link MultipleOutputSupport} interface methods or
 * {@link OutputWriterSupport#singleOutput()} to have access to the {@link SingleOutputSupport} interface.
 * 
 * <pre>
 * {@code
 * multipleOutputs().add(file(tmpFile).name("newName"));
 * ....
 * AbstractPdfOutput output = ...
 * boolean overwrite = ...
 * multipleOutputs().flushOutputs(output, overwrite);
 * }
 * </pre>
 * 
 * Or:
 * 
 * <pre>
 * {@code
 * PopulatedFileOutput singleOut = file(tmpFile).name("newName");
 * AbstractPdfOutput output = ...
 * boolean overwrite = ...
 * singleOutput().flushSingleOutput(singleOut, output, overwrite);
 * }
 * </pre>
 * 
 * @author Andrea Vacondio
 * 
 */
public class OutputWriterSupport implements MultipleOutputSupport, SingleOutputSupport {

    private static final String BUFFER_NAME = "SejdaTmpBuffer";

    private Map<String, File> multipleFiles;
    private OutputWriter outputWriter;

    public OutputWriterSupport() {
        this.multipleFiles = new HashMap<String, File>();
        this.outputWriter = new OutputWriter();
    }

    protected MultipleOutputSupport multipleOutputs() {
        return this;
    }

    protected SingleOutputSupport singleOutput() {
        return this;
    }

    public void flushOutputs(AbstractPdfOutput output, boolean overwrite) throws TaskIOException {
        try {
            if (OutputType.FILE_OUTPUT.equals(output.getOutputType())) {
                throw new TaskIOException("Unsupported file ouput for a multiple output task.");
            } else {
                if (OutputType.DIRECTORY_OUTPUT.equals(output.getOutputType())) {
                    write(destination((PdfDirectoryOutput) output).overwriting(overwrite));
                } else {
                    write(destination((PdfStreamOutput) output));
                }
            }
        } finally {
            multipleFiles.clear();
        }
    }

    /**
     * Writes the stored files to the destination
     * 
     * @param destination
     * @throws TaskIOException
     */
    private void write(Destination destination) throws TaskIOException {
        outputWriter.executeCopy(multipleFiles, destination);
    }

    public void add(PopulatedFileOutput fileOutput) {
        fileOutput.getFile().deleteOnExit();
        multipleFiles.put(fileOutput.getName(), fileOutput.getFile());
    }

    public void flushSingleOutput(PopulatedFileOutput fileOutput, AbstractPdfOutput output, boolean overwrite)
            throws TaskIOException {
        try {
            if (OutputType.FILE_OUTPUT.equals(output.getOutputType())) {
                add(fileOutput);
                outputWriter.executeCopy(multipleFiles, destination((PdfFileOutput) output).overwriting(overwrite));
            } else {
                add(fileOutput);
                flushOutputs(output, overwrite);
            }
        } finally {
            multipleFiles.clear();
        }
    }

    /**
     * @return a temporary pdf file
     * @throws TaskIOException
     */
    public File createTemporaryPdfBuffer() throws TaskIOException {
        try {
            File retVal = File.createTempFile(BUFFER_NAME, ".pdf");
            return retVal;
        } catch (IOException e) {
            throw new TaskIOException("Unable to create temporary buffer", e);
        }
    }
}
