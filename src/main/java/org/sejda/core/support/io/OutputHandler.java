/*
 * Created on 04/giu/2010
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.sejda.core.exception.TaskIOException;
import org.sejda.core.manipulation.model.output.AbstractOutput;
import org.sejda.core.manipulation.model.output.OutputType;
import org.sejda.core.manipulation.model.output.PdfDirectoryOutput;
import org.sejda.core.manipulation.model.output.PdfFileOutput;
import org.sejda.core.manipulation.model.output.PdfStreamOutput;

/**
 * Support class used to write a {@link File} to a destination.<br />
 * Usage
 * <p>
 * <code>write(inputFile).overwriting(false).to(destination(out).withPrefix("prefix");</code>
 * </p>
 * 
 * @author Andrea Vacondio
 * 
 */
public final class OutputHandler implements ToDestinationWriter, OverwriteDestination {

    private static final String BUFFER_NAME = "SejdaTmpBuffer";

    private File inputFile;
    private boolean overwrite = false;
    private String prefix = "";
    private AbstractOutput output = null;

    private OutputHandler(File inputFile) {
        this.inputFile = inputFile;
    }

    /**
     * create an output writer that will write the input file to a destination
     * 
     * @param inputFile
     * @return
     */
    public static OverwriteDestination write(File inputFile) {
        return new OutputHandler(inputFile);
    }

    public void to(DestinationWithPrefix destination) throws TaskIOException {
        this.prefix = destination.getPrefix();
        this.output = destination.getOutputDestination();
        executeCopy();
    }

    public void to(DestinationWithoutPrefix destination) throws TaskIOException {
        this.prefix = "";
        this.output = destination.getOutputDestination();
        executeCopy();
    }

    private void executeCopy() throws TaskIOException {
        OutputType type = output.getOutputType();
        if (OutputType.STREAM_OUTPUT.equals(type)) {
            copyToStream(((PdfStreamOutput) output).getStream());
        } else if (OutputType.FILE_OUTPUT.equals(type)) {
            copyToFile(((PdfFileOutput) output).getFile());
        } else {
            copyToDirectory(((PdfDirectoryOutput) output).getFile(), prefix);
        }
    }

    /**
     * Creates an output file with the given prefix and copy the input file to it.
     * 
     * @param out
     * @param prefix
     * @throws TaskIOException
     */
    private void copyToDirectory(File out, String prefix) throws TaskIOException {
        File outFile = new File(out, prefix);
        copyToFile(outFile);
    }

    /**
     * Copy the input file to the output file
     * 
     * @param out
     * @throws TaskIOException
     */
    private void copyToFile(File out) throws TaskIOException {
        if (!overwrite && out.exists()) {
            throw new TaskIOException(String.format(
                    "Unable to overwrite the output file %s with the input %s (overwrite is false)", inputFile, out));
        }
        try {
            FileUtils.copyFile(inputFile, out);
            if (!inputFile.delete()) {
                inputFile.deleteOnExit();
            }
        } catch (IOException e) {
            throw new TaskIOException("Unable to copy the input file to the output file", e);
        }
    }

    /**
     * Copy the input file to the output stream
     * 
     * @param out
     * @throws TaskIOException
     */
    private void copyToStream(OutputStream out) throws TaskIOException {
        FileInputStream input = null;
        try {
            input = new FileInputStream(inputFile);
            IOUtils.copyLarge(input, out);
        } catch (IOException e) {
            throw new TaskIOException("Unable to copy the input file to the output stream", e);
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    /**
     * @return a temporary pdf file that will be deleted when the JVM terminates.
     * @throws TaskIOException
     * @see {@link File#deleteOnExit()}
     */
    public static File createTemporaryPdfBuffer() throws TaskIOException {
        try {
            File retVal = File.createTempFile(BUFFER_NAME, ".pdf");
            return retVal;
        } catch (IOException e) {
            throw new TaskIOException("Unable to create temporary buffer", e);
        }
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public ToDestinationWriter overwriting(boolean overwrite) {
        this.overwrite = overwrite;
        return this;
    }
}
