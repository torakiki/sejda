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
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.sejda.core.service.TestUtils;
import org.sejda.io.SeekableSources;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.UnsupportedTextException;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.input.PdfMergeInput;
import org.sejda.model.parameter.MergeParameters;
import org.sejda.model.toc.ToCPolicy;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;
import org.sejda.sambox.text.PDFTextStripper;

/**
 * @author Andrea Vacondio
 * @author Edi Weissmann
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
    public void testAddToCNone() throws TaskException {
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
    public void testAddToC() throws TaskException {
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
    public void testAddToCTop() throws IOException, TaskException {
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
    public void testAddToCWithBlankPageIfOdd() throws IOException, TaskException {
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
    public void testAddTwoPagesToC() throws IOException, TaskException {
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
    public void testAddToCSuperLongText() throws TaskException {
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
    public void testToCPageSize() throws TaskException {
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
    public void testToCForLargePageSize() throws TaskException {
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
    public void testStringsThatMixMultipleFontRequirements() throws TaskException {
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
    public void indexPageIsConsideredInPageNumbers() throws IOException, TaskException {
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

    @Test
    public void test_Toc_Long_Item_That_Wraps_On_Two_Lines() throws TaskException, IOException {
        MergeParameters params = new MergeParameters();
        params.setTableOfContentsPolicy(ToCPolicy.FILE_NAMES);
        PDDocument doc = new PDDocument();
        PDPage pageDest1 = new PDPage(), pageDest2 = new PDPage(), pageDest3 = new PDPage();
        TableOfContentsCreator victim = new TableOfContentsCreator(params, doc);
        victim.appendItem("This is item 1", 1, pageDest1);
        victim.appendItem(
                "This is item 2 that has a very long name and should not be truncated so that the version is visible at the end v7.pdf",
                10, pageDest2);
        victim.appendItem("This is item 3", 14, pageDest3);
        victim.pageSizeIfNotSet(PDRectangle.A4);
        victim.addToC();

        TestUtils.assertPageTextExactLines(doc.getPage(0),
                "This is item 1   1\n" + "This is item 2 that has a very long name and should not be\n"
                        + "truncated so that the version is visible at the end v7.pdf   10\n"
                        + "This is item 3   14\n");

        // verify size of the clickable annotations on top of the TOC items
        List<PDAnnotationLink> annotations = TestUtils.getAnnotationsOf(doc.getPage(0), PDAnnotationLink.class);

        // lower left y always changes, height is only different for the middle one that wraps
        PDAnnotationLink link1 = annotations.get(0);
        TestUtils.assertPDRectanglesEqual(link1.getRectangle(), new PDRectangle(72.0f, 764.98f, 451.27f, 14f));
        TestUtils.assertPageDestination(link1, pageDest1);

        PDAnnotationLink link2 = annotations.get(1);
        TestUtils.assertPDRectanglesEqual(link2.getRectangle(), new PDRectangle(72.0f, 717.38f, 451.27f, 37.79f));
        TestUtils.assertPageDestination(link2, pageDest2);

        PDAnnotationLink link3 = annotations.get(2);
        TestUtils.assertPDRectanglesEqual(link3.getRectangle(), new PDRectangle(72.0f, 693.58f, 451.27f, 14f));
        TestUtils.assertPageDestination(link3, pageDest3);
    }

    @Test
    public void test_Toc_Long_Item_That_Wraps_At_The_End_Of_The_Page() throws TaskException {
        MergeParameters params = new MergeParameters();
        params.setTableOfContentsPolicy(ToCPolicy.FILE_NAMES);
        PDDocument doc = new PDDocument();
        TableOfContentsCreator victim = new TableOfContentsCreator(params, doc);
        for (int i = 0; i < 30; i++) {
            victim.appendItem("This is an item", 1, new PDPage());
        }

        victim.appendItem(
                "This is a long item that has a very long name and should not be truncated so that the version is visible at the end v7.pdf",
                10, new PDPage());
        victim.appendItem("This is an item", 14, new PDPage());
        victim.pageSizeIfNotSet(PDRectangle.A4);
        victim.addToC();

        TestUtils.assertPageTextDoesNotContain(doc.getPage(0), "This is a long item that");

        TestUtils.assertPageTextContains(doc.getPage(1), "This is a long item that");
    }

    @Test
    public void test_Toc_Long_Item_That_Has_No_Word_Breaks() throws TaskException {
        MergeParameters params = new MergeParameters();
        params.setTableOfContentsPolicy(ToCPolicy.FILE_NAMES);
        PDDocument doc = new PDDocument();
        TableOfContentsCreator victim = new TableOfContentsCreator(params, doc);
        victim.appendItem(
                "This_is_a_file_that_has_a_very_long_name_and_should_not_be_truncated_so_that_the_version_is_visible_at_the_end_v7.pdf",
                10, new PDPage());
        victim.pageSizeIfNotSet(PDRectangle.A4);
        victim.addToC();

        TestUtils.assertPageTextExactLines(doc.getPage(0),
                "This_is_a_file_that_has_a_very_long_name_and_should_not_be_tr-\n"
                        + "uncated_so_that_the_version_is_visible_at_the_end_v7.pdf   10\n");
    }

    @Test
    public void test_Toc_Item_Requiring_Multiple_Fonts() throws TaskException {
        MergeParameters params = new MergeParameters();
        params.setTableOfContentsPolicy(ToCPolicy.FILE_NAMES);
        PDDocument doc = new PDDocument();
        TableOfContentsCreator victim = new TableOfContentsCreator(params, doc);
        victim.appendItem("Item multiple fonts ทดสอบ", 10, new PDPage());
        victim.pageSizeIfNotSet(PDRectangle.A4);
        victim.addToC();

        TestUtils.assertPageTextExactLines(doc.getPage(0), "Item multiple fonts ทดสอบ   10\n");
    }

    @Test(expected = UnsupportedTextException.class)
    public void tocItemsMultipleFontsButNotFound() throws TaskException {
        MergeParameters params = new MergeParameters();
        params.setTableOfContentsPolicy(ToCPolicy.FILE_NAMES);
        PDDocument doc = new PDDocument();
        TableOfContentsCreator victim = new TableOfContentsCreator(params, doc);
        victim.appendItem("Item multiple fonts հայերէն", 10, new PDPage());
        victim.pageSizeIfNotSet(PDRectangle.A4);
        victim.addToC();
    }

    @Test
    public void test_Toc_Add_At_Specific_Page() throws TaskException {
        MergeParameters params = new MergeParameters();
        params.setTableOfContentsPolicy(ToCPolicy.FILE_NAMES);
        params.setBlankPageIfOdd(true);

        PDDocument doc = new PDDocument();
        PDPage pageA = new PDPage(), pageB = new PDPage();
        doc.addPage(pageA);
        doc.addPage(pageB);

        PageTextWriter.writeHeader(doc, pageA, "PageA");
        PageTextWriter.writeHeader(doc, pageB, "PageB");

        TableOfContentsCreator victim = new TableOfContentsCreator(params, doc);
        victim.appendItem("This is an item", 2, pageB);
        victim.pageSizeIfNotSet(PDRectangle.A4);

        victim.addToC(1);

        assertThat(doc.getNumberOfPages(), is(4)); // one extra for blank page if odd

        // blank page pageA
        TestUtils.assertPageTextExactLines(doc.getPage(0), "PageA\n");
        // toc
        TestUtils.assertPageTextExactLines(doc.getPage(1), "This is an item   2\n");
        // extra blank page if odd
        TestUtils.assertPageTextExactLines(doc.getPage(2), "");
        // blank page pageB
        TestUtils.assertPageTextExactLines(doc.getPage(3), "PageB\n");
    }

    @Test
    public void tocItemsLinkWithRotatedPages() throws TaskException, IOException {
        MergeParameters params = new MergeParameters();
        params.setTableOfContentsPolicy(ToCPolicy.FILE_NAMES);
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        PDPage page90 = new PDPage(PDRectangle.A4);
        page90.setRotation(90);
        PDPage page180 = new PDPage(PDRectangle.A4);
        page180.setRotation(180);
        PDPage page270 = new PDPage(PDRectangle.A4);
        page270.setRotation(270);
        TableOfContentsCreator victim = new TableOfContentsCreator(params, doc);
        victim.appendItem("Item ", 1, page);
        victim.appendItem("Item 90", 2, page90);
        victim.appendItem("Item 180", 3, page180);
        victim.appendItem("Item 270", 4, page270);
        victim.addToC();

        List<PDAnnotationLink> annotations = TestUtils.getAnnotationsOf(doc.getPage(0), PDAnnotationLink.class);
        PDAnnotationLink link = annotations.get(0);
        assertNotNull(link);
        assertThat(link.getDestination(), instanceOf(PDPageXYZDestination.class));
        assertEquals(0, ((PDPageXYZDestination) link.getDestination()).getLeft());
        assertEquals((int) PDRectangle.A4.getHeight(), ((PDPageXYZDestination) link.getDestination()).getTop());

        PDAnnotationLink link90 = annotations.get(1);
        assertNotNull(link90);
        assertThat(link90.getDestination(), instanceOf(PDPageXYZDestination.class));
        assertEquals(0, ((PDPageXYZDestination) link90.getDestination()).getLeft());
        assertEquals(0, ((PDPageXYZDestination) link90.getDestination()).getTop());

        PDAnnotationLink link180 = annotations.get(2);
        assertNotNull(link180);
        assertThat(link180.getDestination(), instanceOf(PDPageXYZDestination.class));
        assertEquals((int) PDRectangle.A4.getWidth(), ((PDPageXYZDestination) link180.getDestination()).getLeft());
        assertEquals(0, ((PDPageXYZDestination) link180.getDestination()).getTop());

        PDAnnotationLink link270 = annotations.get(3);
        assertNotNull(link270);
        assertThat(link270.getDestination(), instanceOf(PDPageXYZDestination.class));
        assertEquals((int) PDRectangle.A4.getWidth(), ((PDPageXYZDestination) link270.getDestination()).getLeft());
        assertEquals((int) PDRectangle.A4.getHeight(), ((PDPageXYZDestination) link270.getDestination()).getTop());

    }
}
