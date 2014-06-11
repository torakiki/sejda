/*
 * Created on 11/giu/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.parameter.SplitByEveryXPagesParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.task.Task;

/**
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class SplitByEveryXPagesTaskTest extends PdfOutEnabledTest implements
        TestableTask<SplitByEveryXPagesParameters> {
    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private SejdaContext context = mock(DefaultSejdaContext.class);

    private SplitByEveryXPagesParameters parameters;

    @Before
    public void setUp() {
        TestUtils.setProperty(victim, "context", context);
    }

    private PdfStreamSource getPdfSource() {
        return PdfStreamSource.newInstanceNoPassword(
                getClass().getClassLoader().getResourceAsStream("pdf/medium_test.pdf"), "medium_test.pdf");
    }

    private PdfStreamSource getEncPdfSource() {
        return PdfStreamSource.newInstanceWithPassword(
                getClass().getClassLoader().getResourceAsStream("pdf/enc_with_modify_perm.pdf"),
                "enc_with_modify_perm.pdf", "test");
    }

    @Test
    public void split() throws TaskException, IOException {
        parameters = new SplitByEveryXPagesParameters(10);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setOverwrite(true);
        parameters.setSource(getPdfSource());
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewStreamOutput(parameters);
        victim.execute(parameters);
        assertOutputContainsDocuments(4);
    }

    @Test
    public void splitEnc() throws TaskException, IOException {
        parameters = new SplitByEveryXPagesParameters(2);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setOverwrite(true);
        parameters.setSource(getEncPdfSource());
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewStreamOutput(parameters);
        victim.execute(parameters);
        assertOutputContainsDocuments(2);
    }
}
