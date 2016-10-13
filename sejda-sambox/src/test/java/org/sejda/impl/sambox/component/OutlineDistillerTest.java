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

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sejda.common.LookupTable;
import org.sejda.io.SeekableSources;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDPageFitWidthDestination;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.sejda.util.IOUtils;

/**
 * @author Andrea Vacondio
 *
 */
public class OutlineDistillerTest {

    private PDDocument document;
    private LookupTable<PDPage> mapping = new LookupTable<>();

    @Before
    public void setUp() throws IOException {
        document = PDFParser.parse(SeekableSources
                .inMemorySeekableSourceFrom(getClass().getClassLoader().getResourceAsStream("pdf/test_outline.pdf")));
    }

    @After
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
}
