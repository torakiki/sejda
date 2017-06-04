/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com)
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.sejda.model.input.PdfSource;
import org.sejda.model.outline.OutlinePolicy;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.CombineReorderParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.rotation.Rotation;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.sejda.sambox.text.PDFTextStripper;

@Ignore
public abstract class CombineReorderTaskTest extends BaseTaskTest<CombineReorderParameters> {

    private CombineReorderParameters parameters;

    private void setUpParameters(List<PdfSource<?>> sources) {
        parameters = new CombineReorderParameters();
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.addSources(sources);
    }

    private List<PdfSource<?>> basicInputs() {
        List<PdfSource<?>> input = new ArrayList<PdfSource<?>>();
        input.add(customInput("pdf/multipage-test-a.pdf"));
        input.add(customInput("pdf/multipage-test-b.pdf"));
        return input;
    }

    @Test
    public void combineAndReorder() throws IOException {
        setUpParameters(basicInputs());
        parameters.addPage(0, 1);
        parameters.addPage(0, 2);
        parameters.addPage(0, 3);
        parameters.addPage(1, 1);
        parameters.addPage(1, 2);
        parameters.addPage(1, 3);
        parameters.addPage(0, 4);
        parameters.addPage(1, 4);
        parameters.addPage(1, 10);
        parameters.addPage(1, 11);
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        PDDocument outDocument = testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6);

        assertPageHasText(outDocument, 1, "1a");
        assertPageHasText(outDocument, 2, "2a");
        assertPageHasText(outDocument, 3, "3a");
        assertPageHasText(outDocument, 4, "1b");
        assertPageHasText(outDocument, 5, "2b");
        assertPageHasText(outDocument, 6, "3b");
        assertPageHasText(outDocument, 7, "4a");
        assertPageHasText(outDocument, 8, "4b");
        assertPageHasText(outDocument, 9, "10b");
        assertPageHasText(outDocument, 10, "11b");
    }

    @Test
    public void addingBlankPages() throws IOException {
        setUpParameters(basicInputs());
        parameters.addPage(-1, -1);
        parameters.addPage(0, 1);
        parameters.addPage(1, 1);
        parameters.addPage(-1, -1);
        parameters.addPage(0, 3);

        testContext.pdfOutputTo(parameters);
        execute(parameters);
        PDDocument outDocument = testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6);

        assertPageHasText(outDocument, 1, "");
        assertPageHasText(outDocument, 2, "1a");
        assertPageHasText(outDocument, 3, "1b");
        assertPageHasText(outDocument, 4, "");
        assertPageHasText(outDocument, 5, "3a");

        testContext.forEachPdfOutput(d -> {
            assertEquals(d.getPage(0).getMediaBox(), PDRectangle.A4);
            assertEquals(d.getPage(2).getMediaBox(), d.getPage(3).getMediaBox());
        });
    }

    @Test
    public void combineAndReorderWithRotation() throws IOException {
        setUpParameters(basicInputs());
        parameters.addPage(0, 1);
        parameters.addPage(1, 1, Rotation.DEGREES_90);
        parameters.addPage(0, 2, Rotation.DEGREES_180);
        parameters.addPage(1, 2, Rotation.DEGREES_270);
        parameters.addPage(0, 3);
        parameters.addPage(1, 3, Rotation.DEGREES_90);

        testContext.pdfOutputTo(parameters);
        execute(parameters);
        PDDocument outDocument = testContext.assertTaskCompleted();

        assertPageHasText(outDocument, 1, "1a");
        assertPageHasText(outDocument, 2, "1b");
        assertPageHasText(outDocument, 3, "2a");
        assertPageHasText(outDocument, 4, "2b");
        assertPageHasText(outDocument, 5, "3a");
        assertPageHasText(outDocument, 6, "3b");

        testContext.assertCreator().assertPages(6).forEachPdfOutput(d -> {
            assertEquals(90, d.getPage(1).getRotation());
            assertEquals(180, d.getPage(2).getRotation());
            assertEquals(270, d.getPage(3).getRotation());
            assertEquals(90, d.getPage(5).getRotation());
        });
    }

    @Test
    public void keepsOutline() throws IOException {
        List<PdfSource<?>> inputs = new ArrayList<PdfSource<?>>();
        inputs.add(customInput("pdf/large_outline.pdf"));
        inputs.add(customInput("pdf/test_outline.pdf"));
        setUpParameters(inputs);

        parameters.setOutlinePolicy(OutlinePolicy.RETAIN);

        parameters.addPage(0, 3);
        parameters.addPage(1, 3);

        parameters.addPage(0, 2);
        parameters.addPage(1, 2);

        parameters.addPage(0, 1);
        parameters.addPage(1, 1);

        testContext.pdfOutputTo(parameters);
        execute(parameters);

        PDDocument outDocument = testContext.assertTaskCompleted();
        PDDocumentOutline outline = outDocument.getDocumentCatalog().getDocumentOutline();

        assertNotNull("Has outline", outline);
        for(int i = 1; i < 34; i++) {
            testContext.assertOutlineContains("Bookmark" + i);
        }
        for(int i = 34; i <= 49; i++) {
            testContext.assertOutlineDoesntContain("Bookmark" + i);
        }
        testContext.assertOutlineContains("Test first level.");
        testContext.assertOutlineContains("Secondpage test first level.");
    }

    @Test
    public void discardsOutline() throws IOException {
        List<PdfSource<?>> inputs = new ArrayList<PdfSource<?>>();
        inputs.add(customInput("pdf/large_outline.pdf"));
        inputs.add(customInput("pdf/test_outline.pdf"));
        setUpParameters(inputs);

        parameters.setOutlinePolicy(OutlinePolicy.DISCARD);

        parameters.addPage(0, 3);
        parameters.addPage(1, 3);

        parameters.addPage(0, 2);
        parameters.addPage(1, 2);

        parameters.addPage(0, 1);
        parameters.addPage(1, 1);

        testContext.pdfOutputTo(parameters);
        execute(parameters);

        testContext.assertTaskCompleted();
        testContext.assertHasOutline(false);
    }

    void assertPageHasText(PDDocument doc, int page, String expected) throws IOException {
        PDFTextStripper textStripper = new PDFTextStripper();
        textStripper.setStartPage(page);
        textStripper.setEndPage(page);
        String actual = textStripper.getText(doc);

        int[] num = new int[actual.length()];

        for (int i = 0; i < actual.length(); i++) {
            num[i] = actual.charAt(i);
        }
        assertEquals("Page " + page + " text doesn't match", expected, actual.replaceAll("[^A-Za-z0-9]", ""));
    }
}
