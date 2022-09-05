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

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;
import org.sejda.core.Sejda;
import org.sejda.impl.sambox.component.PdfTextExtractorByArea;
import org.sejda.model.exception.InvalidTaskParametersException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.input.ImageMergeInput;
import org.sejda.model.input.MergeInput;
import org.sejda.model.input.PdfMergeInput;
import org.sejda.model.outline.CatalogPageLabelsPolicy;
import org.sejda.model.outline.OutlinePolicy;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.MergeParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.form.AcroFormPolicy;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.rotation.Rotation;
import org.sejda.model.task.Task;
import org.sejda.model.toc.ToCPolicy;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PageNotFoundException;
import org.sejda.sambox.pdmodel.common.PDPageLabelRange;
import org.sejda.sambox.pdmodel.common.PDPageLabels;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.interactive.form.PDField;
import org.sejda.sambox.text.PDFTextStripperByArea;
import org.sejda.tests.DocBuilder;
import org.sejda.tests.tasks.BaseTaskTest;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.sejda.tests.TestUtils.assertPageLabelIndexesAre;
import static org.sejda.tests.TestUtils.assertPageLabelRangeIs;
import static org.sejda.tests.TestUtils.customInput;
import static org.sejda.tests.TestUtils.customNonPdfInput;
import static org.sejda.tests.TestUtils.encryptedAtRest;
import static org.sejda.tests.TestUtils.largeInput;
import static org.sejda.tests.TestUtils.largeOutlineInput;
import static org.sejda.tests.TestUtils.regularInput;
import static org.sejda.tests.TestUtils.shortInput;
import static org.sejda.tests.TestUtils.stronglyEncryptedInput;

/**
 * @author Andrea Vacondio
 */
public class MergeSamboxTaskTest extends BaseTaskTest<MergeParameters> {
    @Override
    public Task<MergeParameters> getTask() {
        return new MergeTask();
    }

    private MergeParameters setUpParameters(List<? extends MergeInput> input) {
        MergeParameters parameters = new MergeParameters();
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setCompress(false);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        for (MergeInput current : input) {
            parameters.addInput(current);
        }
        parameters.setOutlinePolicy(OutlinePolicy.RETAIN);
        return parameters;
    }

    private List<PdfMergeInput> getInputWithOutline() {
        List<PdfMergeInput> input = new ArrayList<>();
        input.add(new PdfMergeInput(largeOutlineInput()));
        input.add(new PdfMergeInput(largeInput()));
        return input;
    }

    private List<PdfMergeInput> getInputWithEncrypted() {
        List<PdfMergeInput> input = new ArrayList<>();
        input.add(new PdfMergeInput(stronglyEncryptedInput()));
        input.add(new PdfMergeInput(largeInput()));
        return input;
    }

