/*
 * Created on 16 dic 2015
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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sejda.commons.LookupTable;
import org.sejda.commons.util.IOUtils;
import org.sejda.io.SeekableSources;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDPageFitWidthDestination;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Andrea Vacondio
 */
public class OutlineDistillerTest {

    private PDDocument document;
    private LookupTable<PDPage> mapping = new LookupTable<>();

    @BeforeEach
    public void setUp() throws IOException {
        document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/test_outline.pdf")));
    }

    @AfterEach
    public void tearDown() {
        IOUtils.closeQuietly(document);
        mapping.clear();
    }

    @Test
    public void keepAll() {
        for (PDPage current : document.getPages()) {
            mapping.addLookupEntry(current, new PDPage());
        }
        PDDocumentOutline outline = new PDDocumentOutline();
        new OutlineDistiller(document).appendRelevantOutlineTo(outline, mapping);
        assertTrue(outline.hasChildren());
        assertEquals(5, outline.getOpenCount());
    }

    @Test
    public void keepOneBranch() {
        mapping.addLookupEntry(document.getPage(2), new PDPage());
        PDDocumentOutline outline = new PDDocumentOutline();
        new OutlineDistiller(document).appendRelevantOutlineTo(outline, mapping);
        assertTrue(outline.hasChildren());
        assertEquals(2, outline.getOpenCount());
    }

    @Test
    public void emptyMapping() {
        PDDocumentOutline outline = new PDDocumentOutline();
        new OutlineDistiller(document).appendRelevantOutlineTo(outline, mapping);
        assertFalse(outline.hasChildren());
    }

    @Test
    public void rootIsKeptChildIsStripped() {
        PDPage page1 = new PDPage();
        PDPage page2 = new PDPage();
        PDDocument document = new PDDocument();
        document.addPage(page1);
        document.addPage(page2);
        PDDocumentOutline outlines = new PDDocumentOutline();
        PDOutlineItem root = new PDOutlineItem();
        root.setTitle("title");
        root.setDestination(page1);
        PDOutlineItem child = new PDOutlineItem();
        child.setTitle("child");
        child.setDestination(page2);
        root.addFirst(child);
        outlines.addFirst(root);
        document.getDocumentCatalog().setDocumentOutline(outlines);
        mapping.addLookupEntry(document.getPage(0), new PDPage());
        PDDocumentOutline outline = new PDDocumentOutline();
        new OutlineDistiller(document).appendRelevantOutlineTo(outline, mapping);
        assertTrue(outline.hasChildren());
        assertEquals(1, outline.getOpenCount());
    }

    @Test
    public void destinationTypeIsPreservedInLeaves() throws IOException {
        try (PDDocument document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/large_outline.pdf")))) {
            mapping.addLookupEntry(document.getPage(2), new PDPage());
            PDDocumentOutline outline = new PDDocumentOutline();
            new OutlineDistiller(document).appendRelevantOutlineTo(outline, mapping);
            PDDestination clonedDest = outline.getFirstChild().getDestination();
            assertThat(clonedDest, is(instanceOf(PDPageFitWidthDestination.class)));
            assertEquals(806, ((PDPageFitWidthDestination) clonedDest).getTop());
        }
    }

    @Test
    public void destinationTypeIsPreservedInNodes() throws IOException {
        mapping.addLookupEntry(document.getPage(2), new PDPage());
        PDDocumentOutline outline = new PDDocumentOutline();
        new OutlineDistiller(document).appendRelevantOutlineTo(outline, mapping);
        PDDestination clonedDest = outline.getFirstChild().getDestination();
        assertThat(clonedDest, is(instanceOf(PDPageXYZDestination.class)));
        assertEquals(759, ((PDPageXYZDestination) clonedDest).getTop());
        assertEquals(56, ((PDPageXYZDestination) clonedDest).getLeft());
    }

    @Test
    public void fallbackHandlesBrokenDestinations() throws IOException {
        // https://github.com/torakiki/pdfsam/issues/361
        try (PDDocument document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/page_dests_with_number_insteadof_refs.pdf")))) {
            for (PDPage current : document.getPages()) {
                mapping.addLookupEntry(current, new PDPage());
            }
            PDDocumentOutline outline = new PDDocumentOutline();
            new OutlineDistiller(document).appendRelevantOutlineTo(outline, mapping);
            assertTrue(outline.hasChildren());
        }
    }

    @Test
    public void fallbackHandlesBrokenDestinationsWithNonExistingPageNumber() throws IOException {
        // https://github.com/torakiki/pdfsam/issues/361
        try (PDDocument document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader()
                        .getResourceAsStream("pdf/page_dests_with_number_insteadof_refs_wrong_num.pdf")))) {
            for (PDPage current : document.getPages()) {
                mapping.addLookupEntry(current, new PDPage());
            }
            PDDocumentOutline outline = new PDDocumentOutline();
            new OutlineDistiller(document).appendRelevantOutlineTo(outline, mapping);
            assertTrue(outline.hasChildren());
        }
    }

    @Test
    public void infiniteLoop() throws IOException {
        try (PDDocument document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/infinite_outline.pdf")))) {

            for (PDPage current : document.getPages()) {
                mapping.addLookupEntry(current, new PDPage());
            }
            PDDocumentOutline outline = new PDDocumentOutline();
            new OutlineDistiller(document).appendRelevantOutlineTo(outline, mapping);
            assertTrue(outline.hasChildren());

            int outlineChildCount = 0;
            for (PDOutlineItem child : outline.children()) {
                outlineChildCount++;
            }

            assertEquals(3, outlineChildCount);
        }
    }

    @Test
    public void infiniteLoopDeepLevel() {
        PDPage page1 = new PDPage();
        PDPage page2 = new PDPage();
        PDDocument document = new PDDocument();
        document.addPage(page1);
        document.addPage(page2);
        PDDocumentOutline outlines = new PDDocumentOutline();
        
        PDOutlineItem root = new PDOutlineItem();
        root.setTitle("root");
        root.setDestination(page1);
        
        PDOutlineItem child1 = new PDOutlineItem();
        child1.setTitle("child1");
        child1.setDestination(page2);

        PDOutlineItem child2 = new PDOutlineItem();
        child2.setTitle("child2");
        child2.setDestination(page2);

        child2.addFirst(root);
        child1.addFirst(child2);
        root.addFirst(child1);
        outlines.addFirst(root);
        
        document.getDocumentCatalog().setDocumentOutline(outlines);
        
        mapping.addLookupEntry(document.getPage(0), new PDPage());
        mapping.addLookupEntry(document.getPage(1), new PDPage());
        
        PDDocumentOutline outline = new PDDocumentOutline();
        new OutlineDistiller(document).appendRelevantOutlineTo(outline, mapping);
        assertTrue(outline.hasChildren());
        assertEquals(1, outline.getOpenCount());
        assertEquals("root", outline.getFirstChild().getTitle());
        assertEquals("child1", outline.getFirstChild().getFirstChild().getTitle());
        assertEquals("child2", outline.getFirstChild().getFirstChild().getFirstChild().getTitle());
    }
}
