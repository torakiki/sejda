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
package org.sejda.core.service;

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
import org.sejda.TestUtils;
import org.sejda.core.TestListenerFactory;
import org.sejda.core.TestListenerFactory.TestListenerStart;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.core.notification.context.GlobalNotificationContext;
import org.sejda.model.exception.NotificationContextException;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.model.output.FileTaskOutput;
import org.sejda.model.output.SingleTaskOutput;
import org.sejda.model.output.StreamTaskOutput;
import org.sejda.model.parameter.base.TaskParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.task.Task;
import org.sejda.model.task.TestTaskParameter;

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
    private SejdaContext context = mock(DefaultSejdaContext.class);
    private Task task = mock(Task.class);

    @Before
    public void setUp() throws TaskException {
        OutputStream stream = mock(OutputStream.class);
        parameters.setOutput(new StreamTaskOutput(stream));
        when(context.getTask(Matchers.any(TaskParameters.class))).thenReturn(task);
        when(context.isValidation()).thenReturn(Boolean.TRUE);
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
        SingleTaskOutput<?> output = mock(SingleTaskOutput.class);
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
        when(file.exists()).thenReturn(Boolean.TRUE);
        parameters.setOutput(new FileTaskOutput(file));
        when(file.isFile()).thenReturn(Boolean.FALSE);
        victim.execute(parameters);
        verify(task, never()).before(parameters);
        verify(task, never()).after();
        verify(task, never()).execute(parameters);
    }
}
