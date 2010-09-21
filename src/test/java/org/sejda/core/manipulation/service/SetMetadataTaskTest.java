/*
 * Created on 09/lug/2010
 * Copyright (C) 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.sejda.core.manipulation.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.DefaultTaskExecutionContext;
import org.sejda.core.manipulation.TaskExecutionContext;
import org.sejda.core.manipulation.model.input.PdfStreamSource;
import org.sejda.core.manipulation.model.parameter.SetMetadataParameters;
import org.sejda.core.manipulation.model.pdf.PdfMetadataKey;
import org.sejda.core.manipulation.model.pdf.PdfVersion;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.manipulation.model.task.itext.SetMetadataTask;

import com.itextpdf.text.pdf.PdfReader;

/**
 * Test unit for the set metadata task
 * 
 * @author Andrea Vacondio
 * 
 */
@SuppressWarnings("unchecked")
public class SetMetadataTaskTest extends PdfStreamOutEnabledTest {

    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private TaskExecutionContext context = mock(DefaultTaskExecutionContext.class);
    private SetMetadataParameters parameters = new SetMetadataParameters();
    private List<Task> tasks = new ArrayList<Task>();

    @Before
    public void setUp() throws TaskException {
        setUpParameters();
        tasks.add(new SetMetadataTask());
        victim.setContext(context);
    }

    /**
     * Set up of the set metadata parameters
     */
    private void setUpParameters() {
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.put(PdfMetadataKey.AUTHOR, "test_author");
        parameters.put(PdfMetadataKey.KEYWORDS, "test_keywords");
        parameters.put(PdfMetadataKey.SUBJECT, "test_subject");
        parameters.put(PdfMetadataKey.TITLE, "test_title");
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf");
        PdfStreamSource source = new PdfStreamSource(stream, "test_file.pdf");
        parameters.setSource(source);
        parameters.setOverwrite(true);
    }

    @Test
    public void testExecuteStream() throws TaskException, IOException {
        for (Task task : tasks) {
            when(context.getTask(parameters)).thenReturn(task);
            initializeNewStreamOutput(parameters);
            victim.execute(parameters);
            PdfReader reader = getReaderFromResultStream("test_file.pdf");
            assertCreator(reader);
            HashMap<String, String> meta = reader.getInfo();
            assertEquals("test_author", meta.get(PdfMetadataKey.AUTHOR.getKey()));
            assertEquals("test_keywords", meta.get(PdfMetadataKey.KEYWORDS.getKey()));
            assertEquals("test_subject", meta.get(PdfMetadataKey.SUBJECT.getKey()));
            assertEquals("test_title", meta.get(PdfMetadataKey.TITLE.getKey()));
            reader.close();
        }
    }

    @Test
    public void testExecuteFile() throws TaskException, IOException {
        for (Task task : tasks) {
            when(context.getTask(parameters)).thenReturn(task);
            initializeNewFileOutput(parameters);
            victim.execute(parameters);
            PdfReader reader = getReaderFromResultFile();
            assertCreator(reader);
            HashMap<String, String> meta = reader.getInfo();
            assertEquals("test_author", meta.get(PdfMetadataKey.AUTHOR.getKey()));
            assertEquals("test_keywords", meta.get(PdfMetadataKey.KEYWORDS.getKey()));
            assertEquals("test_subject", meta.get(PdfMetadataKey.SUBJECT.getKey()));
            assertEquals("test_title", meta.get(PdfMetadataKey.TITLE.getKey()));
            reader.close();
        }
    }
}
