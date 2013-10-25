/*
 * Created on 18/ott/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.sejda.model.exception.TaskIOException;

/**
 * Provides IO utility methods.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class IOUtils {

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

    private static File createTemporaryBuffer(String extension) throws TaskIOException {
        try {
            File buffer = File.createTempFile(BUFFER_NAME, extension);
            buffer.deleteOnExit();
            return buffer;
        } catch (IOException e) {
            throw new TaskIOException("Unable to create temporary buffer", e);
        }
    }

    public static File createTemporaryFolder() {
        File folder = new File(FileUtils.getTempDirectory(), "sejdaTmp" + new Date().getTime());
        folder.mkdirs();
        return folder;
    }
}
