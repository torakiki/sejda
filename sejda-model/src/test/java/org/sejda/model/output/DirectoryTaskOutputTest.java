/*
 * Created on 23/gen/2011
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.output;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.sejda.tests.TestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrea Vacondio
 */
public class DirectoryTaskOutputTest {

    @TempDir
    public Path folder;

    @Test
    public void testNullFile() {
        assertThrows(IllegalArgumentException.class, () -> new DirectoryTaskOutput(null));
    }

    @Test
    public void testInvalidDirectory() {
        assertThrows(IllegalArgumentException.class,
                () -> new DirectoryTaskOutput(Files.createTempFile(folder, "sejda", "txt").toFile()));
    }

    @Test
    public void testValidDirectory() throws IOException {
        DirectoryTaskOutput instance = new DirectoryTaskOutput(Files.createTempDirectory(folder, "sejda").toFile());
        assertNotNull(instance);
    }

    @Test
    public void testValidNonExistingDirectory() {
        DirectoryTaskOutput instance = new DirectoryTaskOutput(new File("I dont exist"));
        assertNotNull(instance);
    }

    @Test
    public void testEquals() throws IOException {
        var directory = Files.createTempDirectory(folder, "sejda").toFile();
        var diffDirectory = Files.createTempDirectory(folder, "sejda").toFile();
        DirectoryTaskOutput eq1 = new DirectoryTaskOutput(directory);
        DirectoryTaskOutput eq2 = new DirectoryTaskOutput(directory);
        DirectoryTaskOutput eq3 = new DirectoryTaskOutput(directory);
        DirectoryTaskOutput diff = new DirectoryTaskOutput(diffDirectory);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }
}
