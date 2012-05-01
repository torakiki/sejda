/*
 * Created on 19/giu/2010
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
package org.sejda.core.support.io;

import static org.apache.commons.lang3.StringUtils.isBlank;

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
     * copy the input file contained in the input map (single file) to the output file
     * 
     * @param files
     * @param outputFile
     * @param overwrite
     *            true to overwrite if already exists
     * @throws IOException
     */
    static void copyToFile(Map<String, File> files, File outputFile, boolean overwrite) throws IOException {
        if (outputFile.exists() && !outputFile.isFile()) {
            throw new IOException(String.format("Wrong output destination %s, must be a file.", outputFile));
        }
        if (files.size() != 1) {
            throw new IOException(String.format(
                    "Wrong files map size %d, must be 1 to copy to the selected destination %s", files.size(),
                    outputFile));
        }
        for (Entry<String, File> entry : files.entrySet()) {
            copyFile(entry.getValue(), outputFile, overwrite);
        }

    }

    /**
     * Copy the input files to the output directory
     * 
     * @param files
     * @param outputDirectory
     * @param overwrite
     *            true to overwrite if already exists
     * @throws IOException
     */
    static void copyToDirectory(Map<String, File> files, File outputDirectory, boolean overwrite) throws IOException {
        if (!outputDirectory.isDirectory()) {
            throw new IOException(String.format("Wrong output destination %s, must be a directory.", outputDirectory));
        }
        if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
            throw new IOException(String.format("Unable to make destination directory tree %s.", outputDirectory));
        }
        for (Entry<String, File> entry : files.entrySet()) {
            if (isBlank(entry.getKey())) {
                throw new IOException(String.format(
                        "Unable to copy %s to the output directory, no output name specified.", entry.getValue()));
            }
            copyFile(entry.getValue(), new File(outputDirectory, entry.getKey()), overwrite);
        }
    }

    /**
     * Copy the input file to the output file
     * 
     * @param input
     *            input file
     * @param output
     *            output file
     * @param overwrite
     *            true to overwrite if already exists
     * @throws IOException
     */
    private static void copyFile(File input, File output, boolean overwrite) throws IOException {
        if (!overwrite && output.exists()) {
            throw new IOException(String.format(
                    "Unable to overwrite the output file %s with the input %s (overwrite is false)", output, input));
        }
        try {
            LOG.debug("Copying {} to {}.", input, output);
            FileUtils.copyFile(input, output);
        } finally {
            delete(input);
        }
    }

    /**
     * Copy the populated file map to a zip output stream
     * 
     * @param files
     * @param out
     * @throws IOException
     */
    static void copyToStream(Map<String, File> files, OutputStream out) throws IOException {
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

    private static void delete(File file) {
        if (!file.delete()) {
            LOG.warn("Unable to delete temporary file {}", file);
        }
    }
}
