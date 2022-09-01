/*
 * Created on 13/set/2011
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
package org.sejda.impl.sambox;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sejda.model.exception.TaskNonLenientExecutionException;
import org.sejda.model.optimization.OptimizationPolicy;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.ExtractPagesParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.pdf.page.PredefinedSetOfPages;
import org.sejda.model.task.Task;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.tests.TestUtils;
import org.sejda.tests.tasks.BaseTaskTest;

import java.io.IOException;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Andrea Vacondio
 */
public class ExtractPagesSamboxTaskTest extends BaseTaskTest<ExtractPagesParameters> {

    private ExtractPagesParameters parameters;

    private void setUpParametersOddPages() {
        parameters = new ExtractPagesParameters(PredefinedSetOfPages.ODD_PAGES);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.addSource(shortInput());
    }

    private void setUpParametersEvenPagesEncrypted() {
        parameters = new ExtractPagesParameters(PredefinedSetOfPages.ODD_PAGES);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.addSource(encryptedInput());
    }

    private void setUpParametersToOptimize() {
        parameters = new ExtractPagesParameters(PredefinedSetOfPages.ODD_PAGES);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setCompress(true);
        parameters.setOptimizationPolicy(OptimizationPolicy.AUTO);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.addSource(customInput("/pdf/shared_resource_dic_w_fonts.pdf"));
    }

    private void setUpParametersPageRangesPages() {
        PageRange firstRange = new PageRange(1, 1);
        PageRange secondRange = new PageRange(3);
        parameters = new ExtractPagesParameters();
        parameters.addPageRange(firstRange);
        parameters.addPageRange(secondRange);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.addSource(shortInput());
    }

    private void setUpParametersWithOutline() {
        parameters = new ExtractPagesParameters();
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setCompress(true);
        parameters.setOptimizationPolicy(OptimizationPolicy.AUTO);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.addPageRange(new PageRange(1, 3));
        parameters.addSource(largeOutlineInput());
    }

