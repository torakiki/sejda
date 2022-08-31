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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Andrea Vacondio
 */
@Disabled
public class BaseFileSourceListParserTest {
    @TempDir
    public Path folder;

    Path xmlFile;
    Path csvFile;
    Path emptyFile;

    @BeforeEach
    public void setUp() throws IOException {
        xmlFile = folder.resolve("merge-filelist-config.xml");
        csvFile = folder.resolve("merge-list.csv");
        emptyFile = folder.resolve("test.txt");
        Files.copy(getClass().getResourceAsStream("/merge-filelist-config.xml"), xmlFile);
        Files.copy(getClass().getResourceAsStream("/merge-list.csv"), csvFile);
    }

}
