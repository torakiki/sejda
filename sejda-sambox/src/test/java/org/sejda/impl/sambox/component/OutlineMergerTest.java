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
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sejda.commons.LookupTable;
import org.sejda.commons.util.IOUtils;
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

    private ImmutablePair<PDDocument, LookupTable<PDPage>> testData;
    private ImmutablePair<PDDocument, LookupTable<PDPage>> testData2;

    @Before
    public void setUp() throws IOException {
        testData = getTestData("pdf/large_outline.pdf");
        testData2 = getTestData("pdf/test_outline.pdf");
    }

    @After
    public void tearDown() {
        IOUtils.closeQuietly(testData.getKey());
        IOUtils.closeQuietly(testData2.getKey());
    }

    @Test
    public void empty() {
        OutlineMerger victim = new OutlineMerger(OutlinePolicy.DISCARD);
        victim.updateOutline(testData.getKey(), "large_outline.pdf", testData.getValue());
        assertFalse(victim.hasOutline());
        assertFalse(victim.getOutline().hasChildren());
    }

    @Test
    public void retainAll() {
        OutlineMerger victim = new OutlineMerger(OutlinePolicy.RETAIN);
        victim.updateOutline(testData.getKey(), "large_outline.pdf", testData.getValue());
        assertTrue(victim.hasOutline());
        assertEquals(count(testData.getKey().getDocumentCatalog().getDocumentOutline()), count(victim.getOutline()));
    }

    @Test
    public void onePerDoc() {
        OutlineMerger victim = new OutlineMerger(OutlinePolicy.ONE_ENTRY_EACH_DOC);
        victim.updateOutline(testData.getKey(), "large_outline.pdf", testData.getValue());
        victim.updateOutline(testData2.getKey(), "test_outline.pdf", testData2.getValue());
        assertTrue(victim.hasOutline());
        assertEquals(2, count(victim.getOutline()));
        for (PDOutlineItem current : victim.getOutline().children()) {
            assertFalse(current.hasChildren());
        }
    }

    @Test
    public void retainAsOneEntry() {
        OutlineMerger victim = new OutlineMerger(OutlinePolicy.RETAIN_AS_ONE_ENTRY);
        victim.updateOutline(testData.getKey(), "large_outline.pdf", testData.getValue());
        victim.updateOutline(testData2.getKey(), "test_outline.pdf", testData2.getValue());
        assertTrue(victim.hasOutline());
        assertEquals(2, count(victim.getOutline()));
        for (PDOutlineItem current : victim.getOutline().children()) {
            assertTrue(current.hasChildren());
        }
    }

    @Test
    public void retainSome() {
        OutlineMerger victim = new OutlineMerger(OutlinePolicy.RETAIN);
        testData.getValue().clear();
        testData.getValue().addLookupEntry(testData.getKey().getPage(2), new PDPage());
        testData.getValue().addLookupEntry(testData.getKey().getPage(3), new PDPage());
        testData.getValue().addLookupEntry(testData.getKey().getPage(4), new PDPage());
        victim.updateOutline(testData.getKey(), "large_outline.pdf", testData.getValue());
        assertTrue(victim.hasOutline());
        assertEquals(28, count(victim.getOutline()));
    }

    @Test
    public void onePerDocNoName() {
        OutlineMerger victim = new OutlineMerger(OutlinePolicy.ONE_ENTRY_EACH_DOC);
        victim.updateOutline(testData.getKey(), "", testData.getValue());
        assertFalse(victim.hasOutline());
    }

    @Test
    public void retainAllNoRelevantPage() {
        OutlineMerger victim = new OutlineMerger(OutlinePolicy.RETAIN);
        victim.updateOutline(testData.getKey(), "large_outline.pdf", new LookupTable<>());
        assertFalse(victim.hasOutline());
    }

    @Test
    public void handlePageNumsInsteadOfRefsInDestinations() throws IOException {
        ImmutablePair<PDDocument, LookupTable<PDPage>> data = getTestData(
                "pdf/page_dests_with_number_insteadof_refs.pdf");
        OutlineMerger victim = new OutlineMerger(OutlinePolicy.RETAIN);
        victim.updateOutline(data.getKey(), "page_dests_with_number_insteadof_refs.pdf", data.getValue());
        assertTrue(victim.hasOutline());
        assertEquals(1, count(victim.getOutline()));
        assertEquals(2,
                StreamSupport.stream(victim.getOutline().getFirstChild().children().spliterator(), false).count());
    }

    private ImmutablePair<PDDocument, LookupTable<PDPage>> getTestData(String path) throws IOException {
        PDDocument document = PDFParser.parse(
                SeekableSources.inMemorySeekableSourceFrom(getClass().getClassLoader().getResourceAsStream(path)));
        LookupTable<PDPage> mapping = new LookupTable<>();
        for (PDPage current : document.getPages()) {
            mapping.addLookupEntry(current, new PDPage());
        }
        return ImmutablePair.of(document, mapping);
    }

    private static long count(PDDocumentOutline outline) {
        return StreamSupport.stream(outline.children().spliterator(), false).count();
    }
}
