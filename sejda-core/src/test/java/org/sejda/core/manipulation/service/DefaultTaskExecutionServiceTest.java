/*
 * Created on 28/mag/2010
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

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.sejda.core.TestListenerFactory;
import org.sejda.core.TestListenerFactory.TestListenerStart;
import org.sejda.core.TestUtils;
import org.sejda.core.exception.NotificationContextException;
import org.sejda.core.exception.TaskException;
import org.sejda.core.exception.TaskExecutionException;
import org.sejda.core.manipulation.TestTaskParameter;
import org.sejda.core.manipulation.model.output.FileOutput;
import org.sejda.core.manipulation.model.output.StreamOutput;
import org.sejda.core.manipulation.model.output.TaskOutput;
import org.sejda.core.manipulation.model.parameter.base.TaskParameters;
import org.sejda.core.manipulation.model.pdf.PdfVersion;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.notification.context.GlobalNotificationContext;

/**
 * Test unit for the {@link DefaultTaskExecutionService}
 * 
 * @author Andrea Vacondio
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class DefaultTaskExecutionServiceTest {

    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();
    private TestTaskParameter parameters = new TestTaskParameter();
    private TaskExecutionContext context = mock(DefaultTaskExecutionContext.class);
    private Task task = mock(Task.class);

    @Before
    public void setUp() throws TaskException {
        OutputStream stream = mock(OutputStream.class);
        parameters.setOutput(StreamOutput.newInstance(stream));
        when(context.getTask(Matchers.any(TaskParameters.class))).thenReturn(task);
    }

    @Test
    public void testExecute() throws NotificationContextException {
        TestListenerStart listener = TestListenerFactory.newStartListener();
        GlobalNotificationContext.getContext().addListener(listener);
        victim.execute(parameters);
        assertTrue(listener.isStarted());
    }

    @Test
    public void testInvalidParameters() throws TaskException {
        parameters.setVersion(PdfVersion.VERSION_1_4);
        parameters.setCompress(true);
        victim.execute(parameters);
        verify(task, never()).before(parameters);
    }

    @Test
    public void testNegativeBeforeExecution() throws TaskException {
        doThrow(new TaskExecutionException("Mock exception")).when(task).before(Matchers.any(TaskParameters.class));
        TaskOutput output = mock(TaskOutput.class);
        parameters.setOutput(output);
        TestUtils.setProperty(victim, "context", context);
        victim.execute(parameters);
        verify(task).before(parameters);
        verify(task).after();
        verify(task, never()).execute(parameters);
    }

    @Test
    public void testNegativeValidationExecution() throws TaskException {
        TestUtils.setProperty(victim, "context", context);
        File file = mock(File.class);
        when(file.isFile()).thenReturn(Boolean.TRUE);
        when(file.getName()).thenReturn("name.pdf");
        parameters.setOutput(FileOutput.newInstance(file));
        victim.execute(parameters);
        verify(task, never()).before(parameters);
        verify(task, never()).after();
        verify(task, never()).execute(parameters);
    }
}
