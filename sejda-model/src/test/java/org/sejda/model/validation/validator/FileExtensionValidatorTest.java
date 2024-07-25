/*
 * Created on 30/ago/2011
 * Copyright 2011 Sober Lemur S.r.l. and Sejda BV.
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
package org.sejda.model.validation.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sejda.model.SejdaFileExtensions;
import org.sejda.model.validation.constraint.FileExtension;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Andrea Vacondio
 * 
 */
public class FileExtensionValidatorTest {

    private File mockFile;
    private FileExtensionValidator victim = new FileExtensionValidator();

    @BeforeEach
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
