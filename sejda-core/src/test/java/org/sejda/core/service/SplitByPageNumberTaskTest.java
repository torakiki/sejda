/*
 * Created on 03/ago/2011
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
import org.sejda.model.parameter.SplitByPagesParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.task.Task;

/**
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class SplitByPageNumberTaskTest extends PdfOutEnabledTest implements
        TestableTask<SplitByPagesParameters> {
    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private SejdaContext context = mock(DefaultSejdaContext.class);
    private SplitByPagesParameters parameters;

    @Before
    public void setUp() {
        TestUtils.setProperty(victim, "context", context);
        parameters = new SplitByPagesParameters();
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setOverwrite(true);
    }

    private PdfStreamSource getPdfSource() {
        return PdfStreamSource.newInstanceNoPassword(
                getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf"), "test_file.pdf");
    }

    private PdfStreamSource getEncPdfSource() {
        return PdfStreamSource.newInstanceWithPassword(
                getClass().getClassLoader().getResourceAsStream("pdf/enc_with_modify_perm.pdf"),
                "enc_with_modify_perm.pdf", "test");
    }

    @Test
    public void burst() throws TaskException, IOException {
        parameters.setSource(getPdfSource());
        doTestBurst();
    }

    @Test
    public void burstEnc() throws TaskException, IOException {
        parameters.setSource(getEncPdfSource());
        doTestBurst();
    }

    public void doTestBurst() throws TaskException, IOException {
        parameters.addPage(1);
        parameters.addPage(2);
        parameters.addPage(3);
        parameters.addPage(4);
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewStreamOutput(parameters);
        victim.execute(parameters);
        assertOutputContainsDocuments(4);
    }

    @Test
    public void even() throws TaskException, IOException {
        parameters.setSource(getPdfSource());
        doTestEven();
    }

    @Test
    public void evenEnc() throws TaskException, IOException {
        parameters.setSource(getEncPdfSource());
        doTestEven();
    }

    public void doTestEven() throws TaskException, IOException {
        parameters.addPage(2);
        parameters.addPage(4);
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewStreamOutput(parameters);
        victim.execute(parameters);
        assertOutputContainsDocuments(2);
    }

    @Test
    public void odd() throws TaskException, IOException {
        parameters.setSource(getPdfSource());
        doTestOdd();
    }

    @Test
    public void oddEnc() throws TaskException, IOException {
        parameters.setSource(getEncPdfSource());
        doTestOdd();
    }

    public void doTestOdd() throws TaskException, IOException {
        parameters.addPage(1);
        parameters.addPage(3);
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewStreamOutput(parameters);
        victim.execute(parameters);
        assertOutputContainsDocuments(3);
    }

    @Test
    public void splitHalf() throws TaskException, IOException {
        parameters.setSource(PdfStreamSource.newInstanceNoPassword(
                getClass().getClassLoader().getResourceAsStream("pdf/2_pages.pdf"), "2_pages.pdf"));
        parameters.addPage(1);
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewStreamOutput(parameters);
        victim.execute(parameters);
        assertOutputContainsDocuments(2);
    }
}
