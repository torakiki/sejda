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
package org.sejda.core.support.io.handler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.sejda.core.exception.TaskIOException;
import org.sejda.core.manipulation.model.output.AbstractOutput;
import org.sejda.core.manipulation.model.output.OutputType;
import org.sejda.core.manipulation.model.output.PdfDirectoryOutput;
import org.sejda.core.manipulation.model.output.PdfFileOutput;
import org.sejda.core.manipulation.model.output.PdfStreamOutput;
import org.sejda.core.support.io.PopulatedFileOutput;
import org.sejda.core.support.io.handler.Destination.FileDestination;
import org.sejda.core.support.io.handler.ToDestinationWriter.ToFileDestinationWriter;

/**
 * Support class used to write a temporary files created by tasks to a destination.<br />
 * Usage:
 * 
 * <pre>
 * {@code
 * File file = ...
 * PdfFileOutput outFile = ...
 * write(file).to(destination(outFile).overwriting(false));
 * }
 * </pre>
 * 
 * Or:
 * 
 * <pre>
 * {@code
 * Map<String, File> files = ...
 * PdfDirectoryOutput outDir = ...
 * write(files).to(destination(outDir).overwriting(false));
 * }
 * </pre>
 * 
 * Or:
 * 
 * <pre>
 * {@code
 * Map<String, File> files = ...
 * PdfStreamOutput streamDir = ...
 * write(files).to(destination(streamDir));
 * }
 * </pre>
 * 
 * @see PdfStreamOutput
 * @see PdfFileOutput
 * @see PdfDirectoryOutput
 * @author Andrea Vacondio
 * 
 */
public final class OutputHandler implements ToFileDestinationWriter, ToDestinationWriter {

    private static final String BUFFER_NAME = "SejdaTmpBuffer";

    private Map<String, File> files;
    private Destination destination = null;

    private OutputHandler(File inputFile) {
        this.files = new HashMap<String, File>();
        this.files.put(inputFile.getName(), inputFile);
    }

    private OutputHandler(Map<String, File> files) {
        this.files = new HashMap<String, File>(files);
    }

    private OutputHandler(PopulatedFileOutput fileWithOutputName) {
        this.files = new HashMap<String, File>();
        this.files.put(fileWithOutputName.getName(), fileWithOutputName.getFile());
    }

    /**
     * create an output writer that will write the input file to a destination
     * 
     * @param inputFile
     * @return the output writer with an unset destination
     */
    public static ToFileDestinationWriter write(File inputFile) {
        return new OutputHandler(inputFile);
    }

    /**
     * create an output writer that will write the input file to a destination using the provided name
     * 
     * @param inputFile
     * @return the output writer with an unset destination
     */
    public static ToFileDestinationWriter write(PopulatedFileOutput fileWithOutputName) {
        return new OutputHandler(fileWithOutputName);
    }

    /**
     * create an output writer that will write the input files to a destination
     * 
     * @param files
     * @return the output writer with an unset destination
     */
    public static ToDestinationWriter write(Map<String, File> files) {
        return new OutputHandler(files);
    }

    public void to(Destination destination) throws TaskIOException {
        this.destination = destination;
        executeCopy();
    }

    public void to(FileDestination destination) throws TaskIOException {
        to((Destination) destination);
    }

    private void executeCopy() throws TaskIOException {
        if (destination != null) {
            AbstractOutput outputDestination = destination.getOutputDestination();
            OutputType type = destination.getOutputDestination().getOutputType();
            if (OutputType.STREAM_OUTPUT.equals(type)) {
                copyToStream(((PdfStreamOutput) outputDestination).getStream());
            } else if (OutputType.FILE_OUTPUT.equals(type)) {
                copyToFile(((PdfFileOutput) outputDestination).getFile());
            } else {
                copyToDirectory(((PdfDirectoryOutput) outputDestination).getFile());
            }
        } else {
            throw new TaskIOException("Destination for the output handler has not been set.");
        }
    }

    /**
     * copy the input file (single file) to the output file
     * 
     * @param outputFile
     * @throws TaskIOException
     */
    private void copyToFile(File outputFile) throws TaskIOException {
        if (outputFile.isFile()) {
            if (files.size() == 0) {
                for (Entry<String, File> entry : files.entrySet()) {
                    copyFile(entry.getValue(), outputFile);
                }
            } else {
                throw new TaskIOException(String.format(
                        "Wrong input size %d, must be 1 to copy to the selected destination %s", files.size(),
                        outputFile));
            }
        } else {
            throw new TaskIOException(String.format("Wrong output destination %s, must be a file.", outputFile));
        }
    }

    /**
     * Copy the input files to the output directory
     * 
     * @param out
     * @throws TaskIOException
     */
    private void copyToDirectory(File outputDirectory) throws TaskIOException {
        if (outputDirectory.isDirectory()) {
            if (!outputDirectory.exists()) {
                outputDirectory.mkdirs();
            }
            for (Entry<String, File> entry : files.entrySet()) {
                copyFile(entry.getValue(), new File(outputDirectory, entry.getKey()));
            }
        } else {
            throw new TaskIOException(String.format("Wrong output destination %s, must be a directory.",
                    outputDirectory));
        }
    }

    /**
     * Copy the input file to the output file
     * 
     * @param input
     *            input file
     * @param out
     *            output file
     * @throws TaskIOException
     */
    private void copyFile(File input, File output) throws TaskIOException {
        if (!destination.isOverwrite() && output.exists()) {
            throw new TaskIOException(String.format(
                    "Unable to overwrite the output file %s with the input %s (overwrite is false)", input, output));
        }
        try {
            FileUtils.copyFile(input, output);
            if (!input.delete()) {
                input.deleteOnExit();
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
        /*
         * FileInputStream input = null; try { input = new FileInputStream(inputFile); IOUtils.copyLarge(input, out); } catch (IOException e) { throw new
         * TaskIOException("Unable to copy the input file to the output stream", e); } finally { IOUtils.closeQuietly(input); }
         */
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

}
