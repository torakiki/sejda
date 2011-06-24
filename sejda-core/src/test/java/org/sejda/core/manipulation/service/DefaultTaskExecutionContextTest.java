/*
 * Created on 12/mag/2010
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sejda.core.exception.TaskException;
import org.sejda.core.exception.TaskNotFoundException;
import org.sejda.core.manipulation.ChildTestTaskParameter;
import org.sejda.core.manipulation.TestTaskParameter;
import org.sejda.core.manipulation.model.output.PdfOutput;
import org.sejda.core.manipulation.model.parameter.TaskParameters;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.manipulation.service.DefaultTaskExecutionContext;
import org.sejda.core.manipulation.service.TaskExecutionContext;

/**
 * @author Andrea Vacondio
 * 
 */
public class DefaultTaskExecutionContextTest {
    private TaskExecutionContext victim;

    @Before
    public void setUp() {
        victim = new DefaultTaskExecutionContext();
    }

    @Test
    public void testGetTaskPositive() throws TaskException {
        Task<? extends TaskParameters> task = victim.getTask(new TestTaskParameter());
        Assert.assertNotNull(task);
    }

    @Test
    public void testGetTaskPositiveNearest() throws TaskException {
        Task<? extends TaskParameters> task = victim.getTask(new ChildTestTaskParameter());
        Assert.assertNotNull(task);
    }
    
    @Test(expected = TaskNotFoundException.class)
    public void testGetTaskNegative() throws TaskException {
        Task<? extends TaskParameters> task = victim.getTask(new TaskParameters() {

            public PdfOutput getOutput() {
                // TODO Auto-generated method stub
                return null;
            }
        });
        Assert.assertNotNull(task);
    }
}
