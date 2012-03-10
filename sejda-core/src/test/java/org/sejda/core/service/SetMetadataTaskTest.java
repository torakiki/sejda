/*
 * Created on 09/lug/2010
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
package org.sejda.core.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.parameter.SetMetadataParameters;
import org.sejda.model.pdf.PdfMetadataKey;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.task.Task;

import com.lowagie.text.pdf.PdfReader;

/**
 * Test unit for the set metadata task
 * 
 * @author Andrea Vacondio
 * 
 */
@Ignore
@SuppressWarnings("unchecked")
public abstract class SetMetadataTaskTest extends PdfOutEnabledTest implements TestableTask<SetMetadataParameters> {

    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private SejdaContext context = mock(DefaultSejdaContext.class);
    private SetMetadataParameters parameters = new SetMetadataParameters();

    @Before
    public void setUp() {
        setUpParameters();
        TestUtils.setProperty(victim, "context", context);
    }

    /**
     * Set up of the set metadata parameters
     * 
     */
    private void setUpParameters() {
        parameters.setCompress(true);
        parameters.setOutputName("outName.pdf");
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.put(PdfMetadataKey.AUTHOR, "test_author");
        parameters.put(PdfMetadataKey.KEYWORDS, "test_keywords");
        parameters.put(PdfMetadataKey.SUBJECT, "test_subject");
        parameters.put(PdfMetadataKey.TITLE, "test_title");
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceNoPassword(stream, "test_file.pdf");
        parameters.setSource(source);
        parameters.setOverwrite(true);
    }

    @Test
    public void testExecuteStream() throws TaskException, IOException {
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewStreamSingleOutput(parameters);
        victim.execute(parameters);
        PdfReader reader = getReaderFromResultStream("outName.pdf");
        assertCreator(reader);
        assertVersion(reader, PdfVersion.VERSION_1_6);
        HashMap<String, String> meta = reader.getInfo();
        assertEquals("test_author", meta.get(PdfMetadataKey.AUTHOR.getKey()));
        assertEquals("test_keywords", meta.get(PdfMetadataKey.KEYWORDS.getKey()));
        assertEquals("test_subject", meta.get(PdfMetadataKey.SUBJECT.getKey()));
        assertEquals("test_title", meta.get(PdfMetadataKey.TITLE.getKey()));
        reader.close();
    }

    @Test
    public void testExecuteFile() throws TaskException, IOException {
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewFileOutput(parameters);
        victim.execute(parameters);
        PdfReader reader = getReaderFromResultFile();
        assertCreator(reader);
        assertVersion(reader, PdfVersion.VERSION_1_6);
        HashMap<String, String> meta = reader.getInfo();
        assertEquals("test_author", meta.get(PdfMetadataKey.AUTHOR.getKey()));
        assertEquals("test_keywords", meta.get(PdfMetadataKey.KEYWORDS.getKey()));
        assertEquals("test_subject", meta.get(PdfMetadataKey.SUBJECT.getKey()));
        assertEquals("test_title", meta.get(PdfMetadataKey.TITLE.getKey()));
        reader.close();
    }

    protected SetMetadataParameters getParameters() {
        return parameters;
    }

}
