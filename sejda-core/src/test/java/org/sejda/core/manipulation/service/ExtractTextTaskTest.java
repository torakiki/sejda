/*
 * Created on 24/ago/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sejda.core.Sejda;
import org.sejda.core.TestUtils;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.model.input.PdfStreamSource;
import org.sejda.core.manipulation.model.output.StreamOutput;
import org.sejda.core.manipulation.model.parameter.ExtractTextParameters;
import org.sejda.core.manipulation.model.task.Task;

/**
 * Parent class for tests testing the ExtractText task.
 * 
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class ExtractTextTaskTest implements TestableTask<ExtractTextParameters> {

    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private SejdaContext context = mock(DefaultSejdaContext.class);
    private ExtractTextParameters parameters;
    private ByteArrayOutputStream out;

    @Before
    public void setUp() {
        out = new ByteArrayOutputStream();
        setUpParameters();
        TestUtils.setProperty(victim, "context", context);
    }

    /**
     * Set up of the unpack parameters
     * 
     */
    private void setUpParameters() {
        parameters = new ExtractTextParameters(StreamOutput.newInstance(out));
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/enc_test_test_file.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceNoPassword(stream, "enc_test_test_file.pdf");
        parameters.addSource(source);
        parameters.setOverwrite(true);
        parameters.setTextEncoding("UTF-8");
    }

    @Test
    public void testExecuteStream() throws TaskException, IOException {
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        victim.execute(parameters);
        ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray());
        ZipInputStream zip = new ZipInputStream(input);
        int counter = 0;
        ZipEntry entry = zip.getNextEntry();
        while (entry != null) {
            counter++;
            assertTrue(entry.getName().endsWith(Sejda.TXT_EXTENSION));
            entry = zip.getNextEntry();
        }
        assertEquals(1, counter);
    }
}
