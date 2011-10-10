/*
 * Created on Oct 7, 2011
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.sejda.cli.transformer.CliCommand;

/**
 * Integration tests, running without mocks the example usage for each task
 * 
 * @author Eduard Weissmann
 * 
 */
public class ExampleUsageIntegrationTest extends AbstractTaskTraitTest {

    public ExampleUsageIntegrationTest(TestableTask testableTask) {
        super(testableTask);
    }

    @Parameters
    public final static Collection<Object[]> testParameters() {
        return TestableTask.allTasks();
    }

    @Test
    public void executeExampleUsage() {
        String exampleUsage = testableTask.getExampleUsage();
        assertThat("Task " + getTaskName() + " doesnt provide example usage", exampleUsage, is(notNullValue()));

        assertTaskCompletes(exampleUsage + " --overwrite");
    }

    public static List<Object[]> allCliCommands() {
        List<Object[]> result = new ArrayList<Object[]>();
        for (CliCommand each : CliCommand.values()) {
            result.add(new Object[] { each });
        }

        return result;
    }
}
