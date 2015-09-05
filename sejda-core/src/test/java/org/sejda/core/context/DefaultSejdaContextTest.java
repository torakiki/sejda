/*
 * Created on 12/mag/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * This file is part of the Sejda source code
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.core.context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskNotFoundException;
import org.sejda.model.output.TaskOutput;
import org.sejda.model.parameter.base.TaskParameters;
import org.sejda.model.task.ChildTestTaskParameter;
import org.sejda.model.task.Task;
import org.sejda.model.task.TestTaskParameter;

/**
 * @author Andrea Vacondio
 * 
 */
public class DefaultSejdaContextTest {
    private SejdaContext victim;

    @Before
    public void setUp() {
        victim = new DefaultSejdaContext();
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

            @Override
            public TaskOutput getOutput() {
                return null;
            }

            @Override
            public boolean isOverwrite() {
                return false;
            }
        });
        Assert.assertNotNull(task);
    }
}
