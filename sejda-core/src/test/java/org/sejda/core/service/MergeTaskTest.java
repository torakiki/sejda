/*
 * Created on 16/ago/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.sejda.model.input.PdfMergeInput;
import org.sejda.model.outline.OutlinePolicy;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.MergeParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.form.AcroFormPolicy;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.sambox.pdmodel.PDDocument;

/**
 * Test for the merge task
 * 
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class MergeTaskTest extends BaseTaskTest<MergeParameters> {

    private MergeParameters parameters;

    private void setUpParameters(List<PdfMergeInput> input) {
        parameters = new MergeParameters();
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setCompress(false);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        for (PdfMergeInput current : input) {
            parameters.addInput(current);
        }
        parameters.setOutlinePolicy(OutlinePolicy.RETAIN);
    }

    private List<PdfMergeInput> getInputWithOutline() {
        List<PdfMergeInput> input = new ArrayList<PdfMergeInput>();
        input.add(new PdfMergeInput(largeOutlineInput()));
        input.add(new PdfMergeInput(largeInput()));
        return input;
    }

    private List<PdfMergeInput> getInputWithEncrypted() {
        List<PdfMergeInput> input = new ArrayList<PdfMergeInput>();
        input.add(new PdfMergeInput(stronglyEncryptedInput()));
        input.add(new PdfMergeInput(largeInput()));
        return input;
    }

    private List<PdfMergeInput> getInput() {
        List<PdfMergeInput> input = new ArrayList<PdfMergeInput>();
        input.add(new PdfMergeInput(regularInput()));
        input.add(new PdfMergeInput(customInput("pdf/attachments_as_annots.pdf")));
        return input;
    }

    @Test
    public void executeMergeAllWithOutlineRetainingOutline() throws IOException {
        setUpParameters(getInputWithOutline());
        doExecuteMergeAll(true, 311);
    }

    @Test
    public void executeMergeAllWithEncryptedRetainingOutline() throws IOException {
        setUpParameters(getInputWithEncrypted());
        doExecuteMergeAll(true, 310);
    }

    @Test
    public void executeMergeAllRetainingOutline() throws IOException {
        setUpParameters(getInput());
        doExecuteMergeAll(false, 14);
    }

    @Test
    public void executeMergeAllWithOutlineDiscardingOutline() throws IOException {
        setUpParameters(getInputWithOutline());
        parameters.setOutlinePolicy(OutlinePolicy.DISCARD);
        doExecuteMergeAll(false, 311);
    }

    @Test
    public void executeMergeAllDiscardingOutline() throws IOException {
        setUpParameters(getInput());
        parameters.setOutlinePolicy(OutlinePolicy.DISCARD);
        doExecuteMergeAll(false, 14);
    }

    @Test
    public void executeMergeAllWithEncryptedDiscardingOutline() throws IOException {
        setUpParameters(getInputWithEncrypted());
        parameters.setOutlinePolicy(OutlinePolicy.DISCARD);
        doExecuteMergeAll(false, 310);
    }

    @Test
    public void executeMergeAllWithOutlineOnePerDoc() throws IOException {
        setUpParameters(getInputWithOutline());
        parameters.setOutlinePolicy(OutlinePolicy.ONE_ENTRY_EACH_DOC);
        doExecuteMergeAll(true, 311);
    }

    @Test
    public void executeMergeAllOnePerDoc() throws IOException {
        setUpParameters(getInput());
        parameters.setOutlinePolicy(OutlinePolicy.ONE_ENTRY_EACH_DOC);
        doExecuteMergeAll(true, 14);
    }

    @Test
    public void executeMergeAllWithEncryptedOnePerDoc() throws IOException {
        setUpParameters(getInputWithEncrypted());
        parameters.setOutlinePolicy(OutlinePolicy.ONE_ENTRY_EACH_DOC);
        doExecuteMergeAll(true, 310);
    }

    @Test
    public void executeMergeAllStreamOutput() throws IOException {
        setUpParameters(getInput());
        testContext.streamOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertPages(14);
    }

    void doExecuteMergeAll(boolean hasBookmarks, int pages) throws IOException {
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertPages(pages)
                .assertHasOutline(hasBookmarks);
    }

    @Test
    public void testExecuteMergeAllFields() throws IOException {
        setUpParameters(getInputWithOutline());
        parameters.addInput(new PdfMergeInput(customInput("pdf/forms/simple_form.pdf")));
        parameters.setOutlinePolicy(OutlinePolicy.DISCARD);
        parameters.setAcroFormPolicy(AcroFormPolicy.MERGE);
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertPages(312).assertVersion(PdfVersion.VERSION_1_6).assertHasOutline(false)
                .assertHasAcroforms(true);
    }

    @Test
    public void testExecuteMergeDiscardForms() throws IOException {
        setUpParameters(getInputWithOutline());
        parameters.addInput(new PdfMergeInput(customInput("pdf/forms/simple_form.pdf")));

        parameters.setOutlinePolicy(OutlinePolicy.DISCARD);
        parameters.setAcroFormPolicy(AcroFormPolicy.DISCARD);
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertPages(312).assertVersion(PdfVersion.VERSION_1_6).assertHasOutline(false)
                .assertHasAcroforms(false);
    }

    @Test
    public void executeMergeRangesMergeForms() throws IOException {
        setUpParameters(getInputWithOutline());
        for (PdfMergeInput input : parameters.getInputList()) {
            input.addPageRange(new PageRange(3, 10));
            input.addPageRange(new PageRange(20, 23));
            input.addPageRange(new PageRange(80, 90));
        }
        parameters.setAcroFormPolicy(AcroFormPolicy.MERGE);
        parameters.addInput(new PdfMergeInput(customInput("pdf/forms/simple_form.pdf")));
        doExecuteMergeRanges();
    }

    @Test
    public void executeMergeRanges() throws IOException {
        setUpParameters(getInputWithOutline());
        for (PdfMergeInput input : parameters.getInputList()) {
            input.addPageRange(new PageRange(3, 10));
            input.addPageRange(new PageRange(20, 23));
            input.addPageRange(new PageRange(80, 90));
        }
        parameters.setAcroFormPolicy(AcroFormPolicy.DISCARD);
        parameters.addInput(new PdfMergeInput(customInput("pdf/forms/simple_form.pdf")));
        doExecuteMergeRanges();
    }

    public void doExecuteMergeRanges() throws IOException {
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertPages(27).assertVersion(PdfVersion.VERSION_1_6)
                .assertOutlineContains("Bookmark27").assertOutlineDoesntContain("Bookmark1");
    }

    @Test
    public void testExecuteMergeRangesWithBlankPage() throws IOException {
        setUpParameters(getInputWithOutline());
        testContext.pdfOutputTo(parameters);
        for (PdfMergeInput input : parameters.getInputList()) {
            input.addPageRange(new PageRange(2, 4));
        }
        parameters.setBlankPageIfOdd(true);
        execute(parameters);
        PDDocument document = testContext.assertTaskCompleted();
        testContext.assertCreator().assertPages(8).assertVersion(PdfVersion.VERSION_1_6);
        assertEquals(document.getPage(2).getCropBox().getWidth(), document.getPage(3).getCropBox().getWidth(), 0);
        assertEquals(document.getPage(2).getCropBox().getHeight(), document.getPage(3).getCropBox().getHeight(), 0);
        assertEquals(document.getPage(6).getCropBox().getWidth(), document.getPage(7).getCropBox().getWidth(), 0);
        assertEquals(document.getPage(6).getCropBox().getHeight(), document.getPage(7).getCropBox().getHeight(), 0);

    }
}
