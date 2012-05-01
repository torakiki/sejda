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

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.spy;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.sejda.core.support.io.model.FileOutput;
import org.sejda.model.exception.TaskOutputVisitException;
import org.sejda.model.output.FileTaskOutput;

/**
 * @author Andrea Vacondio
 * 
 */
public class BaseOutputWriterTest {

    @Test
    public void testWriteFile() throws IOException, TaskOutputVisitException {
        BaseOutputWriter victim = spy(new BaseOutputWriter(true));
        File tempFile = File.createTempFile("srcTest", "");
        victim.add(FileOutput.file(tempFile).name("newName"));

        File outFile = File.createTempFile("outTemp", "");
        FileTaskOutput output = new FileTaskOutput(outFile);

        output.accept(victim);
        assertFalse("temporary file not deleted", tempFile.exists());
    }

}
