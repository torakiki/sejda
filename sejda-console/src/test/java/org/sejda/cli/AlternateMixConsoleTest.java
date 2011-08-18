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

import org.junit.Ignore;
import org.junit.Test;
import org.sejda.core.manipulation.model.parameter.AlternateMixParameters;

/**
 * Tests for the AlternateMixTask command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class AlternateMixConsoleTest extends BaseTaskConsoleTest {

    @Override
    String getTaskName() {
        return "alternatemix";
    }

    @Override
    protected CommandLineTestBuilder getMandatoryCommandLineArgumentsWithDefaults() {
        return new CommandLineTestBuilder(getTaskName()).with("-f", "inputs/input.pdf inputs/second_input.pdf");
    }

    @Test
    public void testExecuteCommandHelp() {
        assertConsoleOutputContains("-h " + getTaskName(), "Usage: sejda-console alternatemix options");
    }

    @Test
    public void testFlagOptions_on() {
        AlternateMixParameters parameters = invokeConsoleAndReturnTaskParameters(getMandatoryCommandLineArgumentsWithDefaults()
                .with("--reverseFirst").with("--reverseSecond").toString());
        assertTrue(parameters.getFirstInput().isReverse());
        assertTrue(parameters.getSecondInput().isReverse());
    }

    @Test
    public void testFlagOptions_off() {
        AlternateMixParameters parameters = invokeConsoleAndReturnTaskParameters(getMandatoryCommandLineArgumentsWithDefaults()
                .toString());
        assertFalse(parameters.getFirstInput().isReverse());
        assertFalse(parameters.getSecondInput().isReverse());
    }

    @Test
    public void testDefaults() {
        AlternateMixParameters parameters = invokeConsoleAndReturnTaskParameters(getMandatoryCommandLineArgumentsWithDefaults()
                .toString());
        assertEquals(1, parameters.getFirstInput().getStep());
        assertEquals(1, parameters.getSecondInput().getStep());
        assertEquals("alternate_mix_output.pdf", parameters.getOutputName());
    }

    @Test
    public void testNonDefaults() {
        AlternateMixParameters parameters = invokeConsoleAndReturnTaskParameters(getMandatoryCommandLineArgumentsWithDefaults()
                .with("--stepFirst", "5").with("--stepSecond", "9").toString());
        assertEquals(5, parameters.getFirstInput().getStep());
        assertEquals(9, parameters.getSecondInput().getStep());
        assertEquals("alternate_mix_output.pdf", parameters.getOutputName());
    }

    @Test
    public void testFileInputs_positive() {
        AlternateMixParameters parameters = invokeConsoleAndReturnTaskParameters(getMandatoryCommandLineArgumentsWithDefaults()
                .toString());
        assertEquals("input.pdf", parameters.getFirstInput().getSource().getName());
        assertEquals("second_input.pdf", parameters.getSecondInput().getSource().getName());
        assertEquals("alternate_mix_output.pdf", parameters.getOutputName());
    }

    @Test
    public void testFileInputs_missing_second_file() {
        assertConsoleOutputContains(getTaskName() + " -f inputs/input.pdf -o ./outputs",
                "Please specify two files as input parameters, using -f file1 file2");
    }

    @Ignore
    @Override
    @Test
    public void testSourceFileNoPassword() {
        // Ignore base test: this task's parameters are not implementing PdfSourceListParameters
    }

    @Ignore
    @Override
    @Test
    public void testSourceFileWithPassword() {
        // Ignore base test: this task's parameters are not implementing PdfSourceListParameters
    }

    @Ignore
    @Override
    @Test
    public void testBaseParameters_AllOn() {
        // Ignore base test: this task's parameters are not implementing PdfSourceListParameters
    }

    @Ignore
    @Override
    @Test
    public void testBaseParameters_AllOff() {
        // Ignore base test: this task's parameters are not implementing PdfSourceListParameters
    }
}
