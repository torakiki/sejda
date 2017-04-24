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
import static org.mockito.Mockito.mock;

import java.io.IOException;

import org.junit.Test;
import org.sejda.io.SeekableSources;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.input.PdfMergeInput;
import org.sejda.model.parameter.MergeParameters;
import org.sejda.model.toc.ToCPolicy;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.text.PDFTextStripper;

/**
 * @author Andrea Vacondio
 *
 */
public class TableOfContentsCreatorTest {

    @Test(expected = IllegalArgumentException.class)
    public void nullDocument() {
        new TableOfContentsCreator(new MergeParameters(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullParams() {
        new TableOfContentsCreator(null, new PDDocument());
    }

    @Test
    public void testShouldGenerateToC() {
        MergeParameters params = new MergeParameters();
        params.setTableOfContentsPolicy(ToCPolicy.DOC_TITLES);
        assertTrue(new TableOfContentsCreator(params, new PDDocument()).shouldGenerateToC());
        assertFalse(new TableOfContentsCreator(new MergeParameters(), new PDDocument()).shouldGenerateToC());
        params.setTableOfContentsPolicy(null);
        assertFalse(new TableOfContentsCreator(new MergeParameters(), new PDDocument()).shouldGenerateToC());
    }

    @Test
    public void testHasToc() {
        MergeParameters params = new MergeParameters();
        params.setTableOfContentsPolicy(ToCPolicy.DOC_TITLES);
        TableOfContentsCreator victim = new TableOfContentsCreator(params, new PDDocument());
        assertFalse(victim.hasToc());
        victim.appendItem("text", 10, new PDPage());
        assertTrue(victim.hasToc());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAppendItemInvalidString() {
        new TableOfContentsCreator(new MergeParameters(), new PDDocument()).appendItem(" ", 10, new PDPage());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAppendItemInvalidPage() {
        new TableOfContentsCreator(new MergeParameters(), new PDDocument()).appendItem("Text", -10, new PDPage());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAppendItemInvalidAnnotation() {
        new TableOfContentsCreator(new MergeParameters(), new PDDocument()).appendItem("Text", 10, null);
    }

    @Test
    public void testAppendItem() {
        MergeParameters params = new MergeParameters();
        params.setTableOfContentsPolicy(ToCPolicy.DOC_TITLES);
        TableOfContentsCreator victim = new TableOfContentsCreator(params, new PDDocument());
        assertFalse(victim.hasToc());
        victim.appendItem("text", 10, new PDPage());
        assertTrue(victim.hasToc());
    }

    @Test
    public void testAddToCNone() {
        PDDocument doc = new PDDocument();
        MergeParameters params = new MergeParameters();
        params.setTableOfContentsPolicy(ToCPolicy.NONE);
        assertEquals(0, doc.getNumberOfPages());
        TableOfContentsCreator victim = new TableOfContentsCreator(params, doc);
        victim.appendItem("text", 10, new PDPage());
        victim.addToC();
        assertEquals(0, doc.getNumberOfPages());
    }

    @Test
    public void testAddToC() {
        MergeParameters params = new MergeParameters();
        params.setTableOfContentsPolicy(ToCPolicy.DOC_TITLES);
        PDDocument doc = new PDDocument();
        assertEquals(0, doc.getNumberOfPages());
        TableOfContentsCreator victim = new TableOfContentsCreator(params, doc);
        victim.appendItem("text", 10, new PDPage());
        victim.addToC();
        assertEquals(1, doc.getNumberOfPages());
    }

    @Test
    public void testAddToCTop() throws IOException {
        PDDocument doc = PDFParser.parse(SeekableSources
                .inMemorySeekableSourceFrom(getClass().getClassLoader().getResourceAsStream("pdf/test_outline.pdf")));
        PDPage firstPage = doc.getPage(0);
        assertEquals(3, doc.getNumberOfPages());
        MergeParameters params = new MergeParameters();
        params.setTableOfContentsPolicy(ToCPolicy.FILE_NAMES);
        TableOfContentsCreator victim = new TableOfContentsCreator(params, doc);
        victim.appendItem("text", 10, new PDPage());
        victim.addToC();
        assertEquals(4, doc.getNumberOfPages());
        assertEquals(firstPage.getCOSObject(), doc.getPage(1).getCOSObject());
    }

    @Test
    public void testAddToCWithBlankPageIfOdd() throws IOException {
        PDDocument doc = PDFParser.parse(SeekableSources
                .inMemorySeekableSourceFrom(getClass().getClassLoader().getResourceAsStream("pdf/test_outline.pdf")));
        PDPage firstPage = doc.getPage(0);
        assertEquals(3, doc.getNumberOfPages());
        MergeParameters params = new MergeParameters();
        params.setTableOfContentsPolicy(ToCPolicy.FILE_NAMES);
        params.setBlankPageIfOdd(true);
        TableOfContentsCreator victim = new TableOfContentsCreator(params, doc);
        victim.appendItem("text", 10, new PDPage());
        victim.addToC();
        assertEquals(5, doc.getNumberOfPages());
        assertEquals(firstPage.getCOSObject(), doc.getPage(2).getCOSObject());
    }

    @Test
    public void testAddTwoPagesToC() throws IOException {
        PDDocument doc = PDFParser.parse(SeekableSources
                .inMemorySeekableSourceFrom(getClass().getClassLoader().getResourceAsStream("pdf/test_outline.pdf")));
        PDPage firstPage = doc.getPage(0);
        assertEquals(3, doc.getNumberOfPages());
        MergeParameters params = new MergeParameters();
        params.setTableOfContentsPolicy(ToCPolicy.FILE_NAMES);
        TableOfContentsCreator victim = new TableOfContentsCreator(params, doc);
        for (int i = 1; i < 40; i++) {
            victim.appendItem("text", i, new PDPage());
        }
        victim.addToC();
        assertEquals(5, doc.getNumberOfPages());
        assertEquals(firstPage.getCOSObject(), doc.getPage(2).getCOSObject());
    }

    @Test
    public void testAddToCSuperLongText() {
        PDDocument doc = new PDDocument();
        assertEquals(0, doc.getNumberOfPages());
        MergeParameters params = new MergeParameters();
        params.setTableOfContentsPolicy(ToCPolicy.DOC_TITLES);
        TableOfContentsCreator victim = new TableOfContentsCreator(params, doc);
        victim.appendItem(
                "This is a very long file name and we expect that it's handled correctly and no Exception is thrown by the component. We are making this very very long so we can make sure that even the veeeery long ones are handled.",
                100, new PDPage());
        victim.addToC();
        assertEquals(1, doc.getNumberOfPages());
    }

    @Test
    public void testToCPageSize() {
        PDDocument doc = new PDDocument();
        assertEquals(0, doc.getNumberOfPages());
        MergeParameters params = new MergeParameters();
        params.setTableOfContentsPolicy(ToCPolicy.DOC_TITLES);
        TableOfContentsCreator victim = new TableOfContentsCreator(params, doc);
        victim.pageSizeIfNotSet(PDRectangle.LETTER);
        victim.appendItem("test.", 100, new PDPage());
        victim.addToC();
        assertEquals(PDRectangle.LETTER, doc.getPage(0).getMediaBox());
    }

    @Test
    public void testToCForLargePageSize() {
        PDDocument doc = new PDDocument();
        assertEquals(0, doc.getNumberOfPages());
        MergeParameters params = new MergeParameters();
        params.setTableOfContentsPolicy(ToCPolicy.DOC_TITLES);
        TableOfContentsCreator victim = new TableOfContentsCreator(params, doc);
        victim.pageSizeIfNotSet(PDRectangle.A1);
        victim.appendItem("test.", 100, new PDPage());
        victim.addToC();
        assertEquals(39.64, victim.getFontSize(), 0.1);
    }

    @Test
    public void testStringsThatMixMultipleFontRequirements() {
        PDDocument doc = new PDDocument();
        assertEquals(0, doc.getNumberOfPages());
        MergeParameters params = new MergeParameters();
        params.setTableOfContentsPolicy(ToCPolicy.DOC_TITLES);
        TableOfContentsCreator victim = new TableOfContentsCreator(params, doc);
        victim.appendItem("1-abc-עברית", 100, new PDPage());
        victim.addToC();
        assertEquals(1, doc.getNumberOfPages());
    }

    @Test
    public void indexPageIsConsideredInPageNumbers() throws IOException {
        PDDocument doc = new PDDocument();
        assertEquals(0, doc.getNumberOfPages());
        MergeParameters params = new MergeParameters();
        params.addInput(new PdfMergeInput(mock(PdfFileSource.class)));
        params.setTableOfContentsPolicy(ToCPolicy.DOC_TITLES);
        TableOfContentsCreator victim = new TableOfContentsCreator(params, doc);
        victim.appendItem(
                "This is a very long file name and we expect that it's handled correctly and no Exception is thrown by the component. We are making this very very long so we can make sure that even the veeeery long ones are handled.",
                100, new PDPage());
        victim.addToC();
        assertThat(new PDFTextStripper().getText(doc), containsString("101"));
    }

    @Test
    public void testTocNumberOfPagesNoToc() {
        MergeParameters params = new MergeParameters();
        params.setTableOfContentsPolicy(ToCPolicy.NONE);
        params.addInput(new PdfMergeInput(mock(PdfFileSource.class)));
        TableOfContentsCreator victim = new TableOfContentsCreator(params, new PDDocument());
        assertEquals(0, victim.tocNumberOfPages());
    }

    @Test
    public void testTocNumberOfPages() {
        MergeParameters params = new MergeParameters();
        params.setTableOfContentsPolicy(ToCPolicy.DOC_TITLES);
        params.addInput(new PdfMergeInput(mock(PdfFileSource.class)));
        TableOfContentsCreator victim = new TableOfContentsCreator(params, new PDDocument());
        assertEquals(1, victim.tocNumberOfPages());
    }

    @Test
    public void testTocNumberOfPagesAddBlank() {
        MergeParameters params = new MergeParameters();
        params.setBlankPageIfOdd(true);
        params.setTableOfContentsPolicy(ToCPolicy.DOC_TITLES);
        params.addInput(new PdfMergeInput(mock(PdfFileSource.class)));
        TableOfContentsCreator victim = new TableOfContentsCreator(params, new PDDocument());
        assertEquals(2, victim.tocNumberOfPages());
    }

    @Test
    public void testTocNumberOfPagesMultiple() {
        MergeParameters params = new MergeParameters();
        params.setTableOfContentsPolicy(ToCPolicy.FILE_NAMES);
        for (int i = 1; i < 40; i++) {
            params.addInput(new PdfMergeInput(mock(PdfFileSource.class)));
        }
        TableOfContentsCreator victim = new TableOfContentsCreator(params, new PDDocument());
        victim.pageSizeIfNotSet(PDRectangle.A4);
        assertEquals(2, victim.tocNumberOfPages());
    }

    @Test
    public void testTocNumberOfPagesMultipleInA2() {
        MergeParameters params = new MergeParameters();
        params.setTableOfContentsPolicy(ToCPolicy.FILE_NAMES);
        for (int i = 1; i < 40; i++) {
            params.addInput(new PdfMergeInput(mock(PdfFileSource.class)));
        }
        TableOfContentsCreator victim = new TableOfContentsCreator(params, new PDDocument());
        victim.pageSizeIfNotSet(PDRectangle.A2);
        // ToC font is scaled so we get 2 pages even if a2 is twice the a4 height
        assertEquals(2, victim.tocNumberOfPages());
    }
}
