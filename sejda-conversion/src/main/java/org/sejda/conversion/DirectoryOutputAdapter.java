/*
 * Created on Jul 1, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.conversion;

import java.io.File;

import org.sejda.model.output.DirectoryTaskOutput;

/**
 * Adapter for {@link DirectoryTaskOutput}. Main role is to be a string-based constructor for the underlying model object
 * 
 * @author Eduard Weissmann
 * 
 */
public class DirectoryOutputAdapter {

    private final DirectoryTaskOutput pdfDirectoryOutput;

    public DirectoryOutputAdapter(String directoryPath) {
        File directory = new File(directoryPath);

        if (!directory.exists()) {
            throw new IllegalArgumentException("Path '" + directoryPath + "' does not exist");
        }
        
        this.pdfDirectoryOutput = new DirectoryTaskOutput(directory);
    }

    /**
     * @return the pdfDirectoryOutput
     */
    public DirectoryTaskOutput getPdfDirectoryOutput() {
        return pdfDirectoryOutput;
    }
}
