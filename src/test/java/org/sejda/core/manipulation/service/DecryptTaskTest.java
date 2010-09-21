/*
 * Created on 15/set/2010
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.DefaultTaskExecutionContext;
import org.sejda.core.manipulation.TaskExecutionContext;
import org.sejda.core.manipulation.model.input.PdfStreamSource;
import org.sejda.core.manipulation.model.parameter.DecryptParameters;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.manipulation.model.task.itext.DecryptTask;

import com.itextpdf.text.pdf.PdfReader;

/**
 * @author Andrea Vacondio
 * 
 */
@SuppressWarnings("unchecked")
public class DecryptTaskTest extends PdfStreamOutEnabledTest {
    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private TaskExecutionContext context = mock(DefaultTaskExecutionContext.class);
    private DecryptParameters parameters = new DecryptParameters();
    private List<Task> tasks = new ArrayList<Task>();

    @Before
    public void setUp() throws TaskException {
        setUpParameters();
        tasks.add(new DecryptTask());
        victim.setContext(context);
    }

    /**
     * Set up of the rotation parameters
     */
    private void setUpParameters() {
        parameters.setCompress(true);
        parameters.setOutputPrefix("test_prefix_");
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/enc_test_test_file.pdf");
        PdfStreamSource source = new PdfStreamSource(stream, "enc_test_test_file.pdf");
        source.setPassword("test");
        parameters.addSource(source);
        parameters.setOverwrite(true);
    }

    @Test
    public void testExecute() throws TaskException, IOException {
        for (Task task : tasks) {
            when(context.getTask(parameters)).thenReturn(task);
            initializeNewStreamOutput(parameters);
            victim.execute(parameters);
            PdfReader reader = getReaderFromResultStream("test_prefix_enc_test_test_file.pdf");
            assertCreator(reader);
            reader.close();
        }
    }
}