    private void setUpParametersPageRangesMediumFile() {
        parameters = new ExtractPagesParameters();
        parameters.addPageRange(new PageRange(2, 3));
        parameters.addPageRange(new PageRange(5, 7));
        parameters.addPageRange(new PageRange(12, 18));
        parameters.addPageRange(new PageRange(20, 26));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_7);
        parameters.addSource(mediumInput());
    }

    private void setUpParametersWrongPageRanges() {
        PageRange range = new PageRange(10);
        parameters = new ExtractPagesParameters();
        parameters.addPageRange(range);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.addSource(shortInput());
    }

    @Test
    public void extractWrongPageRages() throws IOException {
        setUpParametersWrongPageRanges();
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskFailed();
    }

    @Test
    public void extractOddPages() throws IOException {
        setUpParametersOddPages();
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertPages(2);
    }

    @Test
    public void extractEvenPagesFromEncrypted() throws IOException {
        setUpParametersEvenPagesEncrypted();
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertPages(2);
    }

    @Test
    public void extractRanges() throws IOException {
        setUpParametersPageRangesPages();
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertPages(3);
    }

    @Test
    public void extractRangesMedium() throws IOException {
        setUpParametersPageRangesMediumFile();
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_7).assertPages(19);
    }

    @Test
    public void extractRangesMediumOneFilePerRange() throws IOException {
        setUpParametersPageRangesMediumFile();
        testContext.directoryOutputTo(parameters);
        parameters.setSeparateFileForEachRange(true);
        parameters.setOutputPrefix("[CURRENTPAGE]-[FILENUMBER]_[BASENAME]");
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(4).assertPages("2-1_medium-test-file.pdf", 2)
                .assertPages("5-2_medium-test-file.pdf", 3).assertPages("12-3_medium-test-file.pdf", 7)
                .assertPages("20-4_medium-test-file.pdf", 7);
    }

    @Test
    public void extractPagesInvertedSelection() throws IOException {
        parameters = new ExtractPagesParameters();
        parameters.setInvertSelection(true);
        parameters.addPageRange(new PageRange(7, 9));
        parameters.addSource(customInput("/pdf/test-pdf.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);

        assertThat(parameters.getPages(11), hasItems(1, 2, 3, 4, 5, 6, 10, 11));

        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertPages(8);
    }

    @Test
    public void extractPagesInvertedSelectionOneFilePerRange() throws IOException {
        parameters = new ExtractPagesParameters();
        parameters.setInvertSelection(true);
        parameters.addPageRange(new PageRange(7, 9));
        parameters.addSource(customInputAsFileSource("/pdf/test-pdf.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setOutputPrefix("[FILENUMBER]_[BASENAME]");
        parameters.setSeparateFileForEachRange(true);
        assertThat(parameters.getPages(11), hasItems(1, 2, 3, 4, 5, 6, 10, 11));

        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(2).assertPages("1_test-pdf.pdf", 6).assertPages("2_test-pdf.pdf", 2);
    }

    @Test
    public void extractOptimized() throws IOException {
        setUpParametersToOptimize();
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertPages(1);
    }

    @Test
    public void extractWithOutline() throws IOException {
        setUpParametersWithOutline();
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertPages(3)
                .forEachPdfOutput(d -> assertTrue(nonNull(d.getDocumentCatalog().getDocumentOutline())));
    }

    @Test
    public void extractWithDiscardOutline() throws IOException {
        setUpParametersWithOutline();
        parameters.discardOutline(true);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertPages(3)
                .forEachPdfOutput(d -> assertTrue(isNull(d.getDocumentCatalog().getDocumentOutline())));
    }

    @Test
    public void extractMultipleFiles() throws IOException {
        parameters = new ExtractPagesParameters();
        parameters.addPageRange(new PageRange(1, 2));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.addSource(shortInput());
        parameters.addSource(mediumInput());

        testContext.directoryOutputTo(parameters);

        execute(parameters);

        testContext.assertOutputSize(2);
        testContext.forEachPdfOutput(d -> Assertions.assertEquals(d.getNumberOfPages(), 2));
    }

    @Test
    public void extractWithForms() throws IOException {
        parameters = new ExtractPagesParameters();
        parameters.addPageRange(new PageRange(1, 1));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.addSource(formInput());

        testContext.directoryOutputTo(parameters);

        execute(parameters);

        testContext.assertOutputSize(1);
        testContext.forEachPdfOutput(d -> assertNotNull(d.getDocumentCatalog().getAcroForm()));
    }

    @Test
    public void encryptionAtRestTest() throws IOException {
        parameters = new ExtractPagesParameters();
        parameters.addPageRange(new PageRange(1, 2));
        parameters.addSource(TestUtils.encryptedAtRest(customInput("/pdf/test-pdf.pdf")));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);

        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertPages(2);
    }

    @Test
    public void deletePagesBatchLenient() throws IOException {
        parameters = new ExtractPagesParameters();
        parameters.setInvertSelection(true);
        parameters.setLenient(true);
        parameters.addPageRange(new PageRange(1, 3));
        parameters.addSource(customInput("/pdf/test-pdf.pdf"));
        parameters.addSource(customInput("/pdf/one_page.pdf", "one_page.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);

        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(1).assertTaskWarning("Document had all pages removed: one_page.pdf");
    }

    @Test
    public void extractPagesBatchLenient() throws IOException {
        parameters = new ExtractPagesParameters();
        parameters.setLenient(true);
        parameters.addPageRange(new PageRange(3, 3));
        parameters.addSource(customInput("/pdf/test-pdf.pdf"));
        parameters.addSource(customInput("/pdf/one_page.pdf", "one_page.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);

        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(1)
                .assertTaskWarning("No page has been selected for extraction from: one_page.pdf");
    }

    @Test
    public void deletePagesBatchNonLenient() throws IOException {
        parameters = new ExtractPagesParameters();
        parameters.setInvertSelection(true);
        parameters.addPageRange(new PageRange(1, 3));
        parameters.addSource(customInput("/pdf/test-pdf.pdf"));
        parameters.addSource(customInput("/pdf/one_page.pdf", "one_page.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);

        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskFailed(TaskNonLenientExecutionException.class);
    }

    @Test
    public void extractPagesBatchLenientNonLenient() throws IOException {
        parameters = new ExtractPagesParameters();
        parameters.addPageRange(new PageRange(3, 3));
        parameters.addSource(customInput("/pdf/test-pdf.pdf"));
        parameters.addSource(customInput("/pdf/one_page.pdf", "one_page.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);

        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskFailed(TaskNonLenientExecutionException.class);
    }

    @Test
    public void deletePagesBatchLenientNoOutput() throws IOException {
        parameters = new ExtractPagesParameters();
        parameters.setInvertSelection(true);
        parameters.setLenient(true);
        parameters.addPageRange(new PageRange(1));
        parameters.addSource(customInput("/pdf/test-pdf.pdf"));
        parameters.addSource(customInput("/pdf/one_page.pdf", "one_page.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);

        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskFailed("The task didn't generate any output file");
    }

    @Test
    public void extractPagesBatchLenientNoOutput() throws IOException {
        parameters = new ExtractPagesParameters();
        parameters.setLenient(true);
        parameters.addPageRange(new PageRange(100));
        parameters.addSource(customInput("/pdf/test-pdf.pdf"));
        parameters.addSource(customInput("/pdf/one_page.pdf", "one_page.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);

        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskFailed("The task didn't generate any output file");
    }

    /**
     * This is to test that a document going through the optimization process doesn't end up duplicating fonts if they are the same object ref in the original document
     *
     * @throws IOException
     */
    @Test
    public void optimizationReuseFontsDictionaries() throws IOException {
        parameters = new ExtractPagesParameters();
        parameters.addPageRange(new PageRange(1, 2));
        parameters.addSource(customInput("/pdf/multiple_res_dic_sharing_same_font.pdf"));
        parameters.setOptimizationPolicy(OptimizationPolicy.YES);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);

        testContext.directoryOutputTo(parameters);
        execute(parameters);
        PDDocument document = testContext.assertTaskCompleted();
        PDPage page0 = document.getPage(0);
        COSDictionary page0Font = page0.getResources().getCOSObject()
                .getDictionaryObject(COSName.FONT, COSDictionary.class)
                .getDictionaryObject(COSName.getPDFName("F1"), COSDictionary.class);
        PDPage page1 = document.getPage(1);
        COSDictionary page1Font = page1.getResources().getCOSObject()
                .getDictionaryObject(COSName.FONT, COSDictionary.class)
                .getDictionaryObject(COSName.getPDFName("F1"), COSDictionary.class);
        Assertions.assertEquals(page0Font, page1Font);
    }

    @Test
    public void specificResultFilenames() throws IOException {
        parameters = new ExtractPagesParameters();
        parameters.addPageRange(new PageRange(2));
        parameters.addPageRange(new PageRange(4, 5));
        parameters.addPageRange(new PageRange(7, 9));
        parameters.setOutputPrefix("[FILENUMBER]_[BASENAME]");
        parameters.addSource(customInputAsFileSource("/pdf/test-pdf.pdf"));
        parameters.setSeparateFileForEachRange(true);
        testContext.directoryOutputTo(parameters);
        parameters.addSpecificResultFilename("one");
        parameters.addSpecificResultFilename("two");
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(3).assertOutputContainsFilenames("one.pdf", "two.pdf", "3_test-pdf.pdf");
    }

    @Override
    public Task<ExtractPagesParameters> getTask() {
        return new ExtractPagesTask();
    }

}
