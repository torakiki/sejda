/*
 * Created on Aug 25, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.cli;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.sejda.core.manipulation.model.parameter.MultiplePdfSourceParameters;

/**
 * For tasks that support multiple files as input, test various scenarios related to this trait
 * 
 * @author Eduard Weissmann
 * 
 */
public class MultipleInputSourceFilesTraitTest extends AbstractTaskTraitTest {

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { { TestableTask.DECRYPT }, { TestableTask.ENCRYPT },
                { TestableTask.ROTATE }, { TestableTask.SETVIEWERPREFERENCES }, { TestableTask.UNPACK },
                { TestableTask.EXTRACT_TEXT } });
    }

    public MultipleInputSourceFilesTraitTest(TestableTask testableTask) {
        super(testableTask);
    }

    @Test
    public void testSourceFileNotFound() {
        defaultCommandLine().with("-f", "input-doesntexist.pdf").assertConsoleOutputContains(
                "File 'input-doesntexist.pdf' does not exist");
    }

    @Test
    public void testSourceFileNoPassword() {
        MultiplePdfSourceParameters result = defaultCommandLine().with("-f", "inputs/input.pdf").invokeSejdaConsole();

        assertEquals(1, result.getSourceList().size());
        assertHasFileSource(result, new File("inputs/input.pdf"), null);
    }

    @Test
    public void testSourceFileWithPassword() {
        MultiplePdfSourceParameters result = defaultCommandLine().with("-f", "inputs/input-protected.pdf;secret123")
                .invokeSejdaConsole();

        assertEquals(1, result.getSourceList().size());
        assertHasFileSource(result, new File("inputs/input-protected.pdf"), "secret123");
    }

    @Test
    public void testMultipleSourceFiles() {
        MultiplePdfSourceParameters result = defaultCommandLine().with("-f",
                "inputs/input-protected.pdf;secret123 inputs/second_input.pdf").invokeSejdaConsole();

        assertEquals(2, result.getSourceList().size());
        assertHasFileSource(result, new File("inputs/input-protected.pdf"), "secret123");
        assertHasFileSource(result, new File("inputs/second_input.pdf"), null);
    }
}
