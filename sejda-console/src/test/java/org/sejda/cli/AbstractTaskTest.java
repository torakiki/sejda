/*
 * Created on Jul 9, 2011
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

import org.junit.Before;

/**
 * Base class for task test suites, creates default inputs and output files/folders.<br/>
 * Contains base tests for common options
 * 
 * @author Eduard Weissmann
 * 
 */
public abstract class AbstractTaskTest extends AbstractTestSuite {

    protected final TestableTask testableTask;

    public AbstractTaskTest(TestableTask testableTask) {
        this.testableTask = testableTask;
    }

    /**
     * @return the minimum command line arguments that covers the mandatory arguments with reasonable defaults
     */
    protected CommandLineTestBuilder defaultCommandLine() {
        return testableTask.getCommandLineDefaults();
    }

    protected String describeExpectations() {
        return "In the context of task " + testableTask.name();
    }

    protected String getTaskName() {
        return testableTask.getTaskName();
    }

    @Before
    public void setUp() {
        createTestFile("./inputs/input.pdf");
        createTestFile("./inputs/second_input.pdf");
        createTestFile("./inputs/input-protected.pdf");
        createTestFile("./inputs/input-unprotected.pdf");
        createTestFolder("./outputs");
        createTestFile("./outputs/fileOutput.pdf");
    }
}
