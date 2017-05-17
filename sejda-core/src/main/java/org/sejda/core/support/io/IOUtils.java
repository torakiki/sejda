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

import static org.apache.commons.lang3.SystemUtils.IS_OS_UNIX;
import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;
import static org.apache.commons.lang3.SystemUtils.JAVA_IO_TMPDIR;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.exception.TaskOutputVisitException;
import org.sejda.model.output.DirectoryTaskOutput;
import org.sejda.model.output.FileOrDirectoryTaskOutput;
import org.sejda.model.output.FileTaskOutput;
import org.sejda.model.output.TaskOutput;
import org.sejda.model.output.TaskOutputDispatcher;
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

    private static final String BUFFER_NAME = "sejdaTmp";

    /**
     * Creates a temp file trying to find the best location based on the task output.
     * 
     * @param taskOut
     * @return
     * @throws TaskIOException
     */
    public static File createTemporaryBuffer(TaskOutput taskOut) throws TaskIOException {
        TmpBufferLocationFinder bufferLocationFinder = new TmpBufferLocationFinder();
        try {
            taskOut.accept(bufferLocationFinder);
            File buffer = tmpFile(bufferLocationFinder.bufferLocation).toFile();
            buffer.deleteOnExit();
            return buffer;
        } catch (TaskOutputVisitException | IOException e) {
            throw new TaskIOException("Unable to create temporary buffer", e);
        }
    }

    private static Path tmpFile(Path location) throws IOException {
        if (IS_OS_UNIX) {
            return Files.createTempFile(location, "." + BUFFER_NAME, null);
        }
        return hide(Files.createTempFile(location, BUFFER_NAME, null));
    }

    /**
     * Sets the hidden attribute to true on Win platform
     * 
     * @param path
     * @return
     * @throws IOException
     */
    public static Path hide(Path path) throws IOException {
        if (IS_OS_WINDOWS) {
            Files.setAttribute(path, "dos:hidden", Boolean.TRUE);
        }
        return path;
    }

    /**
     * Sets the hidden attribute to false on Win platform
     * 
     * @param path
     * @return
     * @throws IOException
     */
    public static Path unhide(Path path) throws IOException {
        if (IS_OS_WINDOWS) {
            Files.setAttribute(path, "dos:hidden", Boolean.FALSE);
        }
        return path;
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

    public static File createTemporaryBufferWithName(String filename) throws TaskIOException {
        try {
            File tmpDir = createTemporaryFolder();
            File buffer = new File(tmpDir, filename);
            boolean created = buffer.createNewFile();
            if(!created) throw new IOException("Could not create new file: " + buffer.getAbsolutePath());
            buffer.deleteOnExit();
            return buffer;
        } catch (IllegalStateException | IOException e) {
            throw new TaskIOException("Unable to create temporary buffer", e);
        }
    }

    private static final int TEMP_DIR_ATTEMPTS = 1000;

    public static File createTemporaryFolder() {
        File baseDir = SystemUtils.getJavaIoTmpDir();
        String baseName = new StringBuilder(BUFFER_NAME).append(System.currentTimeMillis()).append("-").toString();

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

    /**
     * Component trying to find the best location for the temporary buffer based on the task output location
     * 
     * @author Andrea Vacondio
     */
    private static class TmpBufferLocationFinder implements TaskOutputDispatcher {

        private Path bufferLocation = Paths.get(JAVA_IO_TMPDIR);

        @Override
        public void dispatch(FileTaskOutput output) {
            Path dest = output.getDestination().toPath().getParent();
            if (Files.exists(dest)) {
                bufferLocation = dest;
            }
        }

        @Override
        public void dispatch(DirectoryTaskOutput output) {
            Path dest = output.getDestination().toPath();
            if (Files.exists(dest)) {
                bufferLocation = dest;
            }
        }

        @Override
        public void dispatch(FileOrDirectoryTaskOutput output) {
            Path dest = output.getDestination().toPath();
            if (Files.exists(dest)) {
                if (Files.isDirectory(dest)) {
                    bufferLocation = dest;
                } else {
                    bufferLocation = dest.getParent();
                }
            }
        }
    }
}
