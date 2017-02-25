/*
 * Created on 19 feb 2016
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

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.sejda.io.SeekableSources;
import org.sejda.model.toc.ToCPolicy;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.sejda.sambox.text.PDFTextStripper;

/**
 * @author Andrea Vacondio
 *
 */
public class TableOfContentsCreatorTest {

    @Test(expected = IllegalArgumentException.class)
    public void testTableOfContentsCreator() {
        new TableOfContentsCreator(ToCPolicy.DOC_TITLES, null);
    }

    @Test
    public void testShouldGenerateToC() {
        assertTrue(new TableOfContentsCreator(ToCPolicy.DOC_TITLES, new PDDocument()).shouldGenerateToC());
        assertFalse(new TableOfContentsCreator(ToCPolicy.NONE, new PDDocument()).shouldGenerateToC());
        assertFalse(new TableOfContentsCreator(null, new PDDocument()).shouldGenerateToC());
    }

    @Test
    public void testHasToc() {
        TableOfContentsCreator victim = new TableOfContentsCreator(ToCPolicy.FILE_NAMES, new PDDocument());
        assertFalse(victim.hasToc());
        victim.appendItem("text", 10, new PDAnnotationLink());
        assertTrue(victim.hasToc());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAppendItemInvalidString() {
        new TableOfContentsCreator(ToCPolicy.DOC_TITLES, new PDDocument()).appendItem(" ", 10, new PDAnnotationLink());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAppendItemInvalidPage() {
        new TableOfContentsCreator(ToCPolicy.DOC_TITLES, new PDDocument()).appendItem("Text", -10,
                new PDAnnotationLink());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAppendItemInvalidAnnotation() {
        new TableOfContentsCreator(ToCPolicy.DOC_TITLES, new PDDocument()).appendItem("Text", 10, null);
    }

    @Test
    public void testAppendItem() {
        TableOfContentsCreator victim = new TableOfContentsCreator(ToCPolicy.DOC_TITLES, new PDDocument());
        assertFalse(victim.hasToc());
        victim.appendItem("text", 10, new PDAnnotationLink());
        assertTrue(victim.hasToc());

        TableOfContentsCreator victim2 = new TableOfContentsCreator(ToCPolicy.NONE, new PDDocument());
        assertFalse(victim2.hasToc());
        victim.appendItem("text", 10, new PDAnnotationLink());
        assertFalse(victim2.hasToc());
    }

    @Test
    public void testAddToCNone() {
        PDDocument doc = new PDDocument();
        assertEquals(0, doc.getNumberOfPages());
        TableOfContentsCreator victim = new TableOfContentsCreator(ToCPolicy.NONE, doc);
        victim.appendItem("text", 10, new PDAnnotationLink());
        victim.addToC(false);
        assertEquals(0, doc.getNumberOfPages());
    }

    @Test
    public void testAddToC() {
        PDDocument doc = new PDDocument();
        assertEquals(0, doc.getNumberOfPages());
        TableOfContentsCreator victim = new TableOfContentsCreator(ToCPolicy.DOC_TITLES, doc);
        victim.appendItem("text", 10, new PDAnnotationLink());
        victim.addToC(false);
        assertEquals(1, doc.getNumberOfPages());
    }

    @Test
    public void testAddToCTop() throws IOException {
        PDDocument doc = PDFParser.parse(SeekableSources
                .inMemorySeekableSourceFrom(getClass().getClassLoader().getResourceAsStream("pdf/test_outline.pdf")));
        PDPage firstPage = doc.getPage(0);
        assertEquals(3, doc.getNumberOfPages());
        TableOfContentsCreator victim = new TableOfContentsCreator(ToCPolicy.FILE_NAMES, doc);
        victim.appendItem("text", 10, new PDAnnotationLink());
        victim.addToC(false);
        assertEquals(4, doc.getNumberOfPages());
        assertEquals(firstPage.getCOSObject(), doc.getPage(1).getCOSObject());
    }

    @Test
    public void testAddToCWithBlankPageIfOdd() throws IOException {
        PDDocument doc = PDFParser.parse(SeekableSources
                .inMemorySeekableSourceFrom(getClass().getClassLoader().getResourceAsStream("pdf/test_outline.pdf")));
        PDPage firstPage = doc.getPage(0);
        assertEquals(3, doc.getNumberOfPages());
        TableOfContentsCreator victim = new TableOfContentsCreator(ToCPolicy.FILE_NAMES, doc);
        victim.appendItem("text", 10, new PDAnnotationLink());
        victim.addToC(true);
        assertEquals(5, doc.getNumberOfPages());
        assertEquals(firstPage.getCOSObject(), doc.getPage(2).getCOSObject());
    }

    @Test
    public void testAddTwoPagesToC() throws IOException {
        PDDocument doc = PDFParser.parse(SeekableSources
                .inMemorySeekableSourceFrom(getClass().getClassLoader().getResourceAsStream("pdf/test_outline.pdf")));
        PDPage firstPage = doc.getPage(0);
        assertEquals(3, doc.getNumberOfPages());
        TableOfContentsCreator victim = new TableOfContentsCreator(ToCPolicy.FILE_NAMES, doc);
        for (int i = 1; i < 40; i++) {
            victim.appendItem("text", i, new PDAnnotationLink());
        }
        victim.addToC(false);
        assertEquals(5, doc.getNumberOfPages());
        assertEquals(firstPage.getCOSObject(), doc.getPage(2).getCOSObject());
    }

    @Test
    public void testAddToCSuperLongText() {
        PDDocument doc = new PDDocument();
        assertEquals(0, doc.getNumberOfPages());
        TableOfContentsCreator victim = new TableOfContentsCreator(ToCPolicy.DOC_TITLES, doc);
        victim.appendItem(
                "This is a very long file name and we expect that it's handled correctly and no Exception is thrown by the component. We are making this very very long so we can make sure that even the veeeery long ones are handled.",
                100, new PDAnnotationLink());
        victim.addToC(false);
        assertEquals(1, doc.getNumberOfPages());
    }

    @Test
    public void testToCPageSize() {
        PDDocument doc = new PDDocument();
        assertEquals(0, doc.getNumberOfPages());
        TableOfContentsCreator victim = new TableOfContentsCreator(ToCPolicy.DOC_TITLES, doc);
        victim.pageSizeIfNotSet(PDRectangle.LETTER);
        victim.appendItem("test.", 100, new PDAnnotationLink());
        victim.addToC(false);
        assertEquals(PDRectangle.LETTER, doc.getPage(0).getMediaBox());
    }

    @Test
    public void testToCForLargePageSize() {
        PDDocument doc = new PDDocument();
        assertEquals(0, doc.getNumberOfPages());
        TableOfContentsCreator victim = new TableOfContentsCreator(ToCPolicy.DOC_TITLES, doc);
        victim.pageSizeIfNotSet(PDRectangle.A1);
        victim.appendItem("test.", 100, new PDAnnotationLink());
        victim.addToC(false);
        assertEquals(39.64, victim.getFontSize(), 0.1);
    }

    @Test
    public void testStringsThatMixMultipleFontRequirements() {
        PDDocument doc = new PDDocument();
        assertEquals(0, doc.getNumberOfPages());
        TableOfContentsCreator victim = new TableOfContentsCreator(ToCPolicy.DOC_TITLES, doc);
        victim.appendItem("1-abc-עברית", 100, new PDAnnotationLink());
        victim.addToC(false);
        assertEquals(1, doc.getNumberOfPages());
    }

    @Test
    public void indexPageIsConsideredInPageNumbers() throws IOException {
        PDDocument doc = new PDDocument();
        assertEquals(0, doc.getNumberOfPages());
        TableOfContentsCreator victim = new TableOfContentsCreator(ToCPolicy.DOC_TITLES, doc);
        victim.appendItem(
                "This is a very long file name and we expect that it's handled correctly and no Exception is thrown by the component. We are making this very very long so we can make sure that even the veeeery long ones are handled.",
                100, new PDAnnotationLink());
        victim.addToC(false);
        assertThat(new PDFTextStripper().getText(doc), containsString("101"));
    }
}
