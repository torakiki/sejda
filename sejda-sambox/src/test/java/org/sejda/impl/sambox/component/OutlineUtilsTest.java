/*
 * Created on 16/ago/2015
 * Copyright 2015 Sober Lemur S.r.l. and Sejda BV.
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

import org.junit.jupiter.api.Test;
import org.sejda.io.SeekableSources;
import org.sejda.sambox.cos.COSArray;
import org.sejda.sambox.cos.COSInteger;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDDocumentCatalog;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.interactive.action.PDActionGoTo;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDNamedDestination;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDPageFitDestination;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDPageFitHeightDestination;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Andrea Vacondio
 *
 */
public class OutlineUtilsTest {

    @Test
    public void toPageDestinationEmpty() {
        PDOutlineItem victim = new PDOutlineItem();
        assertFalse(OutlineUtils.toPageDestination(victim, null).isPresent());
    }

    @Test
    public void toPageDestinationAction() {
        PDPageFitDestination destination = new PDPageFitDestination();
        PDPage page = new PDPage();
        destination.setPage(page);
        PDActionGoTo action = new PDActionGoTo();
        action.setDestination(destination);
        PDOutlineItem victim = new PDOutlineItem();
        victim.setAction(action);
        assertEquals(destination.getPage(), OutlineUtils.toPageDestination(victim, null).get().getPage());
    }

    @Test
    public void toPageDestinationDestination() {
        PDPageFitDestination destination = new PDPageFitDestination();
        destination.setPageNumber(5);
        PDOutlineItem victim = new PDOutlineItem();
        victim.setDestination(destination);
        PDDocumentCatalog catalog = mock(PDDocumentCatalog.class);
        assertEquals(5, OutlineUtils.toPageDestination(victim, catalog).get().getPageNumber());
    }

    @Test
    public void toPageDestinationDestinationNullCatalog() {
        PDPageFitDestination dest = new PDPageFitDestination();
        dest.setPageNumber(5);
        PDNamedDestination destination = new PDNamedDestination();
        destination.setNamedDestination("ChuckNorris");
        PDOutlineItem victim = new PDOutlineItem();
        victim.setDestination(destination);
        PDDocumentCatalog catalog = null;
        assertFalse(OutlineUtils.toPageDestination(victim, catalog).isPresent());
    }

    @Test
    public void toPageDestinationNamedDestinationNullNames() {
        PDNamedDestination destination = new PDNamedDestination();
        PDOutlineItem victim = new PDOutlineItem();
        victim.setDestination(destination);
        assertFalse(OutlineUtils.toPageDestination(victim, null).isPresent());
    }

    @Test
    public void toPageDestinationNamedDestination() throws IOException {
        PDPageFitDestination dest = new PDPageFitDestination();
        dest.setPageNumber(5);
        PDNamedDestination destination = new PDNamedDestination();
        destination.setNamedDestination("ChuckNorris");
        PDOutlineItem victim = new PDOutlineItem();
        victim.setDestination(destination);
        PDDocumentCatalog catalog = mock(PDDocumentCatalog.class);
        when(catalog.findNamedDestinationPage(any())).thenReturn(dest);
        assertEquals(dest, OutlineUtils.toPageDestination(victim, catalog).get());
    }

    @Test
    public void pageDestinationForNull() {
        assertThrows(IllegalArgumentException.class, () -> OutlineUtils.pageDestinationFor(null));
    }

    @Test
    public void pageDestinationFor() {
        PDPage page = new PDPage(PDRectangle.A4);
        PDPageXYZDestination noRotation = OutlineUtils.pageDestinationFor(page);
        assertEquals(0, noRotation.getLeft());
        assertEquals((int) PDRectangle.A4.getHeight(), noRotation.getTop());

        page.setRotation(90);
        PDPageXYZDestination rotation90 = OutlineUtils.pageDestinationFor(page);
        assertEquals(0, rotation90.getLeft());
        assertEquals(0, rotation90.getTop());

        page.setRotation(180);
        PDPageXYZDestination rotation180 = OutlineUtils.pageDestinationFor(page);
        assertEquals((int) PDRectangle.A4.getWidth(), rotation180.getLeft());
        assertEquals(0, rotation180.getTop());

        page.setRotation(270);
        PDPageXYZDestination rotation270 = OutlineUtils.pageDestinationFor(page);
        assertEquals((int) PDRectangle.A4.getWidth(), rotation270.getLeft());
        assertEquals((int) PDRectangle.A4.getHeight(), rotation270.getTop());
    }

