/*
 * Created on 15/set/2010
 *
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
import org.sejda.core.manipulation.model.parameter.DecryptParameters;
import org.sejda.core.manipulation.model.pdf.PdfVersion;
import org.sejda.core.manipulation.model.task.Task;

import com.lowagie.text.pdf.PdfReader;

/**
 * Abstract test unit for the decrypt task
 * 
 * @author Andrea Vacondio
 * 
 */
@Ignore
@SuppressWarnings("unchecked")
public abstract class DecryptTaskTest extends PdfOutEnabledTest implements TestableTask<DecryptParameters> {
    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private TaskExecutionContext context = mock(DefaultTaskExecutionContext.class);
    private DecryptParameters parameters = new DecryptParameters();

    @Before
    public void setUp() {
        setUpParameters();
        TestUtils.setProperty(victim, "context", context);
    }

    /**
     * Set up of the decrypt parameters
     * 
     */
    private void setUpParameters() {
        parameters.setCompress(true);
        parameters.setOutputPrefix("test_prefix_");
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/enc_test_test_file.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceWithPassword(stream, "enc_test_test_file.pdf", "test");
        parameters.addSource(source);
        parameters.setOverwrite(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
    }

    @Test
    public void testExecute() throws TaskException, IOException {
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewStreamOutput(parameters);
        victim.execute(parameters);
        PdfReader reader = getReaderFromResultStream("test_prefix_enc_test_test_file.pdf");
        assertCreator(reader);
        assertVersion(reader, PdfVersion.VERSION_1_6);
        reader.close();
    }

    /**
     * 
     * @return the parameters
     */
    protected DecryptParameters getParameters() {
        return parameters;
    }

}
