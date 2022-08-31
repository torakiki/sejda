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

import org.junit.jupiter.api.Test;
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
import org.sejda.tests.TestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.sejda.tests.TestUtils.assertDocTextExactLines;
import static org.sejda.tests.TestUtils.assertPageTextExactLines;
import static org.sejda.tests.TestUtils.getDocTextNormalized;
import static org.sejda.tests.TestUtils.getPageTextNormalized;

/**
 * @author Andrea Vacondio
 * @author Edi Weissmann
 */
public class TableOfContentsCreatorTest {

    @Test
    public void nullDocument() {
        assertThrows(IllegalArgumentException.class, () -> new TableOfContentsCreator(new MergeParameters(), null));
    }

    @Test
    public void nullParams() {
        assertThrows(IllegalArgumentException.class, () -> new TableOfContentsCreator(null, new PDDocument()));
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

    @Test
    public void testAppendItemInvalidString() {
        assertThrows(IllegalArgumentException.class,
                () -> new TableOfContentsCreator(new MergeParameters(), new PDDocument()).appendItem(" ", 10,
                        new PDPage()));
    }

    @Test
    public void testAppendItemInvalidPage() {
        assertThrows(IllegalArgumentException.class,
                () -> new TableOfContentsCreator(new MergeParameters(), new PDDocument()).appendItem("Text", -10,
                        new PDPage()));
    }

    @Test
    public void testAppendItemInvalidAnnotation() {
        assertThrows(IllegalArgumentException.class,
                () -> new TableOfContentsCreator(new MergeParameters(), new PDDocument()).appendItem("Text", 10, null));
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
    public void testAddToCTop() throws IOException, TaskException {
        PDDocument doc = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/test_outline.pdf")));
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
        PDDocument doc = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/test_outline.pdf")));
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
        PDDocument doc = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/test_outline.pdf")));
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
        TableOfContentsCreator victim = newToCCreator("This is a very long file name and we expect that it's handled correctly and no Exception is thrown by the component. We are making this very very long so we can make sure that even the veeeery long ones are handled.");
        
        victim.addToC();
        
        assertEquals(1, victim.getDoc().getNumberOfPages());
    }

    @Test
    public void testToCPageSize() throws TaskException {
        TableOfContentsCreator victim = newToCCreator();
        victim.pageSizeIfNotSet(PDRectangle.LETTER);
        victim.addToC();
        assertEquals(PDRectangle.LETTER, victim.getDoc().getPage(0).getMediaBox());
    }

    @Test
    public void testToCForLargePageSize() throws TaskException {
        TableOfContentsCreator victim = newToCCreator();
        victim.pageSizeIfNotSet(PDRectangle.A1);
        victim.addToC();
        assertEquals(39.64, victim.getFontSize(), 0.1);
    }

    @Test
    public void testStringsThatMixMultipleFontRequirements() throws TaskException {
        TableOfContentsCreator victim = newToCCreator("1-abc-עברית");
        victim.addToC();
        assertEquals(1, victim.getDoc().getNumberOfPages());
    }

    @Test
    public void indexPageIsConsideredInPageNumbers() throws IOException, TaskException {
        TableOfContentsCreator victim = newToCCreator(ToCPolicy.DOC_TITLES, 
                Arrays.asList("This is a very long file name and we expect that it's handled correctly and no Exception is thrown by the component. We are making this very very long so we can make sure that even the veeeery long ones are handled."));
        
        victim.addToC();
        assertThat(getDocTextNormalized(victim.getDoc()), endsWith("veeeery long ones are handled.   101\n"));
    }

    @Test
    public void testTocNumberOfPagesNoToc() throws TaskException {
        TableOfContentsCreator victim = newToCCreator(ToCPolicy.NONE, Arrays.asList("sample title"));
        int tocNumPages = victim.addToC();
        assertEquals(0, tocNumPages);
        assertEquals(0, victim.getDoc().getNumberOfPages());
    }

    @Test
    public void testTocNumberOfPages() throws TaskException {
        TableOfContentsCreator victim = newToCCreator("sample title");
        int tocNumPages = victim.addToC();
        assertEquals(1, tocNumPages);
        assertEquals(1, victim.getDoc().getNumberOfPages());
    }

    @Test
    public void testTocNumberOfPages_AddBlankOption() throws TaskException {
        MergeParameters params = new MergeParameters();
        params.setBlankPageIfOdd(true);
        params.setTableOfContentsPolicy(ToCPolicy.DOC_TITLES);
        params.addInput(new PdfMergeInput(mock(PdfFileSource.class)));
        PDDocument doc = new PDDocument();
        TableOfContentsCreator victim = new TableOfContentsCreator(params, doc);
        victim.appendItem("sample title", 1, new PDPage());
        int tocNumPages = victim.addToC();
        assertEquals(2, tocNumPages);
        assertEquals(2, doc.getNumberOfPages());
    }

    @Test
    public void testTocNumberOfPages_Multiple() throws TaskException {
        TableOfContentsCreator victim = newToCCreator();
        victim.pageSizeIfNotSet(PDRectangle.A4);
        for (int i = 1; i < 40; i++) {
            victim.appendItem("entry " + i, i, new PDPage());
        }

        int tocNumPages = victim.addToC();
        assertEquals(2, tocNumPages);
        assertEquals(2, victim.getDoc().getNumberOfPages());
    }

    @Test
    public void testTocNumberOfPagesMultipleInA2() throws TaskException {
        TableOfContentsCreator victim = newToCCreator();
        victim.pageSizeIfNotSet(PDRectangle.A2);
        for (int i = 1; i < 40; i++) {
            victim.appendItem("sample " + i + ".pdf", i + 1, new PDPage());
        }
        
        // ToC font is scaled so we get 2 pages even if a2 is twice the a4 height
        int tocNumPages = victim.addToC();
        assertEquals(2, tocNumPages);
        assertEquals(2, victim.getDoc().getNumberOfPages());
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

        assertPageTextExactLines(doc.getPage(0),
                "This is item 1   2\n" + "This is item 2 that has a very long name and should not be\n"
                        + "truncated so that the version is visible at the end v7.pdf   11\n"
                        + "This is item 3   15\n");

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
        TableOfContentsCreator victim = newToCCreator();
        for (int i = 0; i < 30; i++) {
            victim.appendItem("This is an item", 1, new PDPage());
        }

        victim.appendItem(
                "This is a long item that has a very long name and should not be truncated so that the version is visible at the end v7.pdf",
                10, new PDPage());
        victim.appendItem("This is an item", 14, new PDPage());
        victim.pageSizeIfNotSet(PDRectangle.A4);
        victim.addToC();

        TestUtils.assertPageTextDoesNotContain(victim.getDoc().getPage(0), "This is a long item that");
        TestUtils.assertPageTextContains(victim.getDoc().getPage(1), "This is a long item that");
    }

    @Test
    public void test_Toc_Long_Item_That_Has_No_Word_Breaks() throws TaskException {
        TableOfContentsCreator victim = newToCCreator("This_is_a_file_that_has_a_very_long_name_and_should_not_be_truncated_so_that_the_version_is_visible_at_the_end_v7.pdf");
        victim.pageSizeIfNotSet(PDRectangle.A4);
        victim.addToC();

        assertPageTextExactLines(victim.getDoc().getPage(0),
                "This_is_a_file_that_has_a_very_long_name_and_should_not_be-\n"
                        + "_truncated_so_that_the_version_is_visible_at_the_end_v7.pdf   101\n");
    }

    @Test
    public void test_Toc_Item_Requiring_Multiple_Fonts() throws TaskException {
        TableOfContentsCreator victim = newToCCreator("Item multiple fonts ทดสอบ");
        victim.pageSizeIfNotSet(PDRectangle.A4);
        victim.addToC();

        assertPageTextExactLines(victim.getDoc().getPage(0), "Item multiple fonts ทดสอบ   101\n");
    }

    @Test
    public void tocItemsMultipleFontsButNotFound() throws TaskException {
        TableOfContentsCreator victim = newToCCreator();
        victim.appendItem("Item multiple fonts հայերէն", 10, new PDPage());
        victim.pageSizeIfNotSet(PDRectangle.A4);
        assertThrows(UnsupportedTextException.class, () -> victim.addToC());
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
        assertPageTextExactLines(doc.getPage(0), "PageA\n");
        // toc
        assertPageTextExactLines(doc.getPage(1), "This is an item   4\n");
        // extra blank page if odd
        assertPageTextExactLines(doc.getPage(2), "");
        // blank page pageB
        assertPageTextExactLines(doc.getPage(3), "PageB\n");
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

    @Test
    public void testLongTocItemsThatWrapAndGenerateMultiplePagesOfToC_HardToEstimateToCNumberOfPages() throws TaskException, IOException {
        List<String> entries = new ArrayList<>();
        for(int i = 1; i <= 26; i++) {
            if(i % 3 == 0) {
                entries.add("Attachment " + i + " - Sample file name - longer item that might wrap on the next line");    
            } else {
                entries.add("Attachment " + i + " - Sample file name - shorter");
            }
        }
        
        TableOfContentsCreator victim = newToCCreator(entries);
        victim.pageSizeIfNotSet(PDRectangle.A4);

        victim.addToC();
        
        assertThat(victim.getDoc().getNumberOfPages(), is(2));
        
        String expected = "Attachment 1 - Sample file name - shorter   102\n" +
                "Attachment 2 - Sample file name - shorter   103\n" +
                "Attachment 3 - Sample file name - longer item that might wrap on\n" +
                "the next line   104\n" +
                "Attachment 4 - Sample file name - shorter   105\n" +
                "Attachment 5 - Sample file name - shorter   106\n" +
                "Attachment 6 - Sample file name - longer item that might wrap on\n" +
                "the next line   107\n" +
                "Attachment 7 - Sample file name - shorter   108\n" +
                "Attachment 8 - Sample file name - shorter   109\n" +
                "Attachment 9 - Sample file name - longer item that might wrap on\n" +
                "the next line   110\n" +
                "Attachment 10 - Sample file name - shorter   111\n" +
                "Attachment 11 - Sample file name - shorter   112\n" +
                "Attachment 12 - Sample file name - longer item that might wrap\n" +
                "on the next line   113\n" +
                "Attachment 13 - Sample file name - shorter   114\n" +
                "Attachment 14 - Sample file name - shorter   115\n" +
                "Attachment 15 - Sample file name - longer item that might wrap\n" +
                "on the next line   116\n" +
                "Attachment 16 - Sample file name - shorter   117\n" +
                "Attachment 17 - Sample file name - shorter   118\n" +
                "Attachment 18 - Sample file name - longer item that might wrap\n" +
                "on the next line   119\n" +
                "Attachment 19 - Sample file name - shorter   120\n" +
                "Attachment 20 - Sample file name - shorter   121\n" +
                "Attachment 21 - Sample file name - longer item that might wrap\n" +
                "on the next line   122\n" +
                "Attachment 22 - Sample file name - shorter   123\n" +
                "Attachment 23 - Sample file name - shorter   124\n" +
                "Attachment 24 - Sample file name - longer item that might wrap\n" +
                "on the next line   125\n" +
                "Attachment 25 - Sample file name - shorter   126\n" +
                "Attachment 26 - Sample file name - shorter   127\n";

        assertDocTextExactLines(victim.getDoc(), expected);
    }

    @Test
    public void lastItemDoesNotFitPage_WrapsToNextPage() throws TaskException, IOException {
        List<String> entries = new ArrayList<>();
        for(int i = 1; i <= 22; i++) {
            if(i % 3 == 0) {
                entries.add("Attachment " + i + " - Sample file name - longer item that might wrap on the next line");
            } else {
                entries.add("Attachment " + i + " - Sample file name - shorter");
            }
        }

        // this item will not fit the 1st TOC page, will be added to the 2nd
        entries.add("Attachment final - Sample file name - longer item that might wrap on the next line");

        TableOfContentsCreator victim = newToCCreator(entries);
        victim.pageSizeIfNotSet(PDRectangle.A4);

        victim.addToC();
        
        assertThat(victim.getDoc().getNumberOfPages(), is(2));
        assertPageTextExactLines(victim.getDoc().getPage(1), "Attachment final - Sample file name - longer item that might wrap\non the next line   124\n");
    }

    @Test
    public void lastItemBarelyFitsPage_NoWrapToNextPage() throws TaskException, IOException {
        List<String> entries = new ArrayList<>();
        for(int i = 1; i <= 22; i++) {
            if(i % 3 == 0) {
                entries.add("Attachment " + i + " - Sample file name - longer item that might wrap on the next line");
            } else {
                entries.add("Attachment " + i + " - Sample file name - shorter");
            }
        }

        // this item will fit the 1st TOC page
        entries.add("Attachment final - Sample file name - short and fits");

        TableOfContentsCreator victim = newToCCreator(entries);
        victim.pageSizeIfNotSet(PDRectangle.A4);

        victim.addToC();

        assertThat(victim.getDoc().getNumberOfPages(), is(1));
        assertThat(getPageTextNormalized(victim.getDoc().getPage(0)),
                endsWith("\nAttachment final - Sample file name - short and fits   123\n"));
    }

    @Test
    public void catchesWrongUsage_ItemAddedAfterTocGenerated() throws TaskException {
        TableOfContentsCreator victim = newToCCreator();
        victim.addToC();
        assertThrows(IllegalStateException.class, () -> victim.appendItem("cannot add more", 2, new PDPage()));
    }

    private TableOfContentsCreator newToCCreator() {
        return newToCCreator("test");
    }

    private TableOfContentsCreator newToCCreator(String item) {
        return newToCCreator(Collections.singletonList(item));
    }

    private TableOfContentsCreator newToCCreator(List<String> items) {
        return newToCCreator(ToCPolicy.FILE_NAMES, items);
    }

    private TableOfContentsCreator newToCCreator(ToCPolicy toCPolicy, List<String> items) {
        MergeParameters params = new MergeParameters();
        params.setTableOfContentsPolicy(toCPolicy);

        return newToCCreator(params, items);
    }

    private TableOfContentsCreator newToCCreator(MergeParameters params, List<String> items) {
        PDDocument doc = new PDDocument();
        TableOfContentsCreator victim = new TableOfContentsCreator(params, doc);
        
        for (int i = 0; i < items.size(); i++) {
            victim.appendItem(items.get(i), 100 + i, new PDPage());    
        }
        
        return  victim;
    }
}
