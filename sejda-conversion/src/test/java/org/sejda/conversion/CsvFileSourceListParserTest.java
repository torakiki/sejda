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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Andrea Vacondio
 */
public class CsvFileSourceListParserTest {
    private final CsvFileSourceListParser victim = new CsvFileSourceListParser();

    @Test
    public void parseFileNames(@TempDir Path folder) throws IOException {
        var csvFile = folder.resolve("merge-list.csv");
        Files.copy(getClass().getClassLoader().getResourceAsStream("merge-list.csv"), csvFile);
        List<String> result = victim.parseFileNames(csvFile.toFile());
        assertThat(result, hasItem("/another/second.pdf"));
        assertThat(result, hasItem("/my/path/first.pdf"));
    }
}
