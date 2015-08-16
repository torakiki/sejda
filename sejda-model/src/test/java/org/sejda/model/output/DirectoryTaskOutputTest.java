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

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.sejda.TestUtils;

/**
 * @author Andrea Vacondio
 * 
 */
public class DirectoryTaskOutputTest {

    private File directory;

    @Before
    public void setUp() {
        directory = mock(File.class);
        when(directory.isDirectory()).thenReturn(Boolean.TRUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullFile() {
        new DirectoryTaskOutput(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidDirectory() {
        when(directory.isDirectory()).thenReturn(Boolean.FALSE);
        new DirectoryTaskOutput(directory);
    }

    @Test
    public void testValidDirectory() {
        DirectoryTaskOutput instance = new DirectoryTaskOutput(directory);
        assertNotNull(instance);
    }

    @Test
    public void testEquals() {
        File diffDirectory = mock(File.class);
        when(diffDirectory.isDirectory()).thenReturn(Boolean.TRUE);
        DirectoryTaskOutput eq1 = new DirectoryTaskOutput(directory);
        DirectoryTaskOutput eq2 = new DirectoryTaskOutput(directory);
        DirectoryTaskOutput eq3 = new DirectoryTaskOutput(directory);
        DirectoryTaskOutput diff = new DirectoryTaskOutput(diffDirectory);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }
}
