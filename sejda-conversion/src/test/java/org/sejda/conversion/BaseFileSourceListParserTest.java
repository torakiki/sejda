/*
 * Created on 27/gen/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.conversion;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.sejda.model.exception.SejdaRuntimeException;

/**
 * @author Andrea Vacondio
 * 
 */
@Ignore
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
