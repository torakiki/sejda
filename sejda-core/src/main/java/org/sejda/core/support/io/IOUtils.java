/*
 * Created on 18/ott/2011
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
package org.sejda.core.support.io;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.sejda.model.exception.TaskIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides IO utility methods.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class IOUtils {

    private static final Logger LOG = LoggerFactory.getLogger(IOUtils.class);

    private IOUtils() {
        // hide
    }

    private static final String BUFFER_NAME = "SejdaTmpBuffer";

    /**
     * @return a temporary pdf file
     * @throws TaskIOException
     */
    public static File createTemporaryPdfBuffer() throws TaskIOException {
        return createTemporaryBuffer(".pdf");
    }

    /**
     * @return a temporary file
     * @throws TaskIOException
     */
    public static File createTemporaryBuffer() throws TaskIOException {
        return createTemporaryBuffer(".tmp");
    }

    public static File createTemporaryBuffer(String extension) throws TaskIOException {
        try {
            File buffer = File.createTempFile(BUFFER_NAME, extension);
            buffer.deleteOnExit();
            return buffer;
        } catch (IOException e) {
            throw new TaskIOException("Unable to create temporary buffer", e);
        }
    }

    private static final int TEMP_DIR_ATTEMPTS = 1000;

    public static File createTemporaryFolder() {
        File baseDir = new File(System.getProperty("java.io.tmpdir"));
        String baseName = new StringBuilder("sejdaTmp").append(System.currentTimeMillis()).append("-").toString();

        for (int counter = 0; counter < TEMP_DIR_ATTEMPTS; counter++) {
            File tempDir = new File(baseDir, baseName + counter);
            if (tempDir.mkdir()) {
                return tempDir;
            }
        }
        throw new IllegalStateException("Failed to create directory within " + TEMP_DIR_ATTEMPTS + " attempts (tried "
                + baseName + "0 to " + baseName + (TEMP_DIR_ATTEMPTS - 1) + ')');
    }

    public static File findNewNameThatDoesNotExist(File output) throws IOException {
        File newNamedOutput;
        int count = 1;
        int maxTries = 100;
        String basename = FilenameUtils.getBaseName(output.getName());
        String extension = FilenameUtils.getExtension(output.getName());

        do {
            String newName = String.format("%s(%d).%s", basename, count, extension);
            newNamedOutput = new File(output.getParent(), newName);
            count++;
        } while (count < maxTries && newNamedOutput.exists());

        if (newNamedOutput.exists()) {
            LOG.warn("Unable to generate a new filename that does not exist, path was {}", output);
            throw new IOException(
                    String.format("Unable to generate a new filename that does not exist, path was %s", output));
        }

        return newNamedOutput;
    }
}
