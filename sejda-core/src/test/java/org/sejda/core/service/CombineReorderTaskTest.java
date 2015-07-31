/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sejda.core.service;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
