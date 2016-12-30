/*
 * Created on 19/ott/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.core.support.io;

import static org.mockito.Mockito.mock;

import java.io.File;

import org.junit.Test;
import org.sejda.model.exception.TaskOutputVisitException;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.output.FileTaskOutput;
import org.sejda.model.task.Task;
import org.sejda.model.task.TaskExecutionContext;

/**
 * @author Andrea Vacondio
 * 
 */
public class DefaultMultipleOutputWriterTest {

    @Test(expected = TaskOutputVisitException.class)
    public void testWriteNonFile() throws TaskOutputVisitException {
        TaskExecutionContext context = new TaskExecutionContext(mock(Task.class), true);
        MultipleOutputWriter victim = OutputWriters.newMultipleOutputWriter(ExistingOutputPolicy.OVERWRITE, context);
        File outputFile = mock(File.class);
        new FileTaskOutput(outputFile).accept(victim);
    }
}
