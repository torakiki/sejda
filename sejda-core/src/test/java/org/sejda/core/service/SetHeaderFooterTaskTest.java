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

import static org.sejda.model.pdf.TextStampPattern.dateNow;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.sejda.model.HorizontalAlign;
import org.sejda.model.VerticalAlign;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.SetHeaderFooterParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.StandardType1Font;
import org.sejda.model.pdf.numbering.BatesSequence;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.pdf.page.PredefinedSetOfPages;
import org.sejda.sambox.pdmodel.PDPage;

/**
 * @author Eduard Weissmann
 * 
 */
@Ignore
public abstract class SetHeaderFooterTaskTest extends BaseTaskTest<SetHeaderFooterParameters> {

    private SetHeaderFooterParameters parameters;

    private SetHeaderFooterParameters basicNoSources() throws IOException {
        SetHeaderFooterParameters parameters = new SetHeaderFooterParameters();
        parameters.setBatesSequence(new BatesSequence());
        parameters.addPageRange(new PageRange(1));
        parameters.setPattern("[DATE] [PAGE_OF_TOTAL] - Exhibit [FILE_NUMBER] - Case ACME Inc - [BATES_NUMBER]");

        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setFont(StandardType1Font.CURIER_BOLD_OBLIQUE);

        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setHorizontalAlign(HorizontalAlign.LEFT);
        parameters.setVerticalAlign(VerticalAlign.BOTTOM);
        parameters.setFontSize(7d);
        parameters.setOutputPrefix("test_file[FILENUMBER]");
        testContext.directoryOutputTo(parameters);
        return parameters;
    }

    private SetHeaderFooterParameters basicWithSources() throws IOException {
        SetHeaderFooterParameters parameters = basicNoSources();
        parameters.addSource(customInput("pdf/test_file.pdf"));
        parameters.addSource(customInput("pdf/test_file.pdf"));
        return parameters;
    }

