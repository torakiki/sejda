/*
 * Created on Jul 1, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sejda.core.manipulation.model.parameter.AlternateMixParameters;

/**
 * Tests for the AlternateMixTask command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class AlternateMixTaskTest extends AbstractTaskTest {

    public AlternateMixTaskTest() {
        super(TestableTask.ALTERNATEMIX);
    }

    @Override
    protected CommandLineTestBuilder defaultCommandLine() {
        return new CommandLineTestBuilder(getTaskName()).with("-f", "inputs/input.pdf inputs/second_input.pdf").with(
                "-o", "./outputs/fileOutput.pdf");
    }

    @Test
    public void testFlagOptions_on() {
        AlternateMixParameters parameters = defaultCommandLine().with("--reverseFirst").with("--reverseSecond")
                .invokeSejdaConsole();
        assertTrue(parameters.getFirstInput().isReverse());
        assertTrue(parameters.getSecondInput().isReverse());
    }

    @Test
    public void testFlagOptions_off() {
        AlternateMixParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertFalse(parameters.getFirstInput().isReverse());
        assertFalse(parameters.getSecondInput().isReverse());
    }

    @Test
    public void testDefaults() {
        AlternateMixParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertEquals(1, parameters.getFirstInput().getStep());
        assertEquals(1, parameters.getSecondInput().getStep());
    }

    @Test
    public void testNonDefaults() {
        AlternateMixParameters parameters = defaultCommandLine().with("--stepFirst", "5").with("--stepSecond", "9")
                .invokeSejdaConsole();
        assertEquals(5, parameters.getFirstInput().getStep());
        assertEquals(9, parameters.getSecondInput().getStep());
    }

    @Test
    public void testFileInputs_positive() {
        AlternateMixParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertEquals("input.pdf", parameters.getFirstInput().getSource().getName());
        assertEquals("second_input.pdf", parameters.getSecondInput().getSource().getName());
    }

    @Test
    public void testFileInputs_missing_second_file() {
        assertConsoleOutputContains(getTaskName() + " -f inputs/input.pdf -o ./outputs/fileOutput.pdf",
                "Please specify two files as input parameters, using -f file1 file2");
    }
}
