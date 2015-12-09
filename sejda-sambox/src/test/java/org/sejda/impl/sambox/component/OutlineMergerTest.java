/*
 * Created on 05/set/2015
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sejda.common.LookupTable;
import org.sejda.io.SeekableSources;
import org.sejda.model.outline.OutlinePolicy;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.sejda.util.IOUtils;

/**
 * @author Andrea Vacondio
 *
 */
public class OutlineMergerTest {

    private PDDocument document;
    private PDDocument document2;
    private LookupTable<PDPage> mapping = new LookupTable<>();
    private LookupTable<PDPage> mapping2 = new LookupTable<>();

    @Before
    public void setUp() throws IOException {
        document = PDFParser.parse(SeekableSources
                .inMemorySeekableSourceFrom(getClass().getClassLoader().getResourceAsStream("pdf/large_outline.pdf")));
        for (PDPage current : document.getPages()) {
            mapping.addLookupEntry(current, new PDPage());
        }

        document2 = PDFParser.parse(SeekableSources
                .inMemorySeekableSourceFrom(getClass().getClassLoader().getResourceAsStream("pdf/test_outline.pdf")));
        for (PDPage current : document2.getPages()) {
            mapping2.addLookupEntry(current, new PDPage());
        }
    }

    @After
    public void tearDown() {
        IOUtils.closeQuietly(document);
        IOUtils.closeQuietly(document2);
    }

    @Test
    public void empty() {
        OutlineMerger victim = new OutlineMerger(OutlinePolicy.DISCARD);
        victim.updateOutline(document, "large_outline.pdf", mapping);
        assertFalse(victim.hasOutline());
        assertFalse(victim.getOutline().hasChildren());
    }

    @Test
    public void retainAll() {
        OutlineMerger victim = new OutlineMerger(OutlinePolicy.RETAIN);
        victim.updateOutline(document, "large_outline.pdf", mapping);
        assertTrue(victim.hasOutline());
        assertEquals(count(document.getDocumentCatalog().getDocumentOutline()), count(victim.getOutline()));
    }

    @Test
    public void onePerDoc() {
        OutlineMerger victim = new OutlineMerger(OutlinePolicy.ONE_ENTRY_EACH_DOC);
        victim.updateOutline(document, "large_outline.pdf", mapping);
        victim.updateOutline(document2, "test_outline.pdf", mapping2);
        assertTrue(victim.hasOutline());
        assertEquals(2, count(victim.getOutline()));
        for (PDOutlineItem current : victim.getOutline().children()) {
            assertFalse(current.hasChildren());
        }
    }

    @Test
    public void retainAsOneEntry() {
        OutlineMerger victim = new OutlineMerger(OutlinePolicy.RETAIN_AS_ONE_ENTRY);
        victim.updateOutline(document, "large_outline.pdf", mapping);
        victim.updateOutline(document2, "test_outline.pdf", mapping2);
        assertTrue(victim.hasOutline());
        assertEquals(2, count(victim.getOutline()));
        for (PDOutlineItem current : victim.getOutline().children()) {
            assertTrue(current.hasChildren());
        }
    }

    @Test
    public void retainSome() {
        OutlineMerger victim = new OutlineMerger(OutlinePolicy.RETAIN);
        mapping.clear();
        mapping.addLookupEntry(document.getPage(2), new PDPage());
        mapping.addLookupEntry(document.getPage(3), new PDPage());
        mapping.addLookupEntry(document.getPage(4), new PDPage());
        victim.updateOutline(document, "large_outline.pdf", mapping);
        assertTrue(victim.hasOutline());
        assertEquals(28, count(victim.getOutline()));
    }

    @Test
    public void onePerDocNoName() {
        OutlineMerger victim = new OutlineMerger(OutlinePolicy.ONE_ENTRY_EACH_DOC);
        victim.updateOutline(document, "", mapping);
        assertFalse(victim.hasOutline());
    }

    @Test
    public void retainAllNoRelevantPage() {
        OutlineMerger victim = new OutlineMerger(OutlinePolicy.RETAIN);
        victim.updateOutline(document, "large_outline.pdf", new LookupTable<>());
        assertFalse(victim.hasOutline());
    }

    private static int count(PDDocumentOutline outline) {
        int count = 0;
        for (PDOutlineItem item : outline.children()) {
            count++;
        }
        return count;
    }
}
