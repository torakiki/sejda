/*
 * Created on 30/mag/2010
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
package org.sejda.core.manipulation.model.output;

import java.io.File;
import java.security.InvalidParameterException;

/**
 * Directory output destination
 * 
 * @author Andrea Vacondio
 * 
 */
public final class PdfDirectoryOutput extends PdfFileOutput {

    /**
     * @param file
     * @param type
     */
    private PdfDirectoryOutput(File file, OutputType type) {
        super(file, type);
    }

    /**
     * Creates a new instance of a PdfOutput using the input directory
     * 
     * @param file
     * @return the newly created instance
     * @throws InvalidParameterException
     *             if the input file is null or not a directory
     */
    public static PdfDirectoryOutput newInstance(File file) {
        if (file == null || !file.isDirectory()) {
            throw new InvalidParameterException("A not null directory instance is expected.");
        }
        return new PdfDirectoryOutput(file, OutputType.DIRECTORY_OUTPUT);
    }
}
