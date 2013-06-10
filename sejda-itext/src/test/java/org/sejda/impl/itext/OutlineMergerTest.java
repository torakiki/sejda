/*
 * Created on 10/giu/2013
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
package org.sejda.impl.itext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.AbstractPdfSource;
import org.sejda.model.input.PdfMergeInput;
import org.sejda.model.outline.OutlinePolicy;
import org.sejda.model.pdf.page.PageRange;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.SimpleBookmark;

/**
 * @author Andrea Vacondio
 * 
 */
public class OutlineMergerTest {

    private PdfReader reader;
    private PdfMergeInput input;

    @Before
    public void setUp() throws IOException {
        reader = new PdfReader(getClass().getClassLoader().getResourceAsStream("pdf/large_outline.pdf"));
        AbstractPdfSource<?> source = mock(AbstractPdfSource.class);
        input = new PdfMergeInput(source);
    }

    @Test
    public void testEmpty() throws TaskException {
        OutlineMerger victim = new OutlineMerger(OutlinePolicy.DISCARD);
        victim.updateOutline(reader, input, 0);
        assertTrue(victim.getOutline().isEmpty());
    }

    @Test
    public void testRetainAll() throws TaskException {
        OutlineMerger victim = new OutlineMerger(OutlinePolicy.RETAIN);
        victim.updateOutline(reader, input, 0);
        assertEquals(SimpleBookmark.getBookmark(reader).size(), victim.getOutline().size());
    }

    @Test
    public void testRetainSome() throws TaskException {
        input.addPageRange(new PageRange(3, 5));
        OutlineMerger victim = new OutlineMerger(OutlinePolicy.RETAIN);
        victim.updateOutline(reader, input, 0);
        assertEquals(28, victim.getOutline().size());
    }
}
