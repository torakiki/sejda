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
import java.util.HashMap;
import java.util.Map;

import org.sejda.core.exception.TaskIOException;
import org.sejda.core.manipulation.model.output.AbstractOutput;
import org.sejda.core.manipulation.model.output.OutputType;
import org.sejda.core.manipulation.model.output.PdfDirectoryOutput;
import org.sejda.core.manipulation.model.output.PdfStreamOutput;
import org.sejda.core.support.io.handler.Destination;
import org.sejda.core.support.io.handler.OutputHandler;
import org.sejda.core.support.io.handler.Destination.FileDestination;
import org.sejda.core.support.io.handler.ToDestinationWriter.ToFileDestinationWriter;

/**
 * Provides support methods for the tasks to handle output files. Can hold one or multiple output files and write them to the destination when the tasks require to flush the
 * output.
 * 
 * @author Andrea Vacondio
 * 
 */
public class OutputWriter implements MultipleOutput, SingleOutput, ToFileDestinationWriter {

    private Map<String, File> multipleFiles;
    private PopulatedFileOutput singleOutput;

    public OutputWriter() {
        this.multipleFiles = new HashMap<String, File>();
    }

    protected MultipleOutput multipleOutputs() {
        return this;
    }

    public void flushOutputs(AbstractOutput output, boolean overwrite) throws TaskIOException {
        if (OutputType.FILE_OUTPUT.equals(output.getOutputType())) {
            throw new TaskIOException("Unsupported file ouput for a multiple output task.");
        } else {
            if (OutputType.DIRECTORY_OUTPUT.equals(output.getOutputType())) {
                write(destination((PdfDirectoryOutput) output).overwriting(overwrite));
            } else {
                write(destination((PdfStreamOutput) output));
            }
        }

    }

    /**
     * Writes the stored files to the destination
     * 
     * @param destination
     * @throws TaskIOException
     */
    private void write(Destination destination) throws TaskIOException {
        OutputHandler.write(multipleFiles).to(destination);
    }

    public void add(PopulatedFileOutput fileOutput) {
        multipleFiles.put(fileOutput.getName(), fileOutput.getFile());
    }

    public ToFileDestinationWriter write(File file) {
        this.singleOutput = FileOutput.file(file).name(file.getName());
        return this;
    }

    /**
     * sets the destination for the single output and executes the copy to that destination
     * 
     * @param destination
     *            the destination
     */
    public void to(FileDestination destination) throws TaskIOException {
        OutputHandler.write(singleOutput).to(destination);
    }

    public ToFileDestinationWriter write(PopulatedFileOutput fileOutput) {
        this.singleOutput = fileOutput;
        return this;
    }

}
