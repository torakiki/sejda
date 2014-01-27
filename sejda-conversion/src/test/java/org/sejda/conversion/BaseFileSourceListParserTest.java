/*
 * Created on 27/gen/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.sejda.model.exception.SejdaRuntimeException;

/**
 * @author Andrea Vacondio
 * 
 */
public class BaseFileSourceListParserTest {
    File xmlFile;
    File csvFile;
    File emptyFile;

    @Before
    public void setUp() throws IOException {
        xmlFile = new File("/tmp/merge-filelist-config.xml");
        xmlFile.deleteOnExit();
        csvFile = new File("/tmp/merge-list.csv");
        csvFile.deleteOnExit();
        emptyFile = File.createTempFile("test", "txt");
        emptyFile.deleteOnExit();
        try {
            FileUtils.copyInputStreamToFile(getClass().getResourceAsStream("/merge-filelist-config.xml"), xmlFile);
            FileUtils.copyInputStreamToFile(getClass().getResourceAsStream("/merge-list.csv"), csvFile);
        } catch (IOException e) {
            throw new SejdaRuntimeException("Can't create test file. Reason: " + e.getMessage(), e);
        }
    }

    @After
    public void tearDown() {
        if (xmlFile != null) {
            xmlFile.delete();
        }
        if (csvFile != null) {
            csvFile.delete();
        }
        if (emptyFile != null) {
            emptyFile.delete();
        }

    }
}
