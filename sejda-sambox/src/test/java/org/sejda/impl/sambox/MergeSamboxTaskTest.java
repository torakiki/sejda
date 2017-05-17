/*
 * Created on 04/set/2015
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
package org.sejda.impl.sambox;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.sejda.core.TestListenerFactory;
import org.sejda.core.TestListenerFactory.TestListenerFailed;
import org.sejda.core.notification.context.ThreadLocalNotificationContext;
import org.sejda.core.service.BaseTaskTest;
import org.sejda.impl.sambox.component.PdfTextExtractorByArea;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.input.ImageMergeInput;
import org.sejda.model.input.PdfMergeInput;
import org.sejda.model.outline.OutlinePolicy;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.MergeParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.form.AcroFormPolicy;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.task.Task;
import org.sejda.model.toc.ToCPolicy;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.text.PDFTextStripperByArea;

/**
 * @author Andrea Vacondio
 *
 */
public class MergeSamboxTaskTest extends BaseTaskTest<MergeParameters> {
    @Override
    public Task<MergeParameters> getTask() {
        return new MergeTask();
    }

    private MergeParameters setUpParameters(List<PdfMergeInput> input) {
        MergeParameters parameters = new MergeParameters();
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setCompress(false);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        for (PdfMergeInput current : input) {
            parameters.addInput(current);
        }
        parameters.setOutlinePolicy(OutlinePolicy.RETAIN);
        return parameters;
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
        input.add(new PdfMergeInput(customInput("pdf/attachments_as_annots.pdf", "attachments_as_annots.pdf")));
        return input;
    }

    @Test
    public void executeMergeAllWithOutlineRetainingOutline() throws IOException {
        doExecuteMergeAll(true, 311, setUpParameters(getInputWithOutline()));
    }

    @Test
    public void executeMergeAllWithEncryptedRetainingOutline() throws IOException {
        doExecuteMergeAll(true, 310, setUpParameters(getInputWithEncrypted()));
    }

    @Test
    public void executeMergeAllRetainingOutline() throws IOException {
        doExecuteMergeAll(false, 14, setUpParameters(getInput()));
    }

    @Test
    public void executeMergeAllRetainingOutlineTocNames() throws IOException {
        MergeParameters parameters = setUpParameters(getInput());
        parameters.addInput(new PdfMergeInput(customInput("pdf/with_meta.pdf")));
        parameters.setTableOfContentsPolicy(ToCPolicy.FILE_NAMES);
        doExecuteMergeAll(false, 19, parameters);
    }

    @Test
    public void executeMergeRotatedTocPage() throws IOException {
        MergeParameters parameters = new MergeParameters();
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setCompress(false);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setOutlinePolicy(OutlinePolicy.RETAIN);
        parameters.addInput(new PdfMergeInput(customInput("pdf/rotated_pages.pdf", "name.pdf")));
        parameters.setTableOfContentsPolicy(ToCPolicy.DOC_TITLES);
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.forPdfOutput(d -> {
            assertEquals(new PDRectangle(0, 0, 595, 842).rotate(90).toString(), d.getPage(0).getMediaBox().toString());
        });
    }

    @Test
    public void executeMergeAllRetainingOutlineTocTitles() throws IOException {
        MergeParameters parameters = setUpParameters(getInput());
        parameters.addInput(new PdfMergeInput(customInput("pdf/with_meta.pdf")));
        parameters.setTableOfContentsPolicy(ToCPolicy.DOC_TITLES);
        doExecuteMergeAll(false, 19, parameters);
    }

    @Test
    public void executeMergeAllRetainingOutlineTocNamesUTF() throws IOException {
        MergeParameters parameters = setUpParameters(getInput());
        parameters.addInput(new PdfMergeInput(customInput("pdf/with_meta.pdf", "αυτό είναι ένα τεστ.pdf")));
        parameters.setTableOfContentsPolicy(ToCPolicy.FILE_NAMES);
        doExecuteMergeAll(false, 19, parameters);
    }

    @Test
    public void executeMergeAllRetainingOutlineTocNamesUTFThaiAndHindi() throws IOException {
        MergeParameters parameters = setUpParameters(getInput());
        parameters.addInput(new PdfMergeInput(customInput("pdf/with_meta.pdf", "นี่คือการทดสอบ.pdf")));
        parameters.addInput(new PdfMergeInput(customInput("pdf/with_meta.pdf", "यह एक परीक्षण है.pdf")));
        parameters.setTableOfContentsPolicy(ToCPolicy.FILE_NAMES);
        parameters.setFilenameFooter(true);
        doExecuteMergeAll(false, 23, parameters);
    }

