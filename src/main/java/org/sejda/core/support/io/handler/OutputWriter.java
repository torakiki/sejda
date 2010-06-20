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
package org.sejda.core.support.io.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.sejda.core.exception.TaskIOException;
import org.sejda.core.manipulation.model.output.AbstractPdfOutput;
import org.sejda.core.manipulation.model.output.OutputType;
import org.sejda.core.manipulation.model.output.PdfDirectoryOutput;
import org.sejda.core.manipulation.model.output.PdfFileOutput;
import org.sejda.core.manipulation.model.output.PdfStreamOutput;

/**
 * Component responsible for writing the input files to the output destination
 * 
 * @author Andrea Vacondio
 * 
 */
public class OutputWriter {

    private static final Logger LOG = Logger.getLogger(OutputWriter.class.getPackage().getName());

    public void executeCopy(Map<String, File> files, Destination destination) throws TaskIOException {
        if (destination != null) {
            AbstractPdfOutput outputDestination = destination.getOutputDestination();
            OutputType type = destination.getOutputDestination().getOutputType();
            if (OutputType.STREAM_OUTPUT.equals(type)) {
                copyToStream(files, ((PdfStreamOutput) outputDestination).getStream());
            } else if (OutputType.FILE_OUTPUT.equals(type)) {
                copyToFile(files, ((PdfFileOutput) outputDestination).getFile(), destination.isOverwrite());
            } else {
                copyToDirectory(files, ((PdfDirectoryOutput) outputDestination).getFile(), destination.isOverwrite());
            }
        } else {
            throw new TaskIOException("Destination for the output handler has not been set.");
        }
    }

    /**
     * copy the input file contained in the input map (single file) to the output file
     * 
     * @param files
     * @param outputFile
     * @param overwrite
     *            true to overwrite if already exists
     * @throws TaskIOException
     */
    private void copyToFile(Map<String, File> files, File outputFile, boolean overwrite) throws TaskIOException {
        if (outputFile.isFile()) {
            if (files.size() == 1) {
                for (Entry<String, File> entry : files.entrySet()) {
                    copyFile(entry.getValue(), outputFile, overwrite);
                }
            } else {
                throw new TaskIOException(String.format(
                        "Wrong files map size %d, must be 1 to copy to the selected destination %s", files.size(),
                        outputFile));
            }
        } else {
            throw new TaskIOException(String.format("Wrong output destination %s, must be a file.", outputFile));
        }
    }

    /**
     * Copy the input files to the output directory
     * 
     * @param files
     * @param out
     * @param overwrite
     *            true to overwrite if already exists
     * @throws TaskIOException
     */
    private void copyToDirectory(Map<String, File> files, File outputDirectory, boolean overwrite)
            throws TaskIOException {
        if (outputDirectory.isDirectory()) {
            if (!outputDirectory.exists()) {
                if (!outputDirectory.mkdirs()) {
                    throw new TaskIOException(String.format("Unable to make destination directory tree %s.",
                            outputDirectory));
                }
            }
            for (Entry<String, File> entry : files.entrySet()) {
                copyFile(entry.getValue(), new File(outputDirectory, entry.getKey()), overwrite);
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
     * @param overwrite
     *            true to overwrite if already exists
     * @throws TaskIOException
     */
    private void copyFile(File input, File output, boolean overwrite) throws TaskIOException {
        if (!overwrite && output.exists()) {
            throw new TaskIOException(String.format(
                    "Unable to overwrite the output file %s with the input %s (overwrite is false)", input, output));
        }
        try {
            LOG.debug(String.format("Copying %s to %s.", input, output));
            FileUtils.copyFile(input, output);
        } catch (IOException e) {
            throw new TaskIOException("Unable to copy the input file to the output file", e);
        } finally {
            delete(input);
        }
    }

    /**
     * Copy the populated file map to a zip output stream
     * 
     * @param files
     * @param out
     * @throws TaskIOException
     */
    private void copyToStream(Map<String, File> files, OutputStream out) throws TaskIOException {
        ZipOutputStream zipOut = new ZipOutputStream(out);
        for (Entry<String, File> entry : files.entrySet()) {
            FileInputStream input = null;
            try {
                input = new FileInputStream(entry.getValue());
                zipOut.putNextEntry(new ZipEntry(entry.getKey()));
                LOG.debug(String.format("Copying %s to zip stream.", entry.getValue()));
                IOUtils.copy(input, zipOut);
            } catch (IOException e) {
                throw new TaskIOException("Unable to copy the temporary file to the zip output stream", e);
            } finally {
                IOUtils.closeQuietly(input);
                delete(entry.getValue());
            }
        }
    }

    private void delete(File file) {
        if (!file.delete()) {
            LOG.warn(String.format("Unable to delete temporary file %s", file));
        }
    }
}
