/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com).
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
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.parameter.SplitDownTheMiddleParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.task.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Ignore
public abstract class SplitDownTheMiddleTaskTest extends PdfOutEnabledTest implements TestableTask<SplitDownTheMiddleParameters> {

    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private SejdaContext context = mock(DefaultSejdaContext.class);
    private SplitDownTheMiddleParameters parameters;

    @Before
    public void setUp() {
        TestUtils.setProperty(victim, "context", context);
    }

    private void setUpParameters(List<PdfSource<?>> sources) {
        parameters = new SplitDownTheMiddleParameters();
        parameters.setOverwrite(true);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.addSources(sources);
    }

    private List<PdfSource<?>> landscapeInput() {
        List<PdfSource<?>> input = new ArrayList<PdfSource<?>>();
        input.add(PdfStreamSource.newInstanceNoPassword(getClass().getClassLoader()
                .getResourceAsStream("pdf/split_in_two_landscape_sample.pdf"), "a.pdf"));
        return input;
    }

    private List<PdfSource<?>> portraitInput() {
        List<PdfSource<?>> input = new ArrayList<PdfSource<?>>();
        input.add(PdfStreamSource.newInstanceNoPassword(getClass().getClassLoader()
                .getResourceAsStream("pdf/split_in_two_portrait_sample.pdf"), "a.pdf"));
        return input;
    }

    @Test
    public void splitLandscapeMode() throws TaskException, IOException {
        setUpParameters(landscapeInput());
        execute();
        assertNumberOfPages(4);
    }

    @Test
    public void splitPortraitMode() throws TaskException, IOException {
        setUpParameters(portraitInput());
        execute();
        assertNumberOfPages(4);
    }

    void execute() throws TaskException, IOException {
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewStreamOutput(parameters);
        victim.execute(parameters);
        PdfReader reader = null;
        try {
            reader = getReaderFromResultStream();
            assertCreator(reader);
            assertVersion(reader, PdfVersion.VERSION_1_6);
        } finally {
            nullSafeCloseReader(reader);
        }
    }
}
