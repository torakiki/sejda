/*
 * Created on Jul 1, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sejda.cli.command.StandardTestableTask;
import org.sejda.model.parameter.AlternateMixMultipleInputParameters;

/**
 * Tests for the AlternateMixTask command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class AlternateMixTaskTest extends AbstractTaskTest {

    public AlternateMixTaskTest() {
        super(StandardTestableTask.ALTERNATE_MIX);
    }

    @Override
    protected CommandLineTestBuilder defaultCommandLine() {
        return new CommandLineTestBuilder(getTaskName()).with("-f", "inputs/input.pdf inputs/second_input.pdf")
                .with("-o", "./outputs/fileOutput.pdf");
    }

    @Test
    public void testFlagOptions_on() {
        AlternateMixMultipleInputParameters parameters = defaultCommandLine().withFlag("--reverseFirst")
                .withFlag("--reverseSecond").invokeSejdaConsole();
        assertTrue(parameters.getInputList().get(0).isReverse());
        assertTrue(parameters.getInputList().get(1).isReverse());
    }

    @Test
    public void testFlagOptions_off() {
        AlternateMixMultipleInputParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertFalse(parameters.getInputList().get(0).isReverse());
        assertFalse(parameters.getInputList().get(1).isReverse());
    }

    @Test
    public void testDefaults() {
        AlternateMixMultipleInputParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertEquals(1, parameters.getInputList().get(0).getStep());
        assertEquals(1, parameters.getInputList().get(1).getStep());
    }

    @Test
    public void testNonDefaults() {
        AlternateMixMultipleInputParameters parameters = defaultCommandLine().with("--firstStep", "5")
                .with("--secondStep", "9").invokeSejdaConsole();
        assertEquals(5, parameters.getInputList().get(0).getStep());
        assertEquals(9, parameters.getInputList().get(1).getStep());
    }

    @Test
    public void testFileInputs_positive() {
        AlternateMixMultipleInputParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertEquals("input.pdf", parameters.getInputList().get(0).getSource().getName());
        assertEquals("second_input.pdf", parameters.getInputList().get(1).getSource().getName());
    }

    @Test
    public void testFileInputs_missing_second_file() {
        assertConsoleOutputContains(getTaskName() + " -f inputs/input.pdf -o ./outputs/fileOutput.pdf",
                "Please specify two files as input parameters");
    }
}
