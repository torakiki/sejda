/*
 * Created on 17/set/2010
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.DefaultTaskExecutionContext;
import org.sejda.core.manipulation.TaskExecutionContext;
import org.sejda.core.manipulation.model.input.PdfStreamSource;
import org.sejda.core.manipulation.model.parameter.EncryptParameters;
import org.sejda.core.manipulation.model.pdf.PdfAccessPermission;
import org.sejda.core.manipulation.model.pdf.PdfEncryption;
import org.sejda.core.manipulation.model.pdf.PdfVersion;
import org.sejda.core.manipulation.model.task.Task;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Test unit for the encrypt task
 * 
 * @author Andrea Vacondio
 * 
 */
@Ignore
@SuppressWarnings("unchecked")
public abstract class EncryptTaskTest extends PdfStreamOutEnabledTest implements TestableTask<EncryptParameters> {
    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private TaskExecutionContext context = mock(DefaultTaskExecutionContext.class);
    private EncryptParameters parameters = new EncryptParameters();

    @Before
    public void setUp() throws TaskException {
        setUpParameters();
        victim.setContext(context);
    }

    /**
     * Set up of the rotation parameters
     */
    private void setUpParameters() {
        parameters.setCompress(true);
        parameters.setOutputPrefix("test_prefix_");
        parameters.setVersion(PdfVersion.VERSION_1_6);
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf");
        PdfStreamSource source = new PdfStreamSource(stream, "test_file.pdf");
        source.setPassword("test");
        parameters.setEncryptionAlgorithm(PdfEncryption.AES_ENC_128);
        parameters.addSource(source);
        parameters.setOverwrite(true);
    }

    @Test
    public void testExecuteOwner() throws TaskException, IOException {
        parameters.setOwnerPassword("test");
        parameters.addPermission(PdfAccessPermission.COPY);
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewStreamOutput(parameters);
        victim.execute(parameters);
        PdfReader reader = getReaderFromResultStream("test_prefix_test_file.pdf", "test".getBytes());
        assertCreator(reader);
        assertTrue(reader.isEncrypted());
        assertTrue((reader.getPermissions() & PdfWriter.ALLOW_COPY) == PdfWriter.ALLOW_COPY);
        assertFalse((reader.getPermissions() & PdfWriter.ALLOW_ASSEMBLY) == PdfWriter.ALLOW_ASSEMBLY);
        reader.close();
    }
}
