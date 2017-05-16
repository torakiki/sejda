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
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 * @author Andrea Vacondio
 * 
 */
@Ignore
public class BaseFileSourceListParserTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    File xmlFile;
    File csvFile;
    File emptyFile;

    @Before
    public void setUp() throws IOException {
        xmlFile = folder.newFile("merge-filelist-config.xml");
        csvFile = folder.newFile("merge-list.csv");
        emptyFile = folder.newFile("test.txt");
        FileUtils.copyInputStreamToFile(getClass().getResourceAsStream("/merge-filelist-config.xml"), xmlFile);
        FileUtils.copyInputStreamToFile(getClass().getResourceAsStream("/merge-list.csv"), csvFile);
    }

}
