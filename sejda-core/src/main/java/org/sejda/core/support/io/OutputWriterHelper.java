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

import static java.util.Optional.of;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.sejda.core.support.io.IOUtils.findNewNameThatDoesNotExist;
import static org.sejda.core.support.io.IOUtils.shortenFilename;
import static org.sejda.model.output.ExistingOutputPolicy.FAIL;
import static org.sejda.model.output.ExistingOutputPolicy.SKIP;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.task.TaskExecutionContext;
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
     * @param existingOutputPolicy
     *            policy to use if an output that already exists is found
     * @param executionContext
     *            current execution context
     * @throws IOException
     */
    static void moveToFile(Map<String, File> files, File outputFile, ExistingOutputPolicy existingOutputPolicy,
            TaskExecutionContext executionContext) throws IOException {
        if (outputFile.exists() && !outputFile.isFile()) {
            throw new IOException(String.format("Wrong output destination %s, must be a file.", outputFile));
        }
        if (files.size() != 1) {
            throw new IOException(
                    String.format("Wrong files map size %d, must be 1 to copy to the selected destination %s",
                            files.size(), outputFile));
        }
        for (Entry<String, File> entry : files.entrySet()) {
            moveFile(entry.getValue(), outputFile, of(existingOutputPolicy).filter(p -> p != SKIP).orElseGet(() -> {
                LOG.debug("Cannot use {} output policy for single output, replaced with {}", SKIP, FAIL);
                return FAIL;
            }), executionContext);
        }
    }

    /**
     * Moves the input files to the output directory
     * 
     * @param files
     * @param outputDirectory
     * @param existingOutputPolicy
     *            policy to use if an output that already exists is found
     * @param executionContext
     *            current execution context
     * @throws IOException
     */
    static void moveToDirectory(Map<String, File> files, File outputDirectory,
            ExistingOutputPolicy existingOutputPolicy, TaskExecutionContext executionContext) throws IOException {
        if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
            throw new IOException(String.format("Unable to make destination directory tree %s.", outputDirectory));
        }
        if (!outputDirectory.isDirectory()) {
            throw new IOException(String.format("Wrong output destination %s, must be a directory.", outputDirectory));
        }
        for (Entry<String, File> entry : files.entrySet()) {
            if (isBlank(entry.getKey())) {
                throw new IOException(String.format(
                        "Unable to move %s to the output directory, no output name specified.", entry.getValue()));
            }
            moveFile(entry.getValue(), new File(outputDirectory, finalName(entry.getKey(), files.size())),
                    existingOutputPolicy,
                    executionContext);
        }
    }

    /**
     * Moves the input file to the output file
     * 
     * @param input
     *            input file
     * @param output
     *            output file
     * @param existingOutputPolicy
     *            policy to use if an output that already exists is found
     * @param executionContext
     * @throws IOException
     */
    static void moveFile(File input, File output, ExistingOutputPolicy existingOutputPolicy,
            TaskExecutionContext executionContext) throws IOException {
        if (output.exists()) {
            switch (existingOutputPolicy) {
            case OVERWRITE:
                LOG.debug("Moving {} to {}.", input, output);
                Files.move(input.toPath(), output.toPath(), StandardCopyOption.REPLACE_EXISTING);
                executionContext.notifiableTaskMetadata().addTaskOutput(output);
                break;
            case RENAME:
                File newNamedOutput = findNewNameThatDoesNotExist(output);
                LOG.debug("Output exists {}, will use new name {}.", output, newNamedOutput);
                doMoveFile(input, newNamedOutput);
                executionContext.notifiableTaskMetadata().addTaskOutput(newNamedOutput);
                break;
            case SKIP:
                LOG.info("Skipping already existing output file {}", output);
                break;
            default:
                throw new IOException(
                        String.format("Unable to write %s to the already existing file destination %s. (policy is %s)",
                                input, output, existingOutputPolicy));
            }
        } else {
            LOG.debug("Moving {} to {}.", input, output);
            doMoveFile(input, output);
            executionContext.notifiableTaskMetadata().addTaskOutput(output);
        }
    }

    private static void doMoveFile(File input, File output) throws IOException {
        try {
            FileUtils.moveFile(input, output);
        } catch (IOException ex) {
            if (ex.getMessage().contains("Failed to delete original file")) {
                // Don't crash the task because we have leftover temp files, just warn
                LOG.warn(ex.getMessage());
                input.deleteOnExit();
            } else {
                throw ex;
            }
        }
    }

    private static String finalName(String filename, int totalFilesNumber) {
        return shortenFilename(filename.replace("[TOTAL_FILESNUMBER]", Integer.toString(totalFilesNumber)));
    }

    /**
     * Copy the populated file map to a zip output stream
     * 
     * @param files
     * @param out
     * @throws IOException
     */
    static void copyToStreamZipped(Map<String, File> files, OutputStream out) throws IOException {
        try (ZipOutputStream zipOut = new ZipOutputStream(out)) {
            for (Entry<String, File> entry : files.entrySet()) {

                if (isBlank(entry.getKey())) {
                    throw new IOException(String.format(
                            "Unable to copy %s to the output stream, no output name specified.", entry.getValue()));
                }
                try (FileInputStream input = new FileInputStream(entry.getValue())) {
                    zipOut.putNextEntry(new ZipEntry(entry.getKey()));
                    LOG.debug("Copying {} to zip stream {}.", entry.getValue(), entry.getKey());
                    IOUtils.copy(input, zipOut);
                } finally {
                    delete(entry.getValue());
                }
            }
        }
    }

    /**
     * Copies the contents of the file to the specified outputstream, without zipping or applying any other changes.
     * 
     * @param file
     * @param out
     * @throws IOException
     */
    static void copyToStream(File file, OutputStream out) throws IOException {
        try (InputStream in = new FileInputStream(file)) {
            IOUtils.copy(in, out);
        } finally {
            delete(file);
        }
    }

    private static void delete(File file) {
        if (!file.delete()) {
            LOG.warn("Unable to delete temporary file {}", file);
        }
    }
}
