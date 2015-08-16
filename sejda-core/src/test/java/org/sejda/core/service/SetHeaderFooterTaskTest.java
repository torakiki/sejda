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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.sejda.model.pdf.TextStampPattern.dateNow;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.model.HorizontalAlign;
import org.sejda.model.VerticalAlign;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.parameter.SetHeaderFooterParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.StandardType1Font;
import org.sejda.model.pdf.numbering.BatesSequence;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.task.Task;

/**
 * @author Eduard Weissmann
 * 
 */
@Ignore
public abstract class SetHeaderFooterTaskTest extends PdfOutEnabledTest implements
        TestableTask<SetHeaderFooterParameters> {

    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private SejdaContext context = mock(DefaultSejdaContext.class);
    private SetHeaderFooterParameters parameters;

    @Before
    public void setUp() {
        TestUtils.setProperty(victim, "context", context);
    }

    private SetHeaderFooterParameters basicNoSources() {
        SetHeaderFooterParameters parameters = new SetHeaderFooterParameters();
        parameters.setBatesSequence(new BatesSequence());
        parameters.setPageRange(new PageRange(1));
        parameters.setPattern("[DATE] [PAGE_OF_TOTAL] - Exhibit [FILE_NUMBER] - Case ACME Inc - [BATES_NUMBER]");

        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setFont(StandardType1Font.CURIER_BOLD_OBLIQUE);

        parameters.setOverwrite(true);
        parameters.setHorizontalAlign(HorizontalAlign.LEFT);
        parameters.setVerticalAlign(VerticalAlign.BOTTOM);
        parameters.setFontSize(7d);

        return parameters;
    }

    private SetHeaderFooterParameters basicWithSources() {
        SetHeaderFooterParameters parameters = basicNoSources();

        InputStream stream1 = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf");
        PdfStreamSource source1 = PdfStreamSource.newInstanceNoPassword(stream1, "test_file1.pdf");

        InputStream stream2 = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf");
        PdfStreamSource source2 = PdfStreamSource.newInstanceNoPassword(stream2, "test_file2.pdf");

        parameters.addSource(source1);
        parameters.addSource(source2);

        return parameters;
    }

    @Test
    public void testPageRange() throws Exception {
        parameters = basicWithSources();
        parameters.setPageRange(new PageRange(2));
        parameters.setPattern("Test footer");
        parameters.setVerticalAlign(VerticalAlign.BOTTOM);
        doTestExecute();
        assertFooterHasText("test_file1.pdf", 1, "");
        assertFooterHasText("test_file1.pdf", 2, "Test footer");
        assertFooterHasText("test_file1.pdf", 3, "Test footer");
    }

    @Test
    public void testLogicalPage() throws Exception {
        parameters = basicWithSources();
        parameters.setPattern("Page [PAGE_ARABIC]");
        parameters.setPageCountStartFrom(5);
        parameters.setVerticalAlign(VerticalAlign.BOTTOM);
        doTestExecute();
        assertFooterHasText("test_file1.pdf", 1, "Page 5");
        assertFooterHasText("test_file1.pdf", 2, "Page 6");
        assertFooterHasText("test_file1.pdf", 3, "Page 7");
    }

    @Test
    public void testBatesAndFileSequence() throws Exception {
        parameters = basicWithSources();
        parameters.setVerticalAlign(VerticalAlign.BOTTOM);
        doTestExecute();

        assertFooterHasText("test_file1.pdf", 2, dateNow() + " 2 of 4 - Exhibit 1 - Case ACME Inc - 000002");
        assertFooterHasText("test_file1.pdf", 3, dateNow() + " 3 of 4 - Exhibit 1 - Case ACME Inc - 000003");

        assertFooterHasText("test_file2.pdf", 1, dateNow() + " 1 of 4 - Exhibit 2 - Case ACME Inc - 000005");
    }

    @Test
    public void testWriteHeader() throws Exception {
        parameters = basicWithSources();
        parameters.setVerticalAlign(VerticalAlign.TOP);
        parameters.setPattern("Page [PAGE_ROMAN] [PAGE_ARABIC]");

        doTestExecute();

        assertHeaderHasText("test_file1.pdf", 3, "Page III 3");
    }

    @Test
    public void testEncryptedFile() throws Exception {
        parameters = basicNoSources();

        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/enc_with_modify_perm.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceWithPassword(stream, "test_file.pdf", "test");

        parameters.addSource(source);

        doTestExecute();
        assertOutputContainsDocuments(1);
    }

    @Test
    public void testNoBatesSeq() throws Exception {
        parameters = basicWithSources();
        parameters.setBatesSequence(null);

        doTestExecute();
        assertOutputContainsDocuments(2);
    }

    @Test
    public void testConfigurableBatesSeq() throws Exception {
        parameters = basicWithSources();
        parameters.setPattern("[BATES_NUMBER]");
        parameters.setBatesSequence(new BatesSequence(1000, 5, 10));

        doTestExecute();

        assertFooterHasText("test_file1.pdf", 1, "0000001000");
        assertFooterHasText("test_file1.pdf", 2, "0000001005");
    }

    private void doTestExecute() throws TaskException {
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewStreamOutput(parameters);
        victim.execute(parameters);
    }

    protected abstract void assertFooterHasText(String filename, int page, String expectedText) throws Exception;

    protected abstract void assertHeaderHasText(String filename, int page, String expectedText) throws Exception;

    protected SetHeaderFooterParameters getParameters() {
        return parameters;
    }

}
