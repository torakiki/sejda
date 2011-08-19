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
package org.sejda.core.manipulation.model.output;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.sejda.core.TestUtils;

/**
 * @author Andrea Vacondio
 * 
 */
public class DirectoryOutputTest {

    private File directory;

    @Before
    public void setUp() {
        directory = mock(File.class);
        when(directory.isDirectory()).thenReturn(Boolean.TRUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullFile() {
        DirectoryOutput.newInstance(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidDirectory() {
        when(directory.isDirectory()).thenReturn(Boolean.FALSE);
        DirectoryOutput.newInstance(directory);
    }

    @Test
    public void testValidDirectory() {
        DirectoryOutput instance = DirectoryOutput.newInstance(directory);
        assertNotNull(instance);
    }

    @Test
    public void testEquals() {
        File diffDirectory = mock(File.class);
        when(diffDirectory.isDirectory()).thenReturn(Boolean.TRUE);
        DirectoryOutput eq1 = DirectoryOutput.newInstance(directory);
        DirectoryOutput eq2 = DirectoryOutput.newInstance(directory);
        DirectoryOutput eq3 = DirectoryOutput.newInstance(directory);
        DirectoryOutput diff = DirectoryOutput.newInstance(diffDirectory);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }
}
