/*
 * Created on Aug 25, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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

import java.io.File;
import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.sejda.cli.command.TestableTask;
import org.sejda.cli.command.TestableTasks;
import org.sejda.model.parameter.base.SinglePdfSourceTaskParameters;

/**
 * For tasks that support single file as input, test various scenarios related to this trait
 * 
 * @author Eduard Weissmann
 * 
 */
public class SingleInputSourceFileTraitTest extends AbstractTaskTraitTest {

    @Parameters
    public static Collection<Object[]> getTestParameters() {
        return asParameterizedTestData(TestableTasks.getTasksWithSingleSouceFiles());
    }

    public SingleInputSourceFileTraitTest(TestableTask testableTask) {
        super(testableTask);
    }

    @Test
    public void testSourceFileNotFound() {
        defaultCommandLine().with("-f", "input-doesntexist.pdf")
                .assertConsoleOutputContains("File 'input-doesntexist.pdf' does not exist");
    }

    @Test
    public void testSourceFileNoPassword() {
        SinglePdfSourceTaskParameters result = defaultCommandLine().with("-f", "inputs/input.pdf").invokeSejdaConsole();

        assertHasFileSource(result, new File("inputs/input.pdf"), null);
    }

    @Test
    public void testSourceFileWithPassword() {
        SinglePdfSourceTaskParameters result = defaultCommandLine().with("-f", "inputs/input-protected.pdf:secret123")
                .invokeSejdaConsole();

        assertHasFileSource(result, new File("inputs/input-protected.pdf"), "secret123");
    }

    @Test
    public void testMultipleSourceFiles() {
        defaultCommandLine().with("-f", "inputs/input-protected.pdf inputs/second_input.pdf")
                .assertConsoleOutputContains("Only one input file expected, received 2");

    }
}
