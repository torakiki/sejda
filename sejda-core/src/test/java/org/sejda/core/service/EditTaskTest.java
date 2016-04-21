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
import org.sejda.model.input.Source;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.EditParameters;
import org.sejda.model.parameter.edit.AddImageOperation;
import org.sejda.model.parameter.edit.AddPageOperation;
import org.sejda.model.parameter.edit.AddTextOperation;
import org.sejda.model.parameter.edit.DeletePageOperation;
import org.sejda.model.pdf.StandardType1Font;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Ignore
public abstract class EditTaskTest extends BaseTaskTest<EditParameters> {

    private EditParameters parameters;
    public static final Point2D TEXT_EDIT_POSITION = new Point(10, 10);
    public static final Point2D IMAGE_POSITION = new Point(10, 10);

    public static final int IMAGE_WIDTH = 124;
    public static final int IMAGE_HEIGHT = 52;

    private EditParameters basicText(String text) throws IOException {
        return basicText(text, new PageRange(1, 1));
    }

    private EditParameters basicText(String text, PageRange pageRange) throws IOException {
        EditParameters parameters = new EditParameters();
        AddTextOperation textOperation = new AddTextOperation(text, StandardType1Font.HELVETICA_BOLD_OBLIQUE,
                12, Color.RED, TEXT_EDIT_POSITION, pageRange);
        parameters.addTextOperation(textOperation);

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

    private EditParameters rotatedDocumentAddImage() throws IOException {
        EditParameters parameters = basicAddImage(new PageRange(1));
        parameters.removeAllSources();
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
            assertTextEditAreaHasText(d.getPage(1), "S a m p l e  t e x t  h e r e");
            assertTextEditAreaHasText(d.getPage(2), "Sample text here");
            assertTextEditAreaHasText(d.getPage(3), "Sample text here");
            assertTextEditAreaHasText(d.getPage(4), "Sample text here");
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
        parameters.addTextOperation(new AddTextOperation("Sample text", StandardType1Font.HELVETICA_BOLD_OBLIQUE, 12, Color.RED, TEXT_EDIT_POSITION, new PageRange(1, 1)));
        parameters.addAddPageOperation(new AddPageOperation(1));
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

    protected abstract void assertTextEditAreaHasText(PDPage page, String expectedText);

    protected abstract void assertImageAtLocation(PDDocument Doc, PDPage page, Point2D position, int width, int height);
}
