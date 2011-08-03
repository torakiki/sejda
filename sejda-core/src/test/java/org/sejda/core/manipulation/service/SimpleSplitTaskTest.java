/*
 * Created on 29/lug/2011
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
package org.sejda.core.manipulation.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sejda.core.TestUtils;
import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.model.input.PdfStreamSource;
import org.sejda.core.manipulation.model.parameter.SimpleSplitParameters;
import org.sejda.core.manipulation.model.parameter.SimpleSplitType;
import org.sejda.core.manipulation.model.pdf.PdfVersion;
import org.sejda.core.manipulation.model.task.Task;

/**
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class SimpleSplitTaskTest extends PdfOutEnabledTest implements TestableTask<SimpleSplitParameters> {

    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private TaskExecutionContext context = mock(DefaultTaskExecutionContext.class);
    private SimpleSplitParameters parameters;

    @Before
    public void setUp() {
        TestUtils.setProperty(victim, "context", context);
    }

    /**
     * Set up of the set page labels parameters
     * 
     */
    private void setUpParameters(SimpleSplitType type) {
        parameters = new SimpleSplitParameters(type);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceNoPassword(stream, "test_file.pdf");
        parameters.setSource(source);
        parameters.setOverwrite(true);
    }

    @Test
    public void testExecuteBurst() throws TaskException, IOException {
        setUpParameters(SimpleSplitType.BURST);
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewStreamOutput(parameters);
        victim.execute(parameters);
        assertOutputContainsDocuments(4);
    }

    @Test
    public void testExecuteEven() throws TaskException, IOException {
        setUpParameters(SimpleSplitType.EVEN_PAGES);
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewStreamOutput(parameters);
        victim.execute(parameters);
        assertOutputContainsDocuments(2);
    }

    @Test
    public void testExecuteOdd() throws TaskException, IOException {
        setUpParameters(SimpleSplitType.ODD_PAGES);
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewStreamOutput(parameters);
        victim.execute(parameters);
        assertOutputContainsDocuments(3);
    }

}
