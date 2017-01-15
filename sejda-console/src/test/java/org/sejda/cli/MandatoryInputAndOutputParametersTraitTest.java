/*
 * Created on Aug 29, 2011
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

import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.sejda.cli.command.StandardTestableTask;
import org.sejda.cli.command.TestableTask;
import org.sejda.cli.command.TestableTasks;

/**
 * Test verifying the help request feature for commands
 * 
 * @author Eduard Weissmann
 * 
 */
public class MandatoryInputAndOutputParametersTraitTest extends AbstractTaskTraitTest {

    @Parameters
    public final static Collection<Object[]> testParameters() {
        return asParameterizedTestData(TestableTasks.allTasksExceptFor(StandardTestableTask.MERGE));
    }

    public MandatoryInputAndOutputParametersTraitTest(TestableTask testableTask) {
        super(testableTask);
    }

    @Test
    public void testMandatoryOptions() {
        assertConsoleOutputContains(getTaskName() + " --output /tmp", "Option is mandatory: --files -f");
        assertConsoleOutputContains(getTaskName() + " --files /tmp/test1.pdf", "Option is mandatory: --output");
    }
}
