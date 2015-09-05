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
import java.util.Collections;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.sejda.common.collection.NullSafeSet;
import org.sejda.io.SeekableSources;
import org.sejda.model.outline.OutlinePolicy;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

/**
 * @author Andrea Vacondio
 *
 */
public class OutlineMergerTest {

    private PDDocument document;
    private Set<PDPage> relevant = new NullSafeSet<>();

    @Before
    public void setUp() throws IOException {
        document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(getClass().getClassLoader()
                .getResourceAsStream("pdf/large_outline.pdf")));
        for (PDPage current : document.getPages()) {
            relevant.add(current);
        }
    }

    @Test
    public void testEmpty() {
        OutlineMerger victim = new OutlineMerger(OutlinePolicy.DISCARD);
        victim.updateOutline(document, "large_outline.pdf", relevant);
        assertFalse(victim.hasOutline());
        assertFalse(victim.getOutline().hasChildren());
    }

    @Test
    public void testRetainAll() {
        OutlineMerger victim = new OutlineMerger(OutlinePolicy.RETAIN);
        victim.updateOutline(document, "large_outline.pdf", relevant);
        assertTrue(victim.hasOutline());
        assertEquals(count(document.getDocumentCatalog().getDocumentOutline()), count(victim.getOutline()));
    }

    @Test
    public void testOnePerDoc() {
        OutlineMerger victim = new OutlineMerger(OutlinePolicy.ONE_ENTRY_EACH_DOC);
        victim.updateOutline(document, "large_outline.pdf", relevant);
        assertTrue(victim.hasOutline());
        assertEquals(1, count(victim.getOutline()));
    }

    @Test
    public void testRetainSome() {
        OutlineMerger victim = new OutlineMerger(OutlinePolicy.RETAIN);
        relevant.clear();
        relevant.add(document.getPage(2));
        relevant.add(document.getPage(3));
        relevant.add(document.getPage(4));
        victim.updateOutline(document, "large_outline.pdf", relevant);
        assertTrue(victim.hasOutline());
        assertEquals(28, count(victim.getOutline()));
    }

    @Test
    public void testOnePerDocNoName() {
        OutlineMerger victim = new OutlineMerger(OutlinePolicy.ONE_ENTRY_EACH_DOC);
        victim.updateOutline(document, "", relevant);
        assertFalse(victim.hasOutline());
    }

    @Test
    public void testRetainAllNoRelevantPage() {
        OutlineMerger victim = new OutlineMerger(OutlinePolicy.RETAIN);
        victim.updateOutline(document, "large_outline.pdf", Collections.emptySet());
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
