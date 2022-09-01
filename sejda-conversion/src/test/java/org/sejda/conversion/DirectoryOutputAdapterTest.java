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
import org.sejda.conversion.exception.ConversionException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrea Vacondio
 */
public class DirectoryOutputAdapterTest {

    @TempDir
    public Path folder;

    @Test
    public void testNegative() {
        assertThrows(ConversionException.class, () -> new DirectoryOutputAdapter("/I/dont/exist"));
    }

    @Test
    public void testFile() {
        assertThrows(ConversionException.class,
                () -> new DirectoryOutputAdapter(Files.createTempFile(folder, "sejda", ".txt").toString()));
    }

    @Test
    public void testPositive() throws IOException {
        assertNotNull(new DirectoryOutputAdapter(
                Files.createTempDirectory(folder, "a folder").toString()).getPdfDirectoryOutput());
    }
}
