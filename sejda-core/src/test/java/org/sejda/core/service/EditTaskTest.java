/*
 * Copyright 2016 by Eduard Weissmann (edi.weissmann@gmail.com).
 *
 * This file is part of the Sejda source code
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.core.service;

import org.junit.Ignore;
import org.junit.Test;
import org.sejda.model.RectangularBox;
import org.sejda.model.TopLeftRectangularBox;
import org.sejda.model.input.Source;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.EditParameters;
import org.sejda.model.parameter.edit.*;
import org.sejda.model.parameter.edit.Shape;
import org.sejda.model.pdf.StandardType1Font;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Ignore
public abstract class EditTaskTest extends BaseTaskTest<EditParameters> {

    private EditParameters parameters;
    public static final Point TEXT_EDIT_POSITION = new Point(10, 10);
    public static final Point IMAGE_POSITION = new Point(10, 10);

    public static final int IMAGE_WIDTH = 124;
    public static final int IMAGE_HEIGHT = 52;

    private EditParameters basicText(String text) throws IOException {
        return basicText(text, new PageRange(1, 1));
    }

    private EditParameters basicText(String text, PageRange pageRange) throws IOException {
        EditParameters parameters = new EditParameters();
        AppendTextOperation textOperation = new AppendTextOperation(text, StandardType1Font.HELVETICA_BOLD_OBLIQUE,
                12, Color.RED, TEXT_EDIT_POSITION, pageRange);
        parameters.addAppendTextOperation(textOperation);

        testContext.directoryOutputTo(parameters);
        parameters.setOutputPrefix("test_file[FILENUMBER]");
        parameters.addSource(customInput("pdf/test_file.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        return parameters;
    }

    private EditParameters basicAddImage(PageRange pageRange) throws IOException {
        EditParameters parameters = new EditParameters();
        Source<?> imageSource = customNonPdfInput("image/draft.png");
        AddImageOperation imageOperation = new AddImageOperation(imageSource, IMAGE_WIDTH, IMAGE_HEIGHT, IMAGE_POSITION, pageRange);
        parameters.addImageOperation(imageOperation);

        testContext.directoryOutputTo(parameters);
        parameters.setOutputPrefix("test_file[FILENUMBER]");
        parameters.addSource(customInput("pdf/test_file.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        return parameters;
    }

    private AppendTextOperation textOperationForPage(String text, int page) {
        return new AppendTextOperation(text, StandardType1Font.HELVETICA_BOLD_OBLIQUE,
                12, Color.RED, TEXT_EDIT_POSITION, new PageRange(page, page));
    }

    private EditParameters rotatedDocumentAddImage() throws IOException {
        EditParameters parameters = basicAddImage(new PageRange(1));
        parameters.removeAllSources();
        parameters.setOutputPrefix("test_file[FILENUMBER]");
        parameters.addSource(customInput("pdf/rotated_pages.pdf"));
        return parameters;
    }

    @Test
    public void testUnicodeCharacters() throws Exception {
        parameters = basicText("Mirëdita Καλώς góðan dobrý");
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertTextEditAreaHasText(d.getPage(0),
                    "Mirëdita Καλώς góðan dobrý");
        });
    }

    @Test
    public void testTextWithUnicodeNonBreakingSpace() throws Exception {
        parameters = basicText("This is\u00A0non breaking space");
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertTextEditAreaHasText(d.getPage(0),
                    "This is non breaking space");
        });
    }

    @Test
    public void testThaiCharacters() throws Exception {
        parameters = basicText("นี่คือการทดสอบ");
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertTextEditAreaHasText(d.getPage(0), "นี่คือการทดสอบ");

        });
    }

    @Test
    public void testPageRange() throws Exception {
        parameters = basicText("Sample text here", new PageRange(2));
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertTextEditAreaHasText(d.getPage(0), "");
            assertTextEditAreaHasText(d.getPage(1), "Sample text here");
            assertTextEditAreaHasText(d.getPage(2), "Sample text here");
        });
    }

    @Test
    public void testDocumentWithRotatedPagesHeader() throws Exception {
        parameters = basicText("Sample text here", new PageRange(1));
        parameters.removeAllSources();
        parameters.addSource(customInput("pdf/rotated_pages.pdf"));
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertTextEditAreaHasText(d.getPage(1), "S a m p l e t e x t h e r e");
            assertTextEditAreaHasText(d.getPage(2), "Sample text here");
            assertTextEditAreaHasText(d.getPage(3), "Sample text here");
            assertTextEditAreaHasText(d.getPage(4), "Sample text here");
        });
    }

    @Test
    public void testDocumentWithCroppedPagesHeader() throws Exception {
        parameters = basicText("Sample text here", new PageRange(1));
        parameters.removeAllSources();
        parameters.addSource(customInput("pdf/cropped_test_file.pdf"));

        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertTextEditAreaHasText(d.getPage(0), "Sample text here");
        });
    }

    @Test
    public void testDocumentWithRotatedCroppedPagesHeader() throws Exception {
        parameters = basicText("Sample text here", new PageRange(1));
        parameters.removeAllSources();
        parameters.addSource(customInput("pdf/rotated_cropped_test_file.pdf"));

        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertTextEditAreaHasText(d.getPage(0), "Sample text here");
        });
    }

    @Test
    public void testAddingPngImage() throws Exception {
        parameters = basicAddImage(new PageRange(1));
        execute(parameters);
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertImageAtLocation(d, d.getPage(1), IMAGE_POSITION, IMAGE_WIDTH, IMAGE_HEIGHT);
        });
        testContext.assertTaskCompleted();
    }

    @Test
    public void testAddingImageToRotatedDocumentPages() throws Exception {
        parameters = rotatedDocumentAddImage();
        execute(parameters);
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertImageAtLocation(d, d.getPage(1), IMAGE_POSITION, IMAGE_WIDTH, IMAGE_HEIGHT);
            assertImageAtLocation(d, d.getPage(2), IMAGE_POSITION, IMAGE_WIDTH, IMAGE_HEIGHT);
            assertImageAtLocation(d, d.getPage(3), IMAGE_POSITION, IMAGE_WIDTH, IMAGE_HEIGHT);
            assertImageAtLocation(d, d.getPage(4), IMAGE_POSITION, IMAGE_WIDTH, IMAGE_HEIGHT);
        });
        testContext.assertTaskCompleted();
    }

    @Test
    public void testAddingBlankPageWithImageAndTextAndRemovingPage() throws Exception {
        parameters = basicAddImage(new PageRange(1, 1));
        parameters.addAppendTextOperation(new AppendTextOperation("Sample text", StandardType1Font.HELVETICA_BOLD_OBLIQUE, 12, Color.RED, TEXT_EDIT_POSITION, new PageRange(1, 1)));
        parameters.addInsertPageOperation(new InsertPageOperation(1));
        parameters.addDeletePageOperation(new DeletePageOperation(1));
        parameters.addDeletePageOperation(new DeletePageOperation(1));

        // delete page operations get processed first
        // then add page operations
        // last the text & image operations

        execute(parameters);
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertThat(d.getNumberOfPages(), is(4));
            assertTextEditAreaHasText(d.getPage(0), "Sample text");
            assertImageAtLocation(d, d.getPage(0), IMAGE_POSITION, IMAGE_WIDTH, IMAGE_HEIGHT);
        });
        testContext.assertTaskCompleted();
    }

    @Test
    public void testRemovingMultiplePages() throws Exception {
        EditParameters parameters = new EditParameters();
        parameters.addDeletePageOperation(new DeletePageOperation(1));
        parameters.addDeletePageOperation(new DeletePageOperation(3));
        parameters.addDeletePageOperation(new DeletePageOperation(4));

        testContext.directoryOutputTo(parameters);
        parameters.setOutputPrefix("test_file[FILENUMBER]");
        parameters.addSource(customInput("pdf/test_file.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);

        execute(parameters);
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertThat(d.getNumberOfPages(), is(1));
        });
        testContext.assertTaskCompleted();
    }

    @Test
    public void testInsertPageBeforeFirst() throws Exception {
        EditParameters parameters = new EditParameters();
        parameters.addInsertPageOperation(new InsertPageOperation(1));
        parameters.addAppendTextOperation(textOperationForPage("Page 1 text", 1));

        testContext.directoryOutputTo(parameters);
        parameters.setOutputPrefix("test_file[FILENUMBER]");
        parameters.addSource(customInput("pdf/one_page.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);

        execute(parameters);
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertThat(d.getNumberOfPages(), is(2));
            assertTextEditAreaHasText(d.getPage(0), "Page 1 text");
        });
        testContext.assertTaskCompleted();
    }

    @Test
    public void testInsertPageAfterFirst() throws Exception {
        EditParameters parameters = new EditParameters();
        parameters.addInsertPageOperation(new InsertPageOperation(2));
        parameters.addAppendTextOperation(textOperationForPage("Page 2 text", 2));

        testContext.directoryOutputTo(parameters);
        parameters.setOutputPrefix("test_file[FILENUMBER]");
        parameters.addSource(customInput("pdf/one_page.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);

        execute(parameters);
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertThat(d.getNumberOfPages(), is(2));
            assertTextEditAreaHasText(d.getPage(1), "Page 2 text");
        });
        testContext.assertTaskCompleted();
    }

    @Test
    public void testInsertPageAfterLast() throws Exception {
        EditParameters parameters = new EditParameters();
        parameters.addInsertPageOperation(new InsertPageOperation(5));
        parameters.addAppendTextOperation(textOperationForPage("Page 5 text", 5));

        testContext.directoryOutputTo(parameters);
        parameters.setOutputPrefix("test_file[FILENUMBER]");
        parameters.addSource(customInput("pdf/test_file.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);

        execute(parameters);
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertThat(d.getNumberOfPages(), is(5));
            assertTextEditAreaHasText(d.getPage(4), "Page 5 text");
        });
        testContext.assertTaskCompleted();
    }

    @Test
    public void highlightText() throws IOException {
        parameters = basicText("Sample text here");
        parameters.addHighlightTextOperation(new HighlightTextOperation(1, new HashSet<RectangularBox>() {{
            add(RectangularBox.newInstance(10, 10, 30, 200));
        }}, Color.YELLOW));

        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertThat(d.getPage(0).getAnnotations().size(), is(1));
        });
    }

    @Test
    public void drawShapes() throws IOException {
        parameters = basicText("Shapes");
        parameters.addShapeOperation(new AddShapeOperation(Shape.ELLIPSE, 100, 200, new Point(100, 100), new PageRange(1, 1), 1, Color.DARK_GRAY, null));
        parameters.addShapeOperation(new AddShapeOperation(Shape.RECTANGLE, 100, 200, new Point(10, 10), new PageRange(2, 2), 1, Color.RED, Color.BLUE));

        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.forPdfOutput("test_file1.pdf", d -> {
            // TODO: assert shapes are there
        });
    }

    @Test
    public void testReplaceOneAndOnlyPage() throws Exception {
        EditParameters parameters = new EditParameters();
        parameters.addDeletePageOperation(new DeletePageOperation(1));
        parameters.addInsertPageOperation(new InsertPageOperation(1));
        parameters.addAppendTextOperation(textOperationForPage("Page 1 text", 1));

        testContext.directoryOutputTo(parameters);
        parameters.setOutputPrefix("test_file[FILENUMBER]");
        parameters.addSource(customInput("pdf/one_page.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);

        execute(parameters);
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertThat(d.getNumberOfPages(), is(1));
            assertTextEditAreaHasText(d.getPage(0), "Page 1 text");
        });
        testContext.assertTaskCompleted();
    }

    @Test
    public void testEditExistingText() throws Exception {
        EditParameters parameters = new EditParameters();
        TopLeftRectangularBox redactArea = new TopLeftRectangularBox(70, 94, 205, 21);
        parameters.addEditTextOperation(new EditTextOperation("Redacted out :)", redactArea, new PageRange(1, 1)));

        testContext.directoryOutputTo(parameters);

        parameters.setOutputPrefix("test_file[FILENUMBER]");
        parameters.addSource(customInput("pdf/paragraphs.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);

        execute(parameters);
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertThat(d.getNumberOfPages(), is(1));
            assertTextAreaHasText(d.getPage(0), "Redacted out :)", redactArea);
            assertPageTextDoesNotContain(d.getPage(0), "What is Lorem Ipsum?");
        });
        testContext.assertTaskCompleted();
    }

    @Test
    public void testEditExistingText_rotated() throws Exception {
        EditParameters parameters = new EditParameters();
        TopLeftRectangularBox redactArea = new TopLeftRectangularBox(129, 33, 246, 21);
        parameters.addEditTextOperation(new EditTextOperation("Redacted out :)", redactArea, new PageRange(1, 1)));

        testContext.directoryOutputTo(parameters);

        parameters.setOutputPrefix("test_file[FILENUMBER]");
        parameters.addSource(customInput("pdf/rotated_paragraphs.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);

        execute(parameters);
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertThat(d.getNumberOfPages(), is(1));
            assertTextAreaHasText(d.getPage(0), "Redacted out :)", redactArea);
            assertPageTextDoesNotContain(d.getPage(0), "This is red text on a rotated page");
        });
        testContext.assertTaskCompleted();
    }

    @Test
    public void testEditTextWithinXForms() throws Exception {
        EditParameters parameters = new EditParameters();
        TopLeftRectangularBox redactArea1 = new TopLeftRectangularBox(681, 70, 169, 28);
        TopLeftRectangularBox redactArea2 = new TopLeftRectangularBox(680, 189, 104, 16);
        parameters.addEditTextOperation(new EditTextOperation("Redacted out :)", redactArea1, new PageRange(1, 1)));
        parameters.addEditTextOperation(new EditTextOperation("Another replacement", redactArea2, new PageRange(1, 1)));

        testContext.directoryOutputTo(parameters);

        parameters.setOutputPrefix("test_file[FILENUMBER]");
        parameters.addSource(customInput("pdf/2-up-sample.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);

        execute(parameters);
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertPageTextDoesNotContain(d.getPage(0), "Why do we use it?");
            assertTextAreaHasText(d.getPage(0), "Redacted out :)", redactArea1);
            assertPageTextDoesNotContain(d.getPage(0), "humour and the like");
            assertTextAreaHasText(d.getPage(0), "Another replacement", redactArea2);
        });
        testContext.assertTaskCompleted();
    }

    @Ignore("When doc is rotated and cropped somehow the position is shifted, incorrect")
    @Test
    public void testEditExistingText_rotatedCropped() throws Exception {
        EditParameters parameters = new EditParameters();
        TopLeftRectangularBox redactArea = new TopLeftRectangularBox(60, 5, 246, 21);
        parameters.addEditTextOperation(new EditTextOperation("This is red", redactArea, new PageRange(1, 1)));

        testContext.directoryOutputTo(parameters);

        parameters.setOutputPrefix("test_file[FILENUMBER]");
        parameters.addSource(customInput("pdf/cropped_rotated_paragraphs.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);

        execute(parameters);
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertPageTextDoesNotContain(d.getPage(0), "This is red text on a rotated page.");
            assertTextAreaHasText(d.getPage(0), "This is red", redactArea);
        });
        testContext.assertTaskCompleted();
    }

    @Test
    public void testEditExistingTextWithSubsetFontKeepsOriginalFont() throws Exception {
        EditParameters parameters = new EditParameters();
        TopLeftRectangularBox redactArea = new TopLeftRectangularBox(325, 36, 36, 15);
        parameters.addEditTextOperation(new EditTextOperation("Tootal", redactArea, new PageRange(1, 1)));

        testContext.directoryOutputTo(parameters);

        parameters.setOutputPrefix("test_file[FILENUMBER]");
        parameters.addSource(customInput("pdf/tabular-data.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);

        execute(parameters);
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertPageTextDoesNotContain(d.getPage(0), "Total");
            assertTextAreaHasText(d.getPage(0), "Tootal", redactArea);
            assertThat("No new fonts are added => original font is reused", size(d.getPage(0).getResources().getFontNames().iterator()), is(2));
        });
        testContext.assertTaskCompleted();
    }

    @Test
    public void testEditExistingTextWithSubsetFontUsesReplacementFont() throws Exception {
        EditParameters parameters = new EditParameters();
        TopLeftRectangularBox redactArea = new TopLeftRectangularBox(325, 36, 36, 15);
        parameters.addEditTextOperation(new EditTextOperation("XYZW", redactArea, new PageRange(1, 1)));

        testContext.directoryOutputTo(parameters);

        parameters.setOutputPrefix("test_file[FILENUMBER]");
        parameters.addSource(customInput("pdf/tabular-data.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);

        execute(parameters);
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertPageTextDoesNotContain(d.getPage(0), "Total");
            assertTextAreaHasText(d.getPage(0), "XYZW", redactArea);
            assertThat("New fonts are added => original font is not reused", size(d.getPage(0).getResources().getFontNames().iterator()), is(3));
        });
        testContext.assertTaskCompleted();
    }

    @Test
    public void testEditExistingTextWithFontThatHasNoSpaceGlyph() throws Exception {
        EditParameters parameters = new EditParameters();
        TopLeftRectangularBox redactArea = new TopLeftRectangularBox(100, 77, 108, 16);
        parameters.addEditTextOperation(new EditTextOperation("Cdoul Cmpu", redactArea, new PageRange(1, 1)));

        testContext.directoryOutputTo(parameters);

        parameters.setOutputPrefix("test_file[FILENUMBER]");
        parameters.addSource(customInput("pdf/no-space-in-font.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);

        execute(parameters);
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertPageTextDoesNotContain(d.getPage(0), "Cloud Copmuting");
            assertTextAreaHasText(d.getPage(0), "Cdoul Cmpu", redactArea);
        });
        testContext.assertTaskCompleted();
    }

    @Test
    public void testEditExistingTextWithPartialMatchInShowTextAdjustedOperand() throws Exception {
        EditParameters parameters = new EditParameters();
        TopLeftRectangularBox redactArea = new TopLeftRectangularBox(174, 60, 107, 26);
        TopLeftRectangularBox secondArea = new TopLeftRectangularBox(393, 58, 76, 26);
        parameters.addEditTextOperation(new EditTextOperation("Hi", redactArea, new PageRange(1, 1)));

        testContext.directoryOutputTo(parameters);

        parameters.setOutputPrefix("test_file[FILENUMBER]");
        parameters.addSource(customInput("pdf/show-text-adjusted.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);

        execute(parameters);
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertPageTextDoesNotContain(d.getPage(0), "He llo World!");
            assertTextAreaHasText(d.getPage(0), "Hi", redactArea);
            assertTextAreaHasText(d.getPage(0), "World!", secondArea);
        });
        testContext.assertTaskCompleted();
    }

    @Test
    public void testEditExistingTextWhenBoundingBoxOverlapsOtherTextPartially() throws Exception {
        EditParameters parameters = new EditParameters();
        TopLeftRectangularBox redactArea = new TopLeftRectangularBox(69, 126, 69, 22);
        TopLeftRectangularBox secondArea = new TopLeftRectangularBox(69, 140, 142, 17);
        parameters.addEditTextOperation(new EditTextOperation("ASDFGH", redactArea, new PageRange(1, 1)));

        testContext.directoryOutputTo(parameters);

        parameters.setOutputPrefix("test_file[FILENUMBER]");
        parameters.addSource(customInput("pdf/paragraphs.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);

        execute(parameters);
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertPageTextDoesNotContain(d.getPage(0), "Lorem Ipsum is simply dummy");
            assertTextAreaHasText(d.getPage(0), "ASDFGH", redactArea);
            assertTextAreaHasText(d.getPage(0), "been the industry's standard", secondArea);
        });
        testContext.assertTaskCompleted();
    }

    /**
     * Returns the number of elements remaining in {@code iterator}. The iterator
     * will be left exhausted: its {@code hasNext()} method will return
     * {@code false}.
     */
    public static int size(Iterator<?> iterator) {
        int count = 0;
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        return count;
    }

    protected abstract void assertPageTextDoesNotContain(PDPage page, String expectedNotFoundText);

    protected abstract void assertTextAreaHasText(PDPage page, String expectedText, TopLeftRectangularBox area);

    protected abstract void assertTextEditAreaHasText(PDPage page, String expectedText);

    protected abstract void assertImageAtLocation(PDDocument Doc, PDPage page, Point2D position, int width, int height);
}
