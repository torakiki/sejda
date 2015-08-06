/*
 * Created on 10/giu/2013
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.itext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        when(source.getName()).thenReturn("large_outline.pdf");
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
    public void testOnePerDoc() throws TaskException {
        OutlineMerger victim = new OutlineMerger(OutlinePolicy.ONE_ENTRY_EACH_DOC);
        victim.updateOutline(reader, input, 0);
        assertEquals(1, victim.getOutline().size());
    }

    @Test
    public void testRetainSome() throws TaskException {
        input.addPageRange(new PageRange(3, 5));
        OutlineMerger victim = new OutlineMerger(OutlinePolicy.RETAIN);
        victim.updateOutline(reader, input, 0);
        assertEquals(28, victim.getOutline().size());
    }
}
