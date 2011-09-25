/*
 * Created on 25/set/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.core.support.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.OutputStream;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.sejda.core.exception.TaskIOException;
import org.sejda.core.manipulation.model.output.DirectoryOutput;
import org.sejda.core.manipulation.model.output.OutputType;
import org.sejda.core.manipulation.model.output.StreamOutput;
import org.sejda.core.manipulation.model.output.TaskOutput;

/**
 * @author Andrea Vacondio
 * 
 */
public class OutputWriterSupportTest {

    @Test(expected = TaskIOException.class)
    public void testWriteNonFile() throws TaskIOException {
        OutputWriterSupport victim = new OutputWriterSupport();
        TaskOutput output = mock(TaskOutput.class);
        when(output.getOutputType()).thenReturn(OutputType.FILE_OUTPUT);
        victim.writeToNonFileDestination(output, true);
    }

    @Test
    public void testWriteStream() throws TaskIOException {
        OutputWriterSupport victim = spy(new OutputWriterSupport());
        OutputStream stream = mock(OutputStream.class);
        StreamOutput output = StreamOutput.newInstance(stream);
        ArgumentCaptor<Destination> destination = ArgumentCaptor.forClass(Destination.class);
        victim.writeToNonFileDestination(output, true);
        verify(victim).write(destination.capture());
        assertFalse(destination.getValue().isOverwrite());
        assertEquals(output, destination.getValue().getOutputDestination());
    }

    @Test
    public void testWriteDirectory() throws TaskIOException {
        OutputWriterSupport victim = spy(new OutputWriterSupport());
        File file = mock(File.class);
        when(file.isDirectory()).thenReturn(Boolean.TRUE);
        when(file.exists()).thenReturn(Boolean.TRUE);
        DirectoryOutput output = DirectoryOutput.newInstance(file);
        ArgumentCaptor<Destination> destination = ArgumentCaptor.forClass(Destination.class);
        victim.writeToNonFileDestination(output, true);
        verify(victim).write(destination.capture());
        assertTrue(destination.getValue().isOverwrite());
        assertEquals(output, destination.getValue().getOutputDestination());
    }

}
