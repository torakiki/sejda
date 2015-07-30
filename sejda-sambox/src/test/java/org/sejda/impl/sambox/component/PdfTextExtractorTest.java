/*
 * Created on 25/ago/2011
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
package org.sejda.impl.sambox.component;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.sejda.sambox.pdmodel.PDDocument;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.internal.matchers.Contains;
import org.mockito.internal.matchers.StartsWith;
import org.sejda.model.exception.TaskException;

/**
 * @author Andrea Vacondio
 * 
 */
public class PdfTextExtractorTest {

    @Rule
    public ExpectedException expected = ExpectedException.none();
    private PdfTextExtractor victim;
    private PDDocument doc;
    private File file;

    @Before
    public void setUp() throws TaskException {
        victim = new PdfTextExtractor("UTF-8");
        doc = mock(PDDocument.class);
        file = mock(File.class);
    }

    @Test
    public void testNullDocExtract() throws TaskException {
        expected.expectMessage(new Contains("Unable to extract text from a null document."));
        victim.extract(null, null);
    }

    @Test
    public void testNullFileExtract() throws TaskException {
        expected.expectMessage(new StartsWith("Cannot write extracted text"));
        victim.extract(doc, null);
    }

    @Test
    public void testNotFileExtract() throws TaskException {
        expected.expectMessage(new StartsWith("Cannot write extracted text"));
        when(file.isFile()).thenReturn(Boolean.FALSE);
        victim.extract(doc, file);
    }

    @Test
    public void testCannotWriteFileExtract() throws TaskException {
        expected.expectMessage(new StartsWith("Cannot write extracted text"));
        when(file.isFile()).thenReturn(Boolean.TRUE);
        when(file.canWrite()).thenReturn(Boolean.FALSE);
        victim.extract(doc, file);
    }
}