    @Test
    public void executeMergeAllWithOutlineDiscardingOutline() throws IOException {
        MergeParameters parameters = setUpParameters(getInputWithOutline());
        parameters.setOutlinePolicy(OutlinePolicy.DISCARD);
        doExecuteMergeAll(false, 311, parameters);
    }

    @Test
    public void executeMergeAllDiscardingOutline() throws IOException {
        MergeParameters parameters = setUpParameters(getInput());
        parameters.setOutlinePolicy(OutlinePolicy.DISCARD);
        doExecuteMergeAll(false, 14, parameters);
    }

    @Test
    public void executeMergeAllWithEncryptedDiscardingOutline() throws IOException {
        MergeParameters parameters = setUpParameters(getInputWithEncrypted());
        parameters.setOutlinePolicy(OutlinePolicy.DISCARD);
        doExecuteMergeAll(false, 310, parameters);
    }

    @Test
    public void executeMergeAllWithOutlineOnePerDoc() throws IOException {
        MergeParameters parameters = setUpParameters(getInputWithOutline());
        parameters.setOutlinePolicy(OutlinePolicy.ONE_ENTRY_EACH_DOC);
        doExecuteMergeAll(true, 311, parameters);
    }

    @Test
    public void executeMergeAllOnePerDoc() throws IOException {
        MergeParameters parameters = setUpParameters(getInput());
        parameters.setOutlinePolicy(OutlinePolicy.ONE_ENTRY_EACH_DOC);
        doExecuteMergeAll(true, 14, parameters);
    }

    @Test
    public void executeMergeAllWithEncryptedOnePerDoc() throws IOException {
        MergeParameters parameters = setUpParameters(getInputWithEncrypted());
        parameters.setOutlinePolicy(OutlinePolicy.ONE_ENTRY_EACH_DOC);
        doExecuteMergeAll(true, 310, parameters);
    }

    void doExecuteMergeAll(boolean hasBookmarks, int pages, MergeParameters parameters) throws IOException {
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertPages(pages)
                .assertHasOutline(hasBookmarks);
    }

