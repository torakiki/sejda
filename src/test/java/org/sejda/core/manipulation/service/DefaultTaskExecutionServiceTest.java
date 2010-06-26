/*
 * Created on 28/mag/2010
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

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.sejda.core.exception.TaskException;
import org.sejda.core.exception.TaskExecutionException;
import org.sejda.core.manipulation.DefaultTaskExecutionContext;
import org.sejda.core.manipulation.TaskExecutionContext;
import org.sejda.core.manipulation.TestTaskParameter;
import org.sejda.core.manipulation.model.output.AbstractPdfOutput;
import org.sejda.core.manipulation.model.parameter.TaskParameters;
import org.sejda.core.manipulation.model.task.Task;

/**
 * Test unit for the {@link DefaultTaskExecutionService}
 * 
 * @author Andrea Vacondio
 * 
 */
@SuppressWarnings("unchecked")
public class DefaultTaskExecutionServiceTest {

    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();
    private TestTaskParameter parameters = new TestTaskParameter();
    private TaskExecutionContext context = mock(DefaultTaskExecutionContext.class);
    private Task task = mock(Task.class);

    @Before
    public void setUp() throws TaskException {
        when(context.getTask(Matchers.any(TaskParameters.class))).thenReturn(task);
    }

    @Test
    public void testExecute() throws TaskException {
        victim.execute(parameters);
    }

    @Test
    public void testNegativeBeforeExecution() throws TaskException {
        doThrow(new TaskExecutionException("Mock exception")).when(task).before(Matchers.any(TaskParameters.class));
        AbstractPdfOutput output = mock(AbstractPdfOutput.class);
        parameters.setOutput(output);
        victim.setContext(context);
        victim.execute(parameters);
        verify(task).before(parameters);
        verify(task).after();
        verify(task, never()).execute(parameters);
    }
    
    @Test
    public void testNegativeValidationExecution() throws TaskException {
        victim.setContext(context);
        victim.execute(parameters);
        verify(task, never()).before(parameters);
        verify(task, never()).after();
        verify(task, never()).execute(parameters);
    }
}
