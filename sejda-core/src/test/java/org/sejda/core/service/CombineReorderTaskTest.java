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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.parameter.CombineReorderParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.task.Task;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;

@Ignore
public abstract class CombineReorderTaskTest extends PdfOutEnabledTest implements TestableTask<CombineReorderParameters> {

    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private SejdaContext context = mock(DefaultSejdaContext.class);
    private CombineReorderParameters parameters;

    @Before
    public void setUp() {
        TestUtils.setProperty(victim, "context", context);
    }

    private void setUpParameters(List<PdfSource<?>> sources) {
        parameters = new CombineReorderParameters();
        parameters.setOverwrite(true);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.addSources(sources);
    }

    private List<PdfSource<?>> basicInputs() {
        List<PdfSource<?>> input = new ArrayList<PdfSource<?>>();
        input.add(PdfStreamSource.newInstanceNoPassword(getClass().getClassLoader()
                .getResourceAsStream("pdf/multipage-test-a.pdf"), "a.pdf"));
        input.add(PdfStreamSource.newInstanceNoPassword(getClass().getClassLoader()
                .getResourceAsStream("pdf/multipage-test-b.pdf"), "b.pdf"));
        return input;
    }

    @Test
    public void combineAndReorder() throws TaskException, IOException {
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
        execute();

        assertPageHasText(1, "1a");
        assertPageHasText(2, "2a");
        assertPageHasText(3, "3a");
        assertPageHasText(4, "1b");
        assertPageHasText(5, "2b");
        assertPageHasText(6, "3b");
        assertPageHasText(7, "4a");
        assertPageHasText(8, "4b");
        assertPageHasText(9, "10b");
        assertPageHasText(10, "11b");
    }

    void execute() throws TaskException, IOException {
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewFileOutput(parameters);
        victim.execute(parameters);
        PdfReader reader = null;
        try {
            reader = getReaderFromResultFile();
            assertCreator(reader);
            assertVersion(reader, PdfVersion.VERSION_1_6);
            //assertEquals(pages, reader.getNumberOfPages());
        } finally {
            nullSafeCloseReader(reader);
        }
    }

    void assertPageHasText(int page, String expected) throws IOException {
        String actual = new PdfTextExtractor(getReaderFromResultFile()).getTextFromPage(page);

        int[] num = new int[actual.length()];

        for (int i = 0; i < actual.length(); i++) {
            num[i] = actual.charAt(i);
        }
        System.out.println(Arrays.toString(num));
        assertEquals("Page " + page + " text doesn't match", expected, actual.replaceAll("[^A-Za-z0-9]", ""));
    }
}