    @Test
    public void testExecuteMergeAllFields() throws IOException {
        MergeParameters parameters = setUpParameters(getInputWithOutline());
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
        MergeParameters parameters = setUpParameters(getInputWithOutline());
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
    public void testExecuteMergeFlattenForms() throws IOException {
        MergeParameters parameters = setUpParameters(getInputWithOutline());
        parameters.addInput(new PdfMergeInput(customInput("pdf/forms/simple_form.pdf")));

        parameters.setOutlinePolicy(OutlinePolicy.DISCARD);
        parameters.setAcroFormPolicy(AcroFormPolicy.FLATTEN);
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertPages(312).assertVersion(PdfVersion.VERSION_1_6).assertHasOutline(false)
                .assertHasAcroforms(false);
    }

    @Test
    public void testExecuteMergeFlattenFormsWithUnicodeValues() throws IOException {
        MergeParameters parameters = setUpParameters(
                Collections.singletonList(new PdfMergeInput(customInput("pdf/forms/simple_form_unicode_values.pdf"))));

        parameters.setAcroFormPolicy(AcroFormPolicy.FLATTEN);
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertPages(1).assertHasAcroforms(false);
        testContext.forEachPdfOutput(doc -> {
            assertPageTextContains(doc.getPage(0), "ጩ");
        });
    }

    @Test
    public void executeMergeRangesMergeForms() throws IOException {
        MergeParameters parameters = setUpParameters(getInputWithOutline());
        for (PdfMergeInput input : parameters.getPdfInputList()) {
            input.addPageRange(new PageRange(3, 10));
            input.addPageRange(new PageRange(20, 23));
            input.addPageRange(new PageRange(80, 90));
        }
        parameters.setAcroFormPolicy(AcroFormPolicy.MERGE);
        parameters.addInput(new PdfMergeInput(customInput("pdf/forms/simple_form.pdf")));
        doExecuteMergeRanges(parameters);
    }

    @Test
    public void executeMergeRanges() throws IOException {
        MergeParameters parameters = setUpParameters(getInputWithOutline());
        for (PdfMergeInput input : parameters.getPdfInputList()) {
            input.addPageRange(new PageRange(3, 10));
            input.addPageRange(new PageRange(20, 23));
            input.addPageRange(new PageRange(80, 90));
        }
        parameters.setAcroFormPolicy(AcroFormPolicy.DISCARD);
        parameters.addInput(new PdfMergeInput(customInput("pdf/forms/simple_form.pdf")));
        doExecuteMergeRanges(parameters);
    }

    public void doExecuteMergeRanges(MergeParameters parameters) throws IOException {
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertPages(27).assertVersion(PdfVersion.VERSION_1_6)
                .assertOutlineContains("Bookmark27").assertOutlineDoesntContain("Bookmark1");
    }

    @Test
    public void testExecuteMergeRangesWithBlankPage() throws IOException {
        MergeParameters parameters = setUpParameters(getInputWithOutline());
        testContext.pdfOutputTo(parameters);
        for (PdfMergeInput input : parameters.getPdfInputList()) {
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

    @Test
    public void testExecuteMergeRangesWithBlankPagesAndToc() throws IOException {
        MergeParameters parameters = setUpParameters(getInputWithOutline());
        testContext.pdfOutputTo(parameters);
        for (PdfMergeInput input : parameters.getPdfInputList()) {
            input.addPageRange(new PageRange(2, 4));
        }
        parameters.setBlankPageIfOdd(true);
        parameters.setTableOfContentsPolicy(ToCPolicy.FILE_NAMES);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertPages(10);
    }

    @Test
    public void testExecuteMergeRangesWithFlattenForms() throws IOException {
        List<PdfMergeInput> inputs = new ArrayList<PdfMergeInput>();
        inputs.add(new PdfMergeInput(customInput("pdf/forms/simple_form_with_values.pdf")));
        MergeParameters parameters = setUpParameters(inputs);
        parameters.setAcroFormPolicy(AcroFormPolicy.FLATTEN);
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        PDDocument document = testContext.assertTaskCompleted();
        PDPage page = document.getPage(0);
        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
        stripper.addRegion("completePage",
                new Rectangle((int) page.getCropBox().getWidth(), (int) page.getCropBox().getHeight()));
        stripper.extractRegions(page);
        String pageText = stripper.getTextForRegion("completePage");

        assertThat(pageText, containsString("TextFieldValue"));
    }

    @Test
    public void executeMergeMissingPageNonLenient() throws IOException {
        MergeParameters parameters = new MergeParameters();
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setCompress(false);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setOutlinePolicy(OutlinePolicy.RETAIN);
        parameters.addInput(new PdfMergeInput(customInput("pdf/missing_page_ref.pdf", "name.pdf")));
        testContext.pdfOutputTo(parameters);
        TestListenerFailed failListener = TestListenerFactory.newFailedListener();
        ThreadLocalNotificationContext.getContext().addListener(failListener);
        execute(parameters);
        assertTrue(failListener.isFailed());
    }

    @Test
    public void executeMergeMissingPageLenient() throws IOException {
        MergeParameters parameters = new MergeParameters();
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setCompress(false);
        parameters.setLenient(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setOutlinePolicy(OutlinePolicy.RETAIN);
        parameters.addInput(new PdfMergeInput(customInput("pdf/missing_page_ref.pdf", "name.pdf")));
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertPages(3).assertVersion(PdfVersion.VERSION_1_6);
    }

    @Test
    public void normalizePageSizes_FirstPagePortrait() throws IOException {
        MergeParameters parameters = new MergeParameters();
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.addInput(new PdfMergeInput(customInput("pdf/A4Portrait.pdf")));
        parameters.addInput(new PdfMergeInput(customInput("pdf/A3Landscape.pdf")));
        parameters.addInput(new PdfMergeInput(customInput("pdf/A3Portrait.pdf")));
        parameters.setNormalizePageSizes(true);

        testContext.pdfOutputTo(parameters);
        execute(parameters);

        testContext.assertTaskCompleted();
        testContext.assertPages(3).forEachPdfOutput(d -> {
            assertEquals(595, widthOfCropBox(d.getPage(0)), 1);

            // landscape should be handled in a special case
            assertEquals(595, heightOfCropBox(d.getPage(1)), 1);
            assertEquals(840, widthOfCropBox(d.getPage(1)), 1);

            assertEquals(595, widthOfCropBox(d.getPage(2)), 1);
        });
    }

    @Test
    public void normalizePageSizes_FirstPageLandscape() throws IOException {
        MergeParameters parameters = new MergeParameters();
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.addInput(new PdfMergeInput(customInput("pdf/A3Landscape.pdf")));
        parameters.addInput(new PdfMergeInput(customInput("pdf/A4Portrait.pdf")));
        parameters.addInput(new PdfMergeInput(customInput("pdf/A3Portrait.pdf")));
        parameters.setNormalizePageSizes(true);

        testContext.pdfOutputTo(parameters);
        execute(parameters);

        testContext.assertTaskCompleted();
        testContext.assertPages(3).forEachPdfOutput(d -> {
            assertEquals(1190, widthOfCropBox(d.getPage(0)), 1);

            // landscape should be handled in a special case
            assertEquals(1190, heightOfCropBox(d.getPage(1)), 1);
            assertEquals(841, widthOfCropBox(d.getPage(1)), 1);

            assertEquals(841, widthOfCropBox(d.getPage(2)), 1);
        });
    }

    @Test
    public void pageFooter() throws IOException {
        MergeParameters parameters = setUpParameters(getInput());
        parameters.setTableOfContentsPolicy(ToCPolicy.NONE);
        parameters.setFilenameFooter(true);
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertPages(14).forEachPdfOutput(d -> {
            assertFooterHasText(d.getPage(0), "test-file 1");
            assertFooterHasText(d.getPage(11), "attachments_as_annots 12");
        });
    }

    @Test
    public void pageFooterAndToc() throws IOException {
        MergeParameters parameters = setUpParameters(getInput());
        parameters.setTableOfContentsPolicy(ToCPolicy.FILE_NAMES);
        parameters.setFilenameFooter(true);
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertPages(15).forEachPdfOutput(d -> {
            assertFooterHasText(d.getPage(1), "test-file 2");
            assertFooterHasText(d.getPage(12), "attachments_as_annots 13");
        });
    }

    @Test
    public void pageFooterAndTocAddBlank() throws IOException {
        MergeParameters parameters = setUpParameters(getInput());
        parameters.setTableOfContentsPolicy(ToCPolicy.FILE_NAMES);
        parameters.setFilenameFooter(true);
        parameters.setBlankPageIfOdd(true);
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertPages(18).forEachPdfOutput(d -> {
            try {
                assertFalse(isBlankPage(d.getPage(0)));
                assertTrue(isBlankPage(d.getPage(1)));
                assertFooterHasText(d.getPage(2), "test-file 3");
                assertFooterHasText(d.getPage(15), "attachments_as_annots 16");
            } catch (TaskIOException e) {
                fail(e.getMessage());
            }
        });
    }

    @Test
    public void mergeImagesAndPdfs() throws IOException {
        MergeParameters parameters = new MergeParameters();
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.addInput(new ImageMergeInput(customNonPdfInput("image/draft.png")));
        parameters.addInput(new PdfMergeInput(customInput("pdf/test-pdf.pdf")));
        parameters.addInput(new ImageMergeInput(customNonPdfInput("image/draft.png")));
        parameters.addInput(new ImageMergeInput(customNonPdfInput("image/large.jpg")));
        parameters.addInput(new ImageMergeInput(customNonPdfInput("image/draft.tiff")));
        parameters.addInput(new PdfMergeInput(customInput("pdf/test-pdf.pdf")));
        parameters.addInput(new ImageMergeInput(customNonPdfInput("image/draft.png")));

        testContext.pdfOutputTo(parameters);
        execute(parameters);

        testContext.assertTaskCompleted();
        testContext.assertPages(1 + 11 + 3 + 11 + 1).forEachPdfOutput(d -> {
            assertEquals(Arrays.asList(1, 13, 14, 15, 27), getPagesContainingImages(d));
        });
    }

    @Test
    public void mergeImagesWithTocAndFooter() throws IOException {
        MergeParameters parameters = new MergeParameters();
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.addInput(new ImageMergeInput(customNonPdfInput("image/draft.png", "draft.png")));
        parameters.addInput(new ImageMergeInput(customNonPdfInput("image/large.jpg", "large.png")));
        parameters.addInput(new ImageMergeInput(customNonPdfInput("image/draft.tiff", "draft.tiff")));

        parameters.setTableOfContentsPolicy(ToCPolicy.DOC_TITLES);
        parameters.setFilenameFooter(true);

        testContext.pdfOutputTo(parameters);
        execute(parameters);

        testContext.assertTaskCompleted();
        testContext.assertPages(1 + 3).forEachPdfOutput(d -> {
            assertPageText(d.getPage(0), "draft2large3draft4");

            assertFooterHasText(d.getPage(1), "draft 2");
            assertFooterHasText(d.getPage(2), "large 3");
            assertFooterHasText(d.getPage(3), "draft 4");
        });
    }

    private float widthOfCropBox(PDPage page) {
        return page.getCropBox().getWidth();
    }

    private float heightOfCropBox(PDPage page) {
        return page.getCropBox().getHeight();
    }

    private void assertFooterHasText(PDPage page, String expectedText) {
        try {
            assertThat(new PdfTextExtractorByArea().extractFooterText(page).trim(), is(expectedText));
        } catch (TaskIOException e) {
            fail(e.getMessage());
        }
    }

    private boolean isBlankPage(PDPage page) throws TaskIOException {
        return StringUtils.isBlank(new PdfTextExtractorByArea()
                .extractTextFromArea(page,
                        new Rectangle(0, 0, (int) page.getTrimBox().getWidth(), (int) page.getTrimBox().getHeight()))
                .trim());
    }
}