    @Test
    public void clonePageDestinationNullDest() {
        assertThrows(IllegalArgumentException.class, () -> OutlineUtils.clonePageDestination(null, new PDPage()));
    }

    @Test
    public void clonePageDestinationSameType() {
        PDPage origin = new PDPage();
        PDPage newPage = new PDPage();
        PDPageFitHeightDestination destination = new PDPageFitHeightDestination();
        destination.setPage(origin);
        destination.setLeft(20);
        PDPageDestination cloned = OutlineUtils.clonePageDestination(destination, newPage);
        assertThat(cloned, is(instanceOf(PDPageFitHeightDestination.class)));
        assertEquals(newPage, cloned.getPage());
        assertEquals(20, ((PDPageFitHeightDestination) cloned).getLeft());
    }

    @Test
    public void copyDictionary() {
        PDOutlineItem from = new PDOutlineItem();
        from.setBold(true);
        from.setItalic(true);
        from.setTitle("Chuck");
        PDOutlineItem to = new PDOutlineItem();
        to.setBold(false);
        to.setItalic(false);
        OutlineUtils.copyOutlineDictionary(from, to);
        assertTrue(to.isBold());
        assertTrue(to.isItalic());
        assertEquals("Chuck", to.getTitle());
        assertNull(to.getCOSObject().getDictionaryObject(COSName.C));
    }

    @Test
    // title is required by the spec
    public void copyDictionaryFixesNullTitles() {
        PDOutlineItem from = new PDOutlineItem();
        from.setTitle(null);
        PDOutlineItem to = new PDOutlineItem();
        assertNull(from.getTitle());
        OutlineUtils.copyOutlineDictionary(from, to);
        assertNotNull(to.getTitle());
    }

    @Test
    public void noOutlineFlat() throws IOException {
        try (PDDocument doc = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/test_no_outline.pdf")))) {
            assertTrue(OutlineUtils.getFlatOutline(doc).isEmpty());
        }
    }

    @Test
    public void outlineFlat() throws IOException {
        try (PDDocument doc = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/test_outline.pdf")))) {
            assertEquals(5, OutlineUtils.getFlatOutline(doc).size());
        }
    }

    @Test
    public void outlineFlatIntPageDestinations() throws IOException {
        try (PDDocument doc = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/destination_pages_as_int.pdf")))) {
            List<OutlineItem> flatOutline = OutlineUtils.getFlatOutline(doc);
            assertEquals(5, flatOutline.size());
            assertEquals(1, flatOutline.get(0).page);
            assertEquals(1, flatOutline.get(1).page);
            assertEquals(3, flatOutline.get(2).page);
            assertEquals(3, flatOutline.get(3).page);
            assertEquals(5, flatOutline.get(4).page);
        }
    }

    @Test
    public void outlineLevels() throws IOException {
        try (PDDocument doc = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/test_outline.pdf")))) {
            Set<Integer> levels = OutlineUtils.getOutlineLevelsWithPageDestination(doc);
            assertEquals(3, levels.size());
            assertTrue(levels.contains(1));
            assertTrue(levels.contains(2));
            assertTrue(levels.contains(3));
        }
    }

    @Test
    public void outlineLevelsParentHasNoPageDest() {
        PDPage page1 = new PDPage();
        PDDocument document = new PDDocument();
        document.addPage(page1);
        PDDocumentOutline outlines = new PDDocumentOutline();
        PDOutlineItem root = new PDOutlineItem();
        root.setTitle("title");
        PDOutlineItem child = new PDOutlineItem();
        child.setTitle("child");
        PDOutlineItem child2 = new PDOutlineItem();
        child2.setTitle("child2");
        child2.setDestination(page1);
        child.addFirst(child2);
        root.addLast(child);
        outlines.addFirst(root);
        document.getDocumentCatalog().setDocumentOutline(outlines);
        Set<Integer> levels = OutlineUtils.getOutlineLevelsWithPageDestination(document);
        assertEquals(1, levels.size());
        assertTrue(levels.contains(3));
    }

