/*
 * Created on 10 giu 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.model.task;

import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.sejda.model.exception.TaskCancelledException;
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.model.exception.TaskNonLenientExecutionException;

/**
 * @author Andrea Vacondio
 *
 */
public class TaskExecutionContextTest {

    @Test(expected = IllegalArgumentException.class)
    public void testTaskExecutionContext() {
        new TaskExecutionContext(null, true);
    }

    @Test(expected = TaskCancelledException.class)
    public void negativeAssertTaskNotCancelled() throws TaskCancelledException {
        TaskExecutionContext victim = new TaskExecutionContext(mock(Task.class), true);
        victim.cancelTask();
        victim.assertTaskNotCancelled();
    }

    @Test
    public void positiveAssertTaskNotCancelled() throws TaskCancelledException {
        TaskExecutionContext victim = new TaskExecutionContext(mock(Task.class), true);
        victim.assertTaskNotCancelled();
    }

    @Test
    public void lenient() throws TaskExecutionException {
        TaskExecutionContext victim = new TaskExecutionContext(mock(Task.class), true);
        victim.assertTaskIsLenient(new Exception("Test"));
    }

    @Test(expected = TaskNonLenientExecutionException.class)
    public void nonLenient() throws TaskExecutionException {
        TaskExecutionContext victim = new TaskExecutionContext(mock(Task.class), false);
        victim.assertTaskIsLenient(new Exception("Test"));
    }
}
