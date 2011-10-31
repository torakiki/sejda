/*
 * Created on 30/ago/2011
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
import org.sejda.model.SejdaFileExtensions;
import org.sejda.model.validation.constraint.FileExtension;

/**
 * @author Andrea Vacondio
 * 
 */
public class FileExtensionValidatorTest {

    private File mockFile;
    private FileExtensionValidator victim = new FileExtensionValidator();

    @Before
    public void setUp() {
        mockFile = mock(File.class);
        when(mockFile.isFile()).thenReturn(Boolean.TRUE);
        FileExtension annotation = mock(FileExtension.class);
        when(annotation.value()).thenReturn(SejdaFileExtensions.PDF_EXTENSION);
        victim = new FileExtensionValidator();
        victim.initialize(annotation);
    }

    @Test
    public void testEmptyExtension() {
        when(mockFile.getName()).thenReturn("noExtension");
        assertFalse(victim.isValid(mockFile, null));
    }

    @Test
    public void testOnlyExtension() {
        when(mockFile.getName()).thenReturn(".pdf");
        assertFalse(victim.isValid(mockFile, null));
    }

    @Test
    public void testWrongExtension() {
        when(mockFile.getName()).thenReturn("Bla.txt");
        assertFalse(victim.isValid(mockFile, null));
    }

    @Test
    public void testCaseInsensitiveExtension() {
        when(mockFile.getName()).thenReturn("bla.PdF");
        assertTrue(victim.isValid(mockFile, null));
    }

    @Test
    public void testExtension() {
        when(mockFile.getName()).thenReturn("bla.pdf");
        assertTrue(victim.isValid(mockFile, null));
    }
}
