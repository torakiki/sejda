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
public class FileTaskOutputTest {

    private File file;

    @Before
    public void setUp() {
        file = mock(File.class);
        when(file.isFile()).thenReturn(Boolean.TRUE);
        when(file.exists()).thenReturn(Boolean.TRUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullFile() {
        new FileTaskOutput(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidFile() {
        when(file.isFile()).thenReturn(Boolean.FALSE);
        new FileTaskOutput(file);
    }

    @Test
    public void testValidFile_exists() {
        FileTaskOutput instance = new FileTaskOutput(file);
        assertNotNull(instance);
    }

    @Test
    public void testValidFile_doesntExist() {
        when(file.isFile()).thenReturn(Boolean.FALSE);
        when(file.exists()).thenReturn(Boolean.FALSE);

        FileTaskOutput instance = new FileTaskOutput(file);
        assertNotNull(instance);
    }

    @Test
    public void testEquals() {
        File diffFile = mock(File.class);
        when(diffFile.isFile()).thenReturn(Boolean.TRUE);
        FileTaskOutput eq1 = new FileTaskOutput(file);
        FileTaskOutput eq2 = new FileTaskOutput(file);
        FileTaskOutput eq3 = new FileTaskOutput(file);
        FileTaskOutput diff = new FileTaskOutput(diffFile);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }
}
