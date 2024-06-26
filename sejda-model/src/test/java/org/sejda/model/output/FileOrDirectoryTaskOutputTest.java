/*
 * Copyright 2017 by Edi Weissmann (edi.weissmann@gmail.com).
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sejda.model.TestUtils;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileOrDirectoryTaskOutputTest {

    private File file;
    private File directory;

    @BeforeEach
    public void setUp() {
        file = mock(File.class);
        when(file.isFile()).thenReturn(Boolean.TRUE);
        when(file.exists()).thenReturn(Boolean.TRUE);
        directory = mock(File.class);
        when(directory.isDirectory()).thenReturn(Boolean.TRUE);
    }

    @Test
    public void testValidDirectory() {
        FileOrDirectoryTaskOutput instance = new FileOrDirectoryTaskOutput(directory);
        assertNotNull(instance);
    }

    @Test
    public void testValidFile_exists() {
        FileOrDirectoryTaskOutput instance = new FileOrDirectoryTaskOutput(file);
        assertNotNull(instance);
    }

    @Test
    public void testValidFile_doesntExist() {
        when(file.isFile()).thenReturn(Boolean.FALSE);
        when(file.exists()).thenReturn(Boolean.FALSE);

        FileOrDirectoryTaskOutput instance = new FileOrDirectoryTaskOutput(file);
        assertNotNull(instance);
    }

    @Test
    public void testNullFile() {
        assertThrows(IllegalArgumentException.class, () -> new FileOrDirectoryTaskOutput(null));
    }

    @Test
    public void fileFactoryNullFile() {
        assertThrows(IllegalArgumentException.class, () -> FileOrDirectoryTaskOutput.file(null));
    }

    @Test
    public void fileFactoryInvalidFile() {
        when(file.isFile()).thenReturn(Boolean.FALSE);
        assertThrows(IllegalArgumentException.class, () -> FileOrDirectoryTaskOutput.file(file));
    }

    @Test
    public void fileFactoryValidFile_doesntExist() {
        when(file.isFile()).thenReturn(Boolean.FALSE);
        when(file.exists()).thenReturn(Boolean.FALSE);

        FileOrDirectoryTaskOutput instance = FileOrDirectoryTaskOutput.file(file);
        assertNotNull(instance);
    }

    @Test
    public void dirFactoryNullFile() {
        assertThrows(IllegalArgumentException.class, () -> FileOrDirectoryTaskOutput.directory(null));
    }

    @Test
    public void dirFactoryInvalidDirectory() {
        when(directory.isDirectory()).thenReturn(Boolean.FALSE);
        assertThrows(IllegalArgumentException.class, () -> FileOrDirectoryTaskOutput.directory(directory));
    }

    @Test
    public void dirFactoryValidDirectory() {
        when(directory.isDirectory()).thenReturn(Boolean.TRUE);
        FileOrDirectoryTaskOutput instance = FileOrDirectoryTaskOutput.directory(directory);
        assertNotNull(instance);
    }

    @Test
    public void testEquals() {
        File diffFile = mock(File.class);
        when(diffFile.isFile()).thenReturn(Boolean.TRUE);
        FileOrDirectoryTaskOutput eq1 = new FileOrDirectoryTaskOutput(file);
        FileOrDirectoryTaskOutput eq2 = new FileOrDirectoryTaskOutput(file);
        FileOrDirectoryTaskOutput eq3 = new FileOrDirectoryTaskOutput(file);
        FileOrDirectoryTaskOutput diff = new FileOrDirectoryTaskOutput(diffFile);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }
}
