/*
 * Created on 19/giu/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.core.support.io;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.*;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class responsible for writing the input files to the output destination
 * 
 * @author Andrea Vacondio
 * 
 */
final class OutputWriterHelper {

    private static final Logger LOG = LoggerFactory.getLogger(OutputWriterHelper.class);

    private OutputWriterHelper() {
        // util class
    }

    /**
     * Moves the input file contained in the input map (single file) to the output file
     * 
     * @param files
     * @param outputFile
     * @param overwrite
     *            true to overwrite if already exists
     * @throws IOException
     */
    static void moveToFile(Map<String, File> files, File outputFile, boolean overwrite) throws IOException {
        if (outputFile.exists() && !outputFile.isFile()) {
            throw new IOException(String.format("Wrong output destination %s, must be a file.", outputFile));
        }
        if (files.size() != 1) {
            throw new IOException(String.format(
                    "Wrong files map size %d, must be 1 to copy to the selected destination %s", files.size(),
                    outputFile));
        }
        for (Entry<String, File> entry : files.entrySet()) {
            moveFile(entry.getValue(), outputFile, overwrite);
        }

    }

    /**
     * Moves the input files to the output directory
     * 
     * @param files
     * @param outputDirectory
     * @param overwrite
     *            true to overwrite if already exists
     * @throws IOException
     */
    static void moveToDirectory(Map<String, File> files, File outputDirectory, boolean overwrite) throws IOException {
        if (!outputDirectory.isDirectory()) {
            throw new IOException(String.format("Wrong output destination %s, must be a directory.", outputDirectory));
        }
        if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
            throw new IOException(String.format("Unable to make destination directory tree %s.", outputDirectory));
        }
        for (Entry<String, File> entry : files.entrySet()) {
            if (isBlank(entry.getKey())) {
                throw new IOException(String.format(
                        "Unable to move %s to the output directory, no output name specified.", entry.getValue()));
            }
            moveFile(entry.getValue(), new File(outputDirectory, entry.getKey()), overwrite);
        }
    }

    /**
     * Moves the input file to the output file
     * 
     * @param input
     *            input file
     * @param output
     *            output file
     * @param overwrite
     *            true to overwrite if already exists
     * @throws IOException
     */
    private static void moveFile(File input, File output, boolean overwrite) throws IOException {
        if (!overwrite && output.exists()) {
            throw new IOException(String.format(
                    "Unable to overwrite the output file %s with the input %s (overwrite is false)", output, input));
        }

        LOG.debug("Moving {} to {}.", input, output);
        if(overwrite && output.exists()) {
            FileUtils.deleteQuietly(output);
        }
        FileUtils.moveFile(input, output);
    }

    /**
     * Copy the populated file map to a zip output stream
     * 
     * @param files
     * @param out
     * @throws IOException
     */
    static void copyToStreamZipped(Map<String, File> files, OutputStream out) throws IOException {
        ZipOutputStream zipOut = new ZipOutputStream(out);
        for (Entry<String, File> entry : files.entrySet()) {
            FileInputStream input = null;
            if (isBlank(entry.getKey())) {
                throw new IOException(String.format(
                        "Unable to copy %s to the output stream, no output name specified.", entry.getValue()));
            }
            try {
                input = new FileInputStream(entry.getValue());
                zipOut.putNextEntry(new ZipEntry(entry.getKey()));
                LOG.debug("Copying {} to zip stream {}.", entry.getValue(), entry.getKey());
                IOUtils.copy(input, zipOut);
            } finally {
                IOUtils.closeQuietly(input);
                delete(entry.getValue());
            }
        }
        IOUtils.closeQuietly(zipOut);
    }

    /**
     * Copies the contents of the file to the specified outputstream, without zipping or applying any other changes.
     * @param file
     * @param out
     * @throws IOException
     */
    static void copyToStream(File file, OutputStream out) throws IOException {
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            IOUtils.copy(in, out);
        } finally {
            IOUtils.closeQuietly(in);
            delete(file);
        }
    }

    private static void delete(File file) {
        if (!file.delete()) {
            LOG.warn("Unable to delete temporary file {}", file);
        }
    }
}
