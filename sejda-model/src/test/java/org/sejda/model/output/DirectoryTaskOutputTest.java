/*
 * Created on 23/gen/2011
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
