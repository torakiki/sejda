/*
 * Created on 19/ott/2011
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

import static org.mockito.Mockito.mock;

import java.io.File;

import org.junit.Test;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.output.FileTaskOutput;

/**
 * @author Andrea Vacondio
 * 
 */
public class DefaultMultipleOutputWriterTest {

    private MultipleOutputWriter victim = OutputWriters.newMultipleOutputWriter(true);

    @Test(expected = TaskIOException.class)
    public void testWriteNonFile() throws TaskIOException {
        File outputFile = mock(File.class);
        new FileTaskOutput(outputFile).accept(victim);
    }
}
