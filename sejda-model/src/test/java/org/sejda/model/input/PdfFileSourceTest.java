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
package org.sejda.model.input;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Test;
import org.sejda.model.exception.TaskIOException;

/**
 * @author Andrea Vacondio
 * 
 */
public class PdfFileSourceTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNullFile() {
        PdfFileSource.newInstanceNoPassword(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDirectory() {
        File file = mock(File.class);
        when(file.isFile()).thenReturn(Boolean.FALSE);
        PdfFileSource.newInstanceNoPassword(file);
    }

    @Test
    public void testValidFile() {
        File file = mock(File.class);
        when(file.getName()).thenReturn("name");
        when(file.isFile()).thenReturn(Boolean.TRUE);
        PdfFileSource.newInstanceNoPassword(file);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testOpener() throws TaskIOException {
        PdfSourceOpener opener = mock(PdfSourceOpener.class);
        File file = mock(File.class);
        when(file.getName()).thenReturn("name");
        when(file.isFile()).thenReturn(Boolean.TRUE);
        PdfFileSource instance = PdfFileSource.newInstanceNoPassword(file);
        instance.open(opener);
        verify(opener).open(instance);
    }
}