    @Test
    public void testUnicodeCharacters() throws Exception {
        parameters = basicWithSources();
        parameters.setPattern("Does UTF-8 partially work? Mirëdita grüß Gott dobrý večer góðan dag Καλώς Ορίσατε");
        parameters.setVerticalAlign(VerticalAlign.BOTTOM);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertFooterHasText(d.getPage(0),
                    "Does UTF-8 partially work? Mirëdita grüß Gott dobrý večer góðan dag Καλώς Ορίσατε");
        });
    }

    @Test
    public void testThaiCharacters() throws Exception {
        parameters = basicWithSources();
        parameters.setPattern("นี่คือการทดสอบ");
        parameters.setVerticalAlign(VerticalAlign.BOTTOM);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertFooterHasText(d.getPage(0), "นี่คือการทดสอบ");

        });
    }

    @Test
    public void testLabelWithMixedFonts() throws Exception {
        parameters = basicWithSources();
        parameters.setPattern("Bam - นี่คือการทดสอบ - Some english text after");
        parameters.setVerticalAlign(VerticalAlign.BOTTOM);

        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertFooterHasText(d.getPage(0), "Bam - นี่คือการทดสอบ - Some english text after");
        });
    }

    @Test
    public void testUnsupportedUnicodeCharacters() throws Exception {
        parameters = basicWithSources();
        parameters.setPattern("Some unsupported unicode \uFE0F characters");
        parameters.setVerticalAlign(VerticalAlign.BOTTOM);
        testContext.expectTaskWillProduceWarnings();
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.forEachPdfOutput(d -> {
            assertFooterHasText(d.getPage(0),
                    "Some unsupported unicode characters");
        });
        testContext.assertTaskWarning("Unsupported characters (\\U+FE0F) were removed: 'Some unsupported ...'");
    }

    @Test
    public void testPageRange() throws Exception {
        parameters = basicWithSources();
        parameters.getPageRanges().clear();
        parameters.addPageRange(new PageRange(2));
        parameters.setPattern("Test footer");
        parameters.setVerticalAlign(VerticalAlign.BOTTOM);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertFooterHasText(d.getPage(0), "");
            assertFooterHasText(d.getPage(1), "Test footer");
            assertFooterHasText(d.getPage(2), "Test footer");
        });
    }

    @Test
    public void testEvenPages() throws Exception {
        parameters = new SetHeaderFooterParameters();
        parameters.setPredefinedSetOfPages(PredefinedSetOfPages.EVEN_PAGES);
        parameters.addSource(customInput("pdf/test_file.pdf"));
        parameters.setPattern("Page [PAGE_ARABIC]");
        parameters.setVerticalAlign(VerticalAlign.BOTTOM);

        parameters.setOutputPrefix("test_file[FILENUMBER]");
        testContext.directoryOutputTo(parameters);
        execute(parameters);

        testContext.assertTaskCompleted();
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertFooterHasText(d.getPage(0), "");
            assertFooterHasText(d.getPage(1), "Page 2");
            assertFooterHasText(d.getPage(2), "");
            assertFooterHasText(d.getPage(3), "Page 4");
        });
    }

    @Test
    public void testMultiplePageRanges() throws Exception {
        parameters = new SetHeaderFooterParameters();
        parameters.addSource(customInput("pdf/test_file.pdf"));
        parameters.addPageRange(new PageRange(1, 2));
        parameters.addPageRange(new PageRange(4, 5));
        parameters.setPattern("Page [PAGE_ARABIC]");
        parameters.setVerticalAlign(VerticalAlign.BOTTOM);

        parameters.setOutputPrefix("test_file[FILENUMBER]");
        testContext.directoryOutputTo(parameters);
        execute(parameters);

        testContext.assertTaskCompleted();
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertFooterHasText(d.getPage(0), "Page 1");
            assertFooterHasText(d.getPage(1), "Page 2");
            assertFooterHasText(d.getPage(2), "");
            assertFooterHasText(d.getPage(3), "Page 4");
        });
    }

    @Test
    public void testWithScaling() throws Exception {
        parameters = basicNoSources();
        parameters.setAddMargins(true);

        parameters.addSource(customInput("pdf/no_margins_test_file.pdf"));
        parameters.setPattern("Test footer");
        parameters.setFontSize(15);
        parameters.setVerticalAlign(VerticalAlign.TOP);

        execute(parameters);

        testContext.assertTaskCompleted();
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertHeaderHasText(d.getPage(0), "Test footer");
        });
    }

    @Test
    public void testLogicalPage() throws Exception {
        parameters = basicWithSources();
        parameters.setPattern("Page [PAGE_ARABIC]");
        parameters.setPageCountStartFrom(12);
        parameters.getPageRanges().clear();
        parameters.addPageRange(new PageRange(2));
        parameters.setVerticalAlign(VerticalAlign.BOTTOM);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertFooterHasText(d.getPage(0), "");
            assertFooterHasText(d.getPage(1), "Page 12");
            assertFooterHasText(d.getPage(2), "Page 13");
            assertFooterHasText(d.getPage(3), "Page 14");
        });
    }

    @Test
    public void testLogicalPageOddPagesOnly() throws Exception {
        parameters = basicWithSources();
        parameters.setPattern("Page [PAGE_ARABIC]");
        parameters.setPageCountStartFrom(6);
        parameters.setPredefinedSetOfPages(PredefinedSetOfPages.EVEN_PAGES);
        parameters.setVerticalAlign(VerticalAlign.BOTTOM);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertFooterHasText(d.getPage(0), "");
            assertFooterHasText(d.getPage(1), "Page 6");
            assertFooterHasText(d.getPage(2), "");
            assertFooterHasText(d.getPage(3), "Page 8");
        });
    }

    @Test
    public void testDocumentWithRotatedPagesHeader() throws Exception {
        parameters = basicNoSources();
        parameters.addSource(customInput("pdf/rotated_pages.pdf"));
        parameters.setPattern("[PAGE_ARABIC]");
        parameters.setVerticalAlign(VerticalAlign.TOP);
        parameters.setHorizontalAlign(HorizontalAlign.CENTER);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertHeaderHasText(d.getPage(0), "1");
            assertHeaderHasText(d.getPage(1), "2");
            assertHeaderHasText(d.getPage(2), "3");
            assertHeaderHasText(d.getPage(3), "4");
            assertHeaderHasText(d.getPage(4), "5");
        });
    }

    @Test
    public void testDocumentWithRotatedPagesFooter() throws Exception {
        parameters = basicNoSources();
        parameters.addSource(customInput("pdf/rotated_pages.pdf"));
        parameters.setPattern("[PAGE_ARABIC]");
        parameters.setVerticalAlign(VerticalAlign.BOTTOM);
        parameters.setHorizontalAlign(HorizontalAlign.LEFT);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertFooterHasText(d.getPage(0), "1");
            assertFooterHasText(d.getPage(1), "2");
            assertFooterHasText(d.getPage(2), "3");
            assertFooterHasText(d.getPage(3), "4");
            assertFooterHasText(d.getPage(4), "5");
        });
    }

    @Test
    public void testBatesAndFileSequence() throws Exception {
        parameters = basicWithSources();
        parameters.setVerticalAlign(VerticalAlign.BOTTOM);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertFooterHasText(d.getPage(1), dateNow() + " 2 of 4 - Exhibit 1 - Case ACME Inc - 000002");
            assertFooterHasText(d.getPage(2), dateNow() + " 3 of 4 - Exhibit 1 - Case ACME Inc - 000003");
        }).forPdfOutput("test_file2.pdf", d -> {
            assertFooterHasText(d.getPage(0), dateNow() + " 1 of 4 - Exhibit 2 - Case ACME Inc - 000005");
        });
    }

    @Test
    public void testWriteHeader() throws Exception {
        parameters = basicWithSources();
        parameters.setVerticalAlign(VerticalAlign.TOP);
        parameters.setPattern("Page [PAGE_ROMAN] [PAGE_ARABIC] [BASE_NAME]");

        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertHeaderHasText(d.getPage(2), "Page III 3 test_file1");
        });
    }

    @Test
    public void testEncryptedFile() throws Exception {
        parameters = basicNoSources();
        parameters.addSource(encryptedInput());

        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(1);
    }

    @Test
    public void testNoBatesSeq() throws Exception {
        parameters = basicWithSources();
        parameters.setBatesSequence(null);

        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(2);
    }

    @Test
    public void testConfigurableBatesSeq() throws Exception {
        parameters = basicWithSources();
        parameters.setPattern("[BATES_NUMBER]");
        parameters.setBatesSequence(new BatesSequence(1000, 5, 10));

        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertFooterHasText(d.getPage(0), "0000001000");
            assertFooterHasText(d.getPage(1), "0000001005");

        });
    }

    @Test
    public void testStrippingControlCharacters() throws Exception {
        parameters = basicWithSources();
        parameters.setPattern("\tWith \rControl \n\nChars\t\n\r");
        parameters.setFont(StandardType1Font.HELVETICA);

        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.forPdfOutput("test_file1.pdf", d -> {
            assertFooterHasText(d.getPage(0), "With Control Chars");
        });
    }

    @Test
    public void testFileCounterStartFrom() throws Exception {
        SetHeaderFooterParameters parameters = basicNoSources();
        parameters.addSource(customInput("pdf/test_file.pdf", "a.pdf"));
        parameters.addSource(customInput("pdf/test_file.pdf", "b.pdf"));
        parameters.setFileCountStartFrom(10);
        parameters.setPattern("Foo");
        parameters.setOutputPrefix("[FILENUMBER]-[BASENAME]");
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputContainsFilenames("10-a.pdf", "11-b.pdf");
    }

    protected abstract void assertFooterHasText(PDPage page, String expectedText);

    protected abstract void assertHeaderHasText(PDPage page, String expectedText);

}
