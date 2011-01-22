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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.sejda.core.validation.constraint.PdfFile;

/**
 * {@link File} output destination
 * 
 * @author Andrea Vacondio
 * 
 */
public class PdfFileOutput implements PdfOutput {

    @PdfFile
    private final File file;
    private final OutputType type;

    PdfFileOutput(File file, OutputType type) {
        this.file = file;
        this.type = type;
    }

    public File getFile() {
        return file;
    }

    public OutputType getOutputType() {
        return type;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(getOutputType()).append(file)
                .append(Integer.toHexString(System.identityHashCode(this))).toString();
    }

    /**
     * Creates a new instance of a PdfFileOutput using the input file
     * 
     * @param file
     * @return the newly created instance
     * @throws InvalidParameterException
     *             if the input file is null or not a file
     */
    public static PdfFileOutput newInstance(File file) {
        if (file == null || !file.isFile()) {
            throw new InvalidParameterException("A not null file instance is expected.");
        }
        return new PdfFileOutput(file, OutputType.FILE_OUTPUT);
    }

}
