/*
 * Created on Oct 7, 2011
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.sejda.cli.command.TestableTask;
import org.sejda.cli.command.TestableTasks;

/**
 * Integration tests, running without mocks the example usage for each task
 * 
 * @author Eduard Weissmann
 * 
 */
public class ExampleUsageIntegrationTest extends AbstractTaskTraitTest {

    @Parameters
    public static Collection<Object[]> data() {
        return asParameterizedTestData(
                TestableTasks.allTasksExceptFor(TestableTasks.getTasksWith(TestableTasks::hasFolderOutput)));
    }

    public ExampleUsageIntegrationTest(TestableTask testableTask) {
        super(testableTask);
    }

    @Test
    public void executeExampleUsage() {
        String exampleUsage = testableTask.getCommand().getExampleUsage();
        System.out.println(testableTask.getCommand());

        // change pattern to match outline test file we have
        exampleUsage = StringUtils.replace(exampleUsage, "-l 2 -e \".+(Chapter)+.+\"", "-l 1 -e \".+(level)+.+\""); // to match outline test file we have

        assertThat("Task " + getTaskName() + " doesnt provide example usage", exampleUsage, is(notNullValue()));
        assertTaskCompletes(exampleUsage + " --overwrite");
    }
}
