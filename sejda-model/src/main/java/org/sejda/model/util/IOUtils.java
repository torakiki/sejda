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
package org.sejda.model.util;

import static java.util.Objects.nonNull;
import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.SystemUtils.IS_OS_MAC;
import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;
import static org.apache.commons.lang3.SystemUtils.JAVA_IO_TMPDIR;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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

    public static final String TMP_BUFFER_PREFIX_PROPERTY_NAME = "sejda.tmp.buffer.prefix";

    private static final Logger LOG = LoggerFactory.getLogger(IOUtils.class);

    private IOUtils() {
        // hide
    }

    static final String BUFFER_NAME = "sejdaTmp";

    /**
     * Creates a temp file trying to find the best location based on the task output.
     * 
     * @param taskOut
     * @return the created temporary {@link File}
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
            // sometimes the above fails, eg: java.nio.file.AccessDeniedException: C:\\Users\\edi\\OneDrive\\Docs\\.sejdaTmp123124312312312.tmp
            // so try again this time in the temp dir
            try {
                return createTemporaryBuffer();
            } catch (TaskIOException ex) {
                throw new TaskIOException("Unable to create temporary buffer", ex);
            }
        }
    }

    private static Path tmpFile(Path location) throws IOException {
        // don't add leading dot on Windows
        String prefix = (IS_OS_WINDOWS ? "" : ".") + System.getProperty(TMP_BUFFER_PREFIX_PROPERTY_NAME, BUFFER_NAME);
        return Files.createTempFile(location, prefix, null);
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
            File buffer = File.createTempFile(System.getProperty(TMP_BUFFER_PREFIX_PROPERTY_NAME, BUFFER_NAME),
                    extension);
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
            if (!created)
                throw new IOException("Could not create new file: " + buffer.getAbsolutePath());
            buffer.deleteOnExit();
            return buffer;
        } catch (IllegalStateException | IOException e) {
            throw new TaskIOException("Unable to create temporary buffer", e);
        }
    }

    private static final int TEMP_DIR_ATTEMPTS = 1000;

    public static File createTemporaryFolder() {
        File baseDir = SystemUtils.getJavaIoTmpDir();
        String baseName =
                System.getProperty(TMP_BUFFER_PREFIX_PROPERTY_NAME, BUFFER_NAME) + System.currentTimeMillis() + "-";

        for (int counter = 0; counter < TEMP_DIR_ATTEMPTS; counter++) {
            File tempDir = new File(baseDir, baseName + counter);
            if (tempDir.mkdir()) {
                return tempDir;
            }
        }
        throw new IllegalStateException(
                "Failed to create directory within " + TEMP_DIR_ATTEMPTS + " attempts (tried " + baseName + "0 to "
                        + baseName + (TEMP_DIR_ATTEMPTS - 1) + ')');
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

    public static String shortenFilename(String name) {
        if (IS_OS_WINDOWS || IS_OS_MAC) {
            // char based max length
            return shortenFilenameCharLength(name, 255);
        }
        // bytes based max length
        return shortenFilenameBytesLength(name, 254, StandardCharsets.UTF_8);
    }

    /**
     * @return A {@link String} where all the variations of whitespace and horizontal whitespace are replace with a standard whitespace, all characters deemed unsafe for a filename
     *         are stripped and the resulting filename is trimmed
     */
    public static String toSafeFilename(String input) {
        // TODO maybe we should do the trimming when we ensure a proper file name length to make sure we do it for every generated file? Or we shouldn't do it at all?
        return defaultString(input).replaceAll("\\s|\\h", " ").replaceAll("[\0\f\t\n\r\\\\/:*?\\\"<>|]", "").trim();
    }

    /**
     * Strips all but characters that are known to be safe: alphanumerics for now.
     */
    public static String toStrictFilename(String input) {
        int maxLength = 251; // leave room for extension ".pdf"
        String safe = defaultIfBlank(input, "").replaceAll("[^A-Za-z0-9_ .-]", "");
        if (safe.length() > maxLength) {
            safe = safe.substring(0, maxLength);
        }
        return safe;
    }

    static String shortenFilenameCharLength(String input, int maxCharLength) {
        if (input.length() > maxCharLength) {
            String baseName = getBaseName(input);
            String ext = getExtension(input);

            baseName = baseName.substring(0, maxCharLength - 1 - ext.length());
            return String.format("%s.%s", baseName, ext);
        }
        return input;

    }

    static String shortenFilenameBytesLength(String input, int maxBytesLength, Charset charset) {
        if (input.getBytes(charset).length > maxBytesLength) {
            String baseName = getBaseName(input);
            String ext = getExtension(input);

            // drop last char from basename, try again
            baseName = baseName.substring(0, baseName.length() - 1);
            String shorterFilename = String.format("%s.%s", baseName, ext);

            while (shorterFilename.getBytes(charset).length > maxBytesLength) {
                baseName = baseName.substring(0, baseName.length() - 1);
                shorterFilename = String.format("%s.%s", baseName, ext);
            }

            return shorterFilename;
        }
        return input;
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
            if (nonNull(dest) && Files.exists(dest)) {
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