    private List<PdfMergeInput> getInput() {
        List<PdfMergeInput> input = new ArrayList<>();
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
    public void executeMergeAllRetainingOutlineTocNamesWhenNamesAreVeryLong() throws IOException {
        MergeParameters parameters = setUpParameters(getInput());
        String longFilename = "This is a file that has a very long name and should not be truncated so that the version is visible at the end (but when applied to the footer the name should be truncated not to cover the page number) v7";
        parameters.addInput(new PdfMergeInput(customInput("pdf/with_meta.pdf", longFilename + ".pdf")));
        parameters.setTableOfContentsPolicy(ToCPolicy.FILE_NAMES);
        parameters.setFilenameFooter(true);
        doExecuteMergeAll(false, 19, parameters);

        testContext.forPdfOutput(d -> {
            assertPageTextContains(d.getPage(0), longFilename);
            assertFooterHasText(d.getPage(16), longFilename.substring(0, 124) + " 17");
        });
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
        testContext.forPdfOutput(d -> assertEquals(new PDRectangle(0, 0, 595, 842).rotate(90).toString(),
                d.getPage(0).getMediaBox().toString()));
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
    public void executeMergeAllTocNamesNoFont() throws IOException {
        MergeParameters parameters = setUpParameters(getInput());
        parameters.addInput(new PdfMergeInput(customInput("pdf/with_meta.pdf", "հայերէն.pdf")));
        parameters.setTableOfContentsPolicy(ToCPolicy.FILE_NAMES);
        doExecuteMergeAll(false, 18, parameters);
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
    public void testExecuteMergeFieldsWithDots() throws IOException {
        MergeParameters parameters = new MergeParameters();
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.addInput(new PdfMergeInput(customInput("pdf/forms/simple_form_with_dot_partial_name.pdf")));
        parameters.addInput(new PdfMergeInput(customInput("pdf/forms/simple_form_with_dot_partial_name.pdf")));
        parameters.setOutlinePolicy(OutlinePolicy.RETAIN);
        parameters.setAcroFormPolicy(AcroFormPolicy.MERGE_RENAMING_EXISTING_FIELDS);

        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertHasAcroforms(true);
        testContext.forEachPdfOutput(doc -> {
            // original field
            assertEquals(1, findFieldsNamedExact("Choice_Caption_0wUBrGuJDKIWD9g7kWcKpg.withdot", doc).size());
            // renamed field
            assertEquals(1, findFieldsMatching("Choice_Caption_0wUBrGuJDKIWD9g7kWcKpgwithdot", doc).size());
        });
    }

    @Test
    public void testExecuteMergeFieldsWithSameNameDifferentKinds() throws IOException {
        MergeParameters parameters = new MergeParameters();
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.addInput(new PdfMergeInput(customInput("pdf/forms/form_field_checkbox.pdf")));
        parameters.addInput(new PdfMergeInput(customInput("pdf/forms/form_field_text.pdf")));
        parameters.setAcroFormPolicy(AcroFormPolicy.MERGE);

        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertHasAcroforms(true);

        testContext.forEachPdfOutput(doc -> {
            // original field
            assertEquals(1, findFieldsNamedExact("form_field", doc).size());
            // renamed field
            assertEquals(2, findFieldsMatching("form_field", doc).size());
        });
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
        testContext.forEachPdfOutput(doc -> assertPageTextContains(doc.getPage(0), "ጩ"));
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
        List<PdfMergeInput> inputs = new ArrayList<>();
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
    public void executeMergeFieldsWithSameNamesDifferentValues() throws IOException {
        MergeParameters parameters = new MergeParameters();
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.addInput(new PdfMergeInput(customInput("pdf/forms/test_form_one.pdf")));
        parameters.addInput(new PdfMergeInput(customInput("pdf/forms/test_form_two.pdf")));
        parameters.setAcroFormPolicy(AcroFormPolicy.MERGE);

        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertHasAcroforms(true);

        testContext.forEachPdfOutput(doc -> {
            assertEquals(1, findFieldsNamedExact("text_field", doc).size());
            assertEquals(2, findFieldsMatching("text_field", doc).size());

            // 1 widget each
            assertEquals(Arrays.asList(1, 1),
                    findFieldsMatching("text_field", doc).stream().map(f -> f.getWidgets().size())
                            .collect(Collectors.toList()));
        });
    }

    @Test
    public void executeMergeFieldsWithSameNamesSameValues() throws IOException {
        MergeParameters parameters = new MergeParameters();
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.addInput(new PdfMergeInput(customInput("pdf/forms/test_form_one.pdf")));
        parameters.addInput(new PdfMergeInput(customInput("pdf/forms/test_form_one.pdf")));
        parameters.setAcroFormPolicy(AcroFormPolicy.MERGE);

        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertHasAcroforms(true);

        testContext.forEachPdfOutput(doc -> {
            assertEquals(1, findFieldsNamedExact("text_field", doc).size());
            assertEquals(1, findFieldsMatching("text_field", doc).size());

            // 2 widgets, 1 field
            assertEquals(List.of(2), findFieldsMatching("text_field", doc).stream().map(f -> f.getWidgets().size())
                    .collect(Collectors.toList()));
        });
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
        execute(parameters);
        testContext.assertTaskFailed().assertFailedSource("name.pdf");
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
    @ResourceLock(Resources.SYSTEM_PROPERTIES)
    public void executeMergeMissingPageEagerAssertions() throws IOException {
        try {
            System.setProperty(Sejda.PERFORM_EAGER_ASSERTIONS_PROPERTY_NAME, "true");

            MergeParameters parameters = new MergeParameters();
            parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
            parameters.addInput(new PdfMergeInput(customInput("pdf/missing_page_ref.pdf", "name.pdf")));
            testContext.pdfOutputTo(parameters);
            assertThrows(PageNotFoundException.class, () -> execute(parameters));
            testContext.assertTaskFailed().assertFailedSource("name.pdf");
        } finally {
            System.setProperty(Sejda.PERFORM_EAGER_ASSERTIONS_PROPERTY_NAME, "");
        }
    }

    @Test
    public void normalizePageSizes_FirstPagePortrait() throws IOException {
        MergeParameters parameters = new MergeParameters();
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.addInput(new PdfMergeInput(customInput("pdf/A4Portrait.pdf")));
        parameters.addInput(new PdfMergeInput(customInput("pdf/A3Landscape.pdf")));
        parameters.addInput(new PdfMergeInput(customInput("pdf/A3Portrait.pdf")));
        parameters.addInput(new PdfMergeInput(customInput("pdf/landscape_by_rotation.pdf")));
        parameters.addInput(new PdfMergeInput(customInput("pdf/potrait_by_rotation.pdf")));
        parameters.setNormalizePageSizes(true);

        testContext.pdfOutputTo(parameters);
        execute(parameters);

        testContext.assertTaskCompleted();
        testContext.assertPages(5).forEachPdfOutput(d -> {
            assertEquals(595, widthOfCropBox(d.getPage(0)), 1);

            // landscape should be handled in a special case
            assertEquals(595, heightOfCropBox(d.getPage(1)), 1);
            assertEquals(840, widthOfCropBox(d.getPage(1)), 1);

            assertEquals(595, widthOfCropBox(d.getPage(2)), 1);

            assertEquals(840, widthOfCropBox(d.getPage(3)), 1);
            assertEquals(595, widthOfCropBox(d.getPage(4)), 1);
        });
    }

    @Test
    public void normalizePageSizes_FirstPageLandscape() throws IOException {
        MergeParameters parameters = new MergeParameters();
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.addInput(new PdfMergeInput(customInput("pdf/A3Landscape.pdf")));
        parameters.addInput(new PdfMergeInput(customInput("pdf/A4Portrait.pdf")));
        parameters.addInput(new PdfMergeInput(customInput("pdf/A3Portrait.pdf")));
        parameters.addInput(new PdfMergeInput(customInput("pdf/landscape_by_rotation.pdf")));
        parameters.addInput(new PdfMergeInput(customInput("pdf/potrait_by_rotation.pdf")));
        parameters.setNormalizePageSizes(true);

        testContext.pdfOutputTo(parameters);
        execute(parameters);

        testContext.assertTaskCompleted();
        testContext.assertPages(5).forEachPdfOutput(d -> {
            assertEquals(1190, widthOfCropBox(d.getPage(0)), 1);

            // landscape should be handled in a special case
            assertEquals(1190, heightOfCropBox(d.getPage(1)), 1);
            assertEquals(841, widthOfCropBox(d.getPage(1)), 1);

            assertEquals(841, widthOfCropBox(d.getPage(2)), 1);

            assertEquals(1190, widthOfCropBox(d.getPage(3)), 1);
            assertEquals(841, widthOfCropBox(d.getPage(4)), 1);
        });
    }

    @Test
    public void normalizePageSizes_AllLandscape() throws IOException {
        MergeParameters parameters = new MergeParameters();
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.addInput(new PdfMergeInput(customInput("pdf/A3Landscape.pdf")));
        parameters.addInput(new PdfMergeInput(customInput("pdf/landscape_by_rotation.pdf")));
        parameters.setNormalizePageSizes(true);

        testContext.pdfOutputTo(parameters);
        execute(parameters);

        testContext.assertTaskCompleted();
        testContext.assertPages(2).forEachPdfOutput(d -> {
            assertEquals(1190, widthOfCropBox(d.getPage(0)), 1);
            assertEquals(1190, widthOfCropBox(d.getPage(1)), 1);
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
    public void pageFooterAndTocwithDocTitles() throws IOException {
        List<PdfMergeInput> input = new ArrayList<>();
        input.add(new PdfMergeInput(regularInput()));
        input.add(new PdfMergeInput(customInput("pdf/test_file.pdf", "my_file.pdf")));
        MergeParameters parameters = setUpParameters(input);
        parameters.setTableOfContentsPolicy(ToCPolicy.DOC_TITLES);
        parameters.setFilenameFooter(true);
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertPages(16).forEachPdfOutput(d -> {
            assertFooterHasText(d.getPage(1), "test-file 2");
            assertFooterHasText(d.getPage(12), "my_file 13");
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
        testContext.assertPages(1 + 11 + 3 + 11 + 1)
                .forEachPdfOutput(d -> assertEquals(Arrays.asList(1, 13, 14, 15, 27), getPagesContainingImages(d)));
    }

    @Test
    public void mergeImagesAndPdfsWithSpecificPageSize() throws IOException {
        MergeParameters parameters = new MergeParameters();
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);

        ImageMergeInput image = new ImageMergeInput(customNonPdfInput("image/draft.png"));
        image.setShouldPageSizeMatchImageSize(true);

        parameters.addInput(image);
        parameters.addInput(new PdfMergeInput(customInput("pdf/test-pdf.pdf")));

        testContext.pdfOutputTo(parameters);
        execute(parameters);

        testContext.assertTaskCompleted();
        testContext.forEachPdfOutput(d -> assertEquals(d.getPage(0).getMediaBox().getWidth(), 248, 0.0));
    }

    @Test
    public void mergeImagesWithTocAndFooter() throws IOException {
        MergeParameters parameters = new MergeParameters();
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.addInput(new ImageMergeInput(customNonPdfInput("image/draft.png", "draft.png")));
        parameters.addInput(new ImageMergeInput(customNonPdfInput("image/large.jpg", "large.jpg")));
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

    @Test
    public void mergeImagesWithWrongFileExtension() throws IOException {
        MergeParameters parameters = new MergeParameters();
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.addInput(new ImageMergeInput(customNonPdfInput("image/large.jpg", "large.png")));
        parameters.addInput(new ImageMergeInput(customNonPdfInput("image/draft.png", "draft.jpeg")));

        testContext.pdfOutputTo(parameters);
        execute(parameters);

        testContext.assertTaskCompleted();
        testContext.assertPages(2);
    }

    @Test
    public void mergeWithUnreadableImageThows() throws IOException {
        MergeParameters parameters = new MergeParameters();
        parameters.setLenient(false);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.addInput(new ImageMergeInput(customNonPdfInput("image/draft.png", "draft.png")));
        parameters.addInput(new ImageMergeInput(customNonPdfInput("image/corrupt.png", "corrupt.png")));

        testContext.pdfOutputTo(parameters);
        execute(parameters);

        // TODO: friendlier error message
        testContext.assertTaskFailed("An error occurred creating PDImageXObject from file source: corrupt.png")
                .assertFailedSource("corrupt.png");
    }

    @Test
    public void mergeImagesWithValidation() throws IOException {
        MergeParameters parameters = new MergeParameters();
        parameters.setLenient(false);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        ImageMergeInput input1 = new ImageMergeInput(customNonPdfInput("image/draft.png", "draft.png"));
        input1.setPageSize(null);
        parameters.addInput(input1);

        testContext.pdfOutputTo(parameters);
        executeWithValidation(parameters);
        testContext.assertTaskFailed(InvalidTaskParametersException.class);
    }

    @Test
    public void mergeWithUnreadableImageWarning() throws IOException {
        MergeParameters parameters = new MergeParameters();
        parameters.setLenient(true);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.addInput(new ImageMergeInput(customNonPdfInput("image/draft.png", "draft.png")));
        parameters.addInput(new ImageMergeInput(customNonPdfInput("image/corrupt.png", "corrupt.png")));

        testContext.pdfOutputTo(parameters);
        execute(parameters);

        testContext.assertTaskCompleted();
        testContext.assertPages(1);
        testContext.assertTaskWarning("Image corrupt.png was skipped, could not be processed");
    }

    @Test
    public void mergeKeepingPageLabels() throws IOException {
        MergeParameters parameters = new MergeParameters();
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        PDDocument doc1 = new DocBuilder().withPages(2).withPageLabelRange(0, "r", "Intro:").get();
        PDDocument doc2 = new DocBuilder().withPages(3).withPageLabelRange(0, "D").get();
        parameters.addInput(new PdfMergeInput(customInput(doc1, "doc1.pdf")));
        parameters.addInput(new PdfMergeInput(customInput(doc2, "doc2.pdf")));
        parameters.setCatalogPageLabelsPolicy(CatalogPageLabelsPolicy.RETAIN);
        testContext.pdfOutputTo(parameters);
        execute(parameters);

        testContext.assertTaskCompleted();
        testContext.assertPages(5).forEachPdfOutput(d -> {
            try {
                PDPageLabels mergedLabels = d.getDocumentCatalog().getPageLabels();
                assertPageLabelIndexesAre(mergedLabels, 0, 2);
                assertPageLabelRangeIs(mergedLabels, 0, new PDPageLabelRange("r", "Intro:", null));
                assertPageLabelRangeIs(mergedLabels, 2, new PDPageLabelRange("D", null, null));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void mergeKeepingPageLabelsButDiscardingDecimalsStart() throws IOException {
        MergeParameters parameters = new MergeParameters();
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        PDDocument doc1 = new DocBuilder().withPages(2).withPageLabelRange(0, "D", null, 2).get();
        PDDocument doc2 = new DocBuilder().withPages(3).withPageLabelRange(0, "D", null, 7).get();

        parameters.addInput(new PdfMergeInput(customInput(doc1, "doc1.pdf")));
        parameters.addInput(new PdfMergeInput(customInput(doc2, "doc2.pdf")));
        parameters.setCatalogPageLabelsPolicy(CatalogPageLabelsPolicy.RETAIN);
        testContext.pdfOutputTo(parameters);
        execute(parameters);

        testContext.assertTaskCompleted();
        testContext.assertPages(5).forEachPdfOutput(d -> {
            try {
                PDPageLabels mergedLabels = d.getDocumentCatalog().getPageLabels();
                assertPageLabelIndexesAre(mergedLabels, 0, 2);
                assertPageLabelRangeIs(mergedLabels, 0, new PDPageLabelRange("D", null, null));
                assertPageLabelRangeIs(mergedLabels, 2, new PDPageLabelRange("D", null, null));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void mergeDiscardingPageLabels() throws IOException {
        MergeParameters parameters = new MergeParameters();
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);

        PDDocument doc1 = new DocBuilder().withPages(2).withPageLabelRange(0, "r", "Intro:").get();
        PDDocument doc2 = new DocBuilder().withPages(3).withPageLabelRange(0, "D").get();

        parameters.addInput(new PdfMergeInput(customInput(doc1, "doc1.pdf")));
        parameters.addInput(new PdfMergeInput(customInput(doc2, "doc2.pdf")));

        parameters.setCatalogPageLabelsPolicy(CatalogPageLabelsPolicy.DISCARD);

        testContext.pdfOutputTo(parameters);
        execute(parameters);

        testContext.assertTaskCompleted();
        testContext.assertPages(5).forEachPdfOutput(d -> {
            try {
                assertNull(d.getDocumentCatalog().getPageLabels());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void withCoverPage() throws IOException {
        List<PdfMergeInput> inputs = new ArrayList<>();
        inputs.add(new PdfMergeInput(shortInput())); // 4 pages, cover/title doc
        inputs.add(new PdfMergeInput(regularInput())); // 11 pages
        inputs.add(new PdfMergeInput(
                customInput("pdf/attachments_as_annots.pdf", "attachments_as_annots.pdf"))); // 3 pages

        MergeParameters parameters = setUpParameters(inputs);
        parameters.setTableOfContentsPolicy(ToCPolicy.FILE_NAMES);
        parameters.setFilenameFooter(true);
        parameters.setFirstInputCoverTitle(true);

        testContext.pdfOutputTo(parameters);

        execute(parameters);

        testContext.assertTaskCompleted();
        testContext.assertPages(19).forEachPdfOutput(d -> {
            // first 4 pages are the cover/title doc - short input
            assertFooterHasText(d.getPage(0), "short-test-file 1");
            assertFooterHasText(d.getPage(3), "short-test-file 4");

            // the TOC
            assertFooterHasText(d.getPage(4), "");
            assertPageTextExactLines(d.getPage(4), "test-file   6\nattachments_as_annots   17\n");

            // next 11 pages are the regular input
            assertFooterHasText(d.getPage(5), "test-file 6");
            assertFooterHasText(d.getPage(15), "test-file 16");

            // next 3 pages are attachments_as_annots doc
            assertFooterHasText(d.getPage(16), "attachments_as_annots 17");
            assertFooterHasText(d.getPage(18), "attachments_as_annots 19");
        });
    }

    @Test
    public void withoutRotations() throws IOException {
        List<MergeInput> inputs = new ArrayList<>();
        inputs.add(new PdfMergeInput(shortInput())); // 4 pages, cover/title doc
        inputs.add(new ImageMergeInput(customNonPdfInput("image/draft.png", "draft.png")));

        MergeParameters parameters = setUpParameters(inputs);

        testContext.pdfOutputTo(parameters);

        execute(parameters);

        testContext.assertTaskCompleted();
        testContext.assertPages(5).forEachPdfOutput(d -> {
            assertEquals(d.getPage(0).getRotation(), 0);
            assertEquals(d.getPage(1).getRotation(), 0);
            assertEquals(d.getPage(2).getRotation(), 0);
            assertEquals(d.getPage(3).getRotation(), 0);

            assertEquals(d.getPage(4).getRotation(), 0);
        });
    }

    @Test
    public void withRotations() throws IOException {
        List<MergeInput> inputs = new ArrayList<>();
        inputs.add(new PdfMergeInput(shortInput())); // 4 pages, cover/title doc
        inputs.add(new ImageMergeInput(customNonPdfInput("image/draft.png", "draft.png")));

        MergeParameters parameters = setUpParameters(inputs);
        parameters.setRotations(Arrays.asList(Rotation.DEGREES_90, Rotation.DEGREES_180));

        testContext.pdfOutputTo(parameters);

        execute(parameters);

        testContext.assertTaskCompleted();
        testContext.assertPages(5).forEachPdfOutput(d -> {
            assertEquals(d.getPage(0).getRotation(), 90);
            assertEquals(d.getPage(1).getRotation(), 90);
            assertEquals(d.getPage(2).getRotation(), 90);
            assertEquals(d.getPage(3).getRotation(), 90);

            assertEquals(d.getPage(4).getRotation(), 180);
        });
    }

    @Test
    public void atRestEncryptionTest() throws IOException {
        MergeParameters parameters = new MergeParameters();
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.addInput(new ImageMergeInput(encryptedAtRest(customNonPdfInput("image/draft.png"))));
        parameters.addInput(new ImageMergeInput(encryptedAtRest(customNonPdfInput("image/draft.tiff"))));
        parameters.addInput(new PdfMergeInput(encryptedAtRest(customInput("pdf/test-pdf.pdf"))));

        testContext.pdfOutputTo(parameters);
        execute(parameters);

        testContext.assertTaskCompleted();
        testContext.assertNoTaskWarnings();
        testContext.assertPages(13);
    }

    @Test
    public void withLargeTocItemsThatWrapAndGenerateMultipleTocPages() throws IOException {
        List<String> entries = new ArrayList<>();
        for (int i = 1; i <= 26; i++) {
            if (i % 3 == 0) {
                entries.add("Attachment " + i + " - Sample file name - longer item that might wrap on the next line");
            } else {
                entries.add("Attachment " + i + " - Sample file name - shorter");
            }
        }

        List<PdfMergeInput> inputs = new ArrayList<>(entries.size());
        for (String entry : entries) {
            inputs.add(new PdfMergeInput(customInput("pdf/one_page.pdf", entry + ".pdf"))); // 1 page
        }

        MergeParameters parameters = setUpParameters(inputs);
        parameters.setTableOfContentsPolicy(ToCPolicy.FILE_NAMES);
        parameters.setFilenameFooter(true);

        testContext.pdfOutputTo(parameters);

        execute(parameters);

        testContext.assertTaskCompleted();
        testContext.assertPages(entries.size() + 2).forEachPdfOutput(d -> {
            // the TOC
            assertPageTextExactLines(d.getPage(0),
                    "Attachment 1 - Sample file name - shorter   3\nAttachment 2 - Sample file name - shorter   4\nAttachment 3 - Sample file name - longer item that might wrap on\nthe next line   5\nAttachment 4 - Sample file name - shorter   6\nAttachment 5 - Sample file name - shorter   7\nAttachment 6 - Sample file name - longer item that might wrap on\nthe next line   8\nAttachment 7 - Sample file name - shorter   9\nAttachment 8 - Sample file name - shorter   10\nAttachment 9 - Sample file name - longer item that might wrap on\nthe next line   11\nAttachment 10 - Sample file name - shorter   12\nAttachment 11 - Sample file name - shorter   13\nAttachment 12 - Sample file name - longer item that might wrap\non the next line   14\nAttachment 13 - Sample file name - shorter   15\nAttachment 14 - Sample file name - shorter   16\nAttachment 15 - Sample file name - longer item that might wrap\non the next line   17\nAttachment 16 - Sample file name - shorter   18\nAttachment 17 - Sample file name - shorter   19\nAttachment 18 - Sample file name - longer item that might wrap\non the next line   20\nAttachment 19 - Sample file name - shorter   21\nAttachment 20 - Sample file name - shorter   22\nAttachment 21 - Sample file name - longer item that might wrap\non the next line   23\nAttachment 22 - Sample file name - shorter   24\nAttachment 23 - Sample file name - shorter   25\n");
            assertPageTextExactLines(d.getPage(1),
                    "Attachment 24 - Sample file name - longer item that might wrap\non the next line   26\nAttachment 25 - Sample file name - shorter   27\nAttachment 26 - Sample file name - shorter   28\n");

            // next pages are the merged inputs
            assertFooterHasText(d.getPage(2), "Attachment 1 - Sample file name - shorter 3");
            assertFooterHasText(d.getPage(3), "Attachment 2 - Sample file name - shorter 4");
            assertFooterHasText(d.getPage(4),
                    "Attachment 3 - Sample file name - longer item that might wrap on the next line 5");
            assertFooterHasText(d.getPage(27), "Attachment 26 - Sample file name - shorter 28");
        });
    }

    private float widthOfCropBox(PDPage page) {
        return page.getCropBox().rotate(page.getRotation()).getWidth();
    }

    private float heightOfCropBox(PDPage page) {
        return page.getCropBox().getHeight();
    }

    private void assertFooterHasText(PDPage page, String expectedText) {
        try {
            assertThat(new PdfTextExtractorByArea().extractFooterText(page).trim(), is(expectedText));
        } catch (TaskIOException e) {
            fail(e.getMessage(), e);
        }
    }

    private boolean isBlankPage(PDPage page) throws TaskIOException {
        return StringUtils.isBlank(new PdfTextExtractorByArea().extractTextFromArea(page,
                new Rectangle(0, 0, (int) page.getTrimBox().getWidth(), (int) page.getTrimBox().getHeight())).trim());
    }

    private List<PDField> findFieldsMatching(String partialName, PDDocument doc) {
        return doc.getDocumentCatalog().getAcroForm().getFieldTree().stream()
                .filter(f -> f.getFullyQualifiedName().contains(partialName)).collect(Collectors.toList());
    }

    private List<PDField> findFieldsNamedExact(String name, PDDocument doc) {
        return doc.getDocumentCatalog().getAcroForm().getFieldTree().stream()
                .filter(f -> f.getFullyQualifiedName().equals(name)).collect(Collectors.toList());
    }
}
