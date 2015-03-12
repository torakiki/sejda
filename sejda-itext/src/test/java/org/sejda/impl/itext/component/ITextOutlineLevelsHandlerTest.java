/*
 * Created on 07/ago/2011
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
package org.sejda.impl.itext.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.sejda.model.outline.OutlineLevelsHandler;

import com.lowagie.text.pdf.PdfReader;

/**
 * @author Andrea Vacondio
 * 
 */
public class ITextOutlineLevelsHandlerTest {
    @Test
    public void testPositiveGetOutlineMaxDepth() throws IOException {
        PdfReader reader = null;
        InputStream inputStream = null;
        try {
            inputStream = getClass().getClassLoader().getResourceAsStream("pdf/test_outline.pdf");
            reader = new PdfReader(inputStream);
            OutlineLevelsHandler victim = new ITextOutlineLevelsHandler(reader, null);
            assertEquals(3, victim.getMaxOutlineDepth());
        } finally {
            IOUtils.closeQuietly(inputStream);
            if (reader != null) {
                reader.close();
            }
        }
    }

    @Test
    public void testNegativeGetOutlineMaxDepth() throws IOException {
        PdfReader reader = null;
        InputStream inputStream = null;
        try {
            inputStream = getClass().getClassLoader().getResourceAsStream("pdf/test_no_outline.pdf");
            reader = new PdfReader(inputStream);
            OutlineLevelsHandler victim = new ITextOutlineLevelsHandler(reader, null);
            assertEquals(0, victim.getMaxOutlineDepth());
        } finally {
            IOUtils.closeQuietly(inputStream);
            if (reader != null) {
                reader.close();
            }
        }
    }

    @Test
    public void testGetPageNumbersAtOutlineLevel() throws IOException {
        PdfReader reader = null;
        InputStream inputStream = null;
        try {
            inputStream = getClass().getClassLoader().getResourceAsStream("pdf/test_outline.pdf");
            reader = new PdfReader(inputStream);
            OutlineLevelsHandler victim = new ITextOutlineLevelsHandler(reader, null);
            assertTrue(victim.getPageDestinationsForLevel(4).getPages().isEmpty());
            assertEquals(2, victim.getPageDestinationsForLevel(2).getPages().size());
            assertEquals(1, victim.getPageDestinationsForLevel(3).getPages().size());
        } finally {
            IOUtils.closeQuietly(inputStream);
            if (reader != null) {
                reader.close();
            }
        }
    }
}