    @Test
    public void rootNoDestinationAndSorted() {
        PDPage page1 = new PDPage();
        PDPage page2 = new PDPage();
        PDDocument document = new PDDocument();
        document.addPage(page1);
        document.addPage(page2);
        PDDocumentOutline outlines = new PDDocumentOutline();
        PDOutlineItem root = new PDOutlineItem();
        root.setTitle("title");
        PDOutlineItem child = new PDOutlineItem();
        child.setTitle("child");
        child.setDestination(page2);
        PDOutlineItem child2 = new PDOutlineItem();
        child2.setTitle("child2");
        child2.setDestination(page1);
        root.addFirst(child);
        root.addLast(child2);
        outlines.addFirst(root);
        document.getDocumentCatalog().setDocumentOutline(outlines);
        List<OutlineItem> flat = OutlineUtils.getFlatOutline(document);
        assertEquals(2, flat.size());
        assertEquals("child2", flat.get(0).title);
    }

    @Test
    public void resolvePageDestination() throws IOException {
        PDPage page1 = new PDPage();
        PDPage page2 = new PDPage();
        PDDocument document = new PDDocument();
        document.addPage(page1);
        document.addPage(page2);
        PDDocumentOutline outlines = new PDDocumentOutline();
        PDOutlineItem root = new PDOutlineItem();
        root.setTitle("title");
        PDOutlineItem child = new PDOutlineItem();
        child.setTitle("child");
        child.setDestination(page2);
        PDOutlineItem child2 = new PDOutlineItem();
        child2.setTitle("child2");
        child2.setDestination(page1);
        child2.getCOSObject().getDictionaryObject(COSName.DEST, COSArray.class).set(0, COSInteger.ONE);
        root.addFirst(child);
        root.addLast(child2);
        outlines.addFirst(root);
        document.getDocumentCatalog().setDocumentOutline(outlines);
        assertNotNull(OutlineUtils.resolvePageDestination((PDPageDestination) child2.getDestination(), document));
        assertNotNull(OutlineUtils.resolvePageDestination((PDPageDestination) child.getDestination(), document));
        PDPageDestination negative = new PDPageXYZDestination();
        negative.setPageNumber(-1);
        assertNull(OutlineUtils.resolvePageDestination(negative, document));
        PDPageDestination notFound = new PDPageXYZDestination();
        notFound.setPageNumber(10);
        assertNull(OutlineUtils.resolvePageDestination(notFound, document));
    }

    @Test
    public void pageGroupedPageDestinations() {
        PDPage page1 = new PDPage();
        PDPage page2 = new PDPage();
        PDPage page3 = new PDPage();
        PDDocument document = new PDDocument();
        document.addPage(page1);
        document.addPage(page2);
        document.addPage(page3);
        PDDocumentOutline outlines = new PDDocumentOutline();
        PDOutlineItem root = new PDOutlineItem();
        root.setTitle("title");
        PDOutlineItem child = new PDOutlineItem();
        child.setTitle("child");
        child.setDestination(page2);
        PDOutlineItem child2 = new PDOutlineItem();
        child2.setTitle("child2");
        child2.setDestination(page1);
        PDOutlineItem child3 = new PDOutlineItem();
        child3.setTitle("child3");
        PDPageFitHeightDestination gotoDest = new PDPageFitHeightDestination();
        gotoDest.setLeft(50);
        gotoDest.setPage(page1);
        PDActionGoTo action = new PDActionGoTo();
        action.setDestination(gotoDest);
        child3.setAction(action);
        child2.getCOSObject().getDictionaryObject(COSName.DEST, COSArray.class).set(0, COSInteger.ONE);
        PDOutlineItem child4 = new PDOutlineItem();
        child4.setTitle("child4");
        child4.setDestination(page3);
        child3.addLast(child4);
        root.addFirst(child);
        root.addLast(child2);
        root.addLast(child3);
        outlines.addFirst(root);
        document.getDocumentCatalog().setDocumentOutline(outlines);
        Map<PDPage, Set<PDPageDestination>> victim = OutlineUtils.pageGroupedOutlinePageDestinations(document);
        assertEquals(3, victim.size());
        assertEquals(1, victim.get(page1).size());
        assertEquals(2, victim.get(page2).size());
        assertEquals(1, victim.get(page3).size());
    }
}
