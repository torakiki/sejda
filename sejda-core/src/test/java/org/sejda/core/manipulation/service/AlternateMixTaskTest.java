/*
 * Created on 25/dic/2010
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sejda.core.TestUtils;
import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.model.input.PdfMixInput;
import org.sejda.core.manipulation.model.input.PdfStreamSource;
import org.sejda.core.manipulation.model.parameter.AlternateMixParameters;
import org.sejda.core.manipulation.model.pdf.PdfVersion;
import org.sejda.core.manipulation.model.task.Task;

import com.itextpdf.text.pdf.PdfReader;

/**
 * Abstract test unit for the alternate mix task
 * 
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class AlternateMixTaskTest extends PdfOutEnabledTest implements TestableTask<AlternateMixParameters> {

    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private TaskExecutionContext context = mock(DefaultTaskExecutionContext.class);
    private AlternateMixParameters parameters;

    @Before
    public void setUp() throws TaskException {
        setUpParameters();
        TestUtils.setProperty(victim, "context", context);
    }

    /**
     * Set up of the rotation parameters
     * 
     */
    private void setUpParameters() {
        InputStream firstStream = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf");
        PdfStreamSource firstSource = PdfStreamSource.newInstanceNoPassword(firstStream, "first_test_file.pdf");
        PdfMixInput firstInput = new PdfMixInput(firstSource);
        InputStream secondStream = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf");
        PdfStreamSource secondSource = PdfStreamSource.newInstanceNoPassword(secondStream, "first_test_file.pdf");
        PdfMixInput secondInput = new PdfMixInput(secondSource, true, 3);
        parameters = new AlternateMixParameters(firstInput, secondInput, "outName.pdf");
        parameters.setOverwrite(true);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
    }

    @Test
    public void testExecute() throws TaskException, IOException {
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewFileOutput(parameters);
        victim.execute(parameters);
        PdfReader reader = getReaderFromResultFile();
        assertCreator(reader);
        assertEquals(8, reader.getNumberOfPages());
        reader.close();
    }

    protected AlternateMixParameters getParameters() {
        return parameters;
    }
}
