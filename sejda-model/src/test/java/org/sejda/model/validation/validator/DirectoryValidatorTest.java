/*
 * Created on 19/ott/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.validation.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Andrea Vacondio
 * 
 */
public class DirectoryValidatorTest {

    private File mockDir;
    private DirectoryValidator victim = new DirectoryValidator();

    @Before
    public void setUp() {
        mockDir = mock(File.class);
        when(mockDir.isDirectory()).thenReturn(Boolean.TRUE);
    }

    @Test
    public void testNull() {
        assertTrue(victim.isValid(null, null));
    }

    @Test
    public void testPositive() {
        assertTrue(victim.isValid(mockDir, null));
    }

    @Test
    public void testNegative() {
        when(mockDir.isDirectory()).thenReturn(Boolean.FALSE);
        assertFalse(victim.isValid(mockDir, null));
    }
}
