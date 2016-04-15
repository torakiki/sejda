/*
 * Copyright 2012 by Eduard Weissmann (edi.weissmann@gmail.com).
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
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.EditParameters;
import org.sejda.model.parameter.edit.AddTextOperation;
import org.sejda.model.pdf.StandardType1Font;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.sambox.pdmodel.PDPage;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;

@Ignore
public abstract class EditTaskTest extends BaseTaskTest<EditParameters> {

    private EditParameters parameters;
    public static final Point2D TEXT_EDIT_POSITION = new Point(10, 10);

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

    protected abstract void assertTextEditAreaHasText(PDPage page, String expectedText);
}
