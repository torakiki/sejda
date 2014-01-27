/*
 * Created on 27/gen/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.conversion;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sejda.conversion.exception.ConversionException;

/**
 * @author Andrea Vacondio
 * 
 */
public class PdfFileSourceListAdapterTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private File path;

    @Before
    public void setUp() throws IOException {
        folder.newFile("test_file.pdf");
        folder.newFile("test2_file.pdf");
        folder.newFile("another_test_file.pdf");
        folder.newFile("ignore_this.something");
        this.path = folder.getRoot();
    }

    @Test(expected = ConversionException.class)
    public void testNegative() {
        new PdfFileSourceListAdapter("/I/dont/exist");
    }

    @Test
    public void testPositive() {
        PdfFileSourceListAdapter victim = new PdfFileSourceListAdapter(path.getAbsolutePath());
        assertEquals(3, victim.getFileSourceList().size());
    }

    @Test
    public void testRegex() {
        PdfFileSourceListAdapter victim = new PdfFileSourceListAdapter(path.getAbsolutePath()).filter("test(.*).pdf");
        assertEquals(2, victim.getFileSourceList().size());
    }

    @Test
    public void testEmptyRegex() {
        PdfFileSourceListAdapter victim = new PdfFileSourceListAdapter(path.getAbsolutePath()).filter("");
        assertEquals(3, victim.getFileSourceList().size());
    }

    @Test(expected = ConversionException.class)
    public void testNoFile() {
        PdfFileSourceListAdapter victim = new PdfFileSourceListAdapter(path.getAbsolutePath()).filter("NOMATCH");
        victim.getFileSourceList();
    }
}
