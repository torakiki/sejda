/*
 * Created on Jul 9, 2011
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

import org.junit.Before;
import org.sejda.cli.command.TestableTask;

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
        return "In the context of task " + testableTask.getCommand().getDisplayName();
    }

    protected String getTaskName() {
        return testableTask.getCommand().getDisplayName();
    }

    @Before
    public void setUp() {
        createTestPdfFile("./inputs/input.pdf");
        createTestPdfFile("./inputs/second_input.pdf");
        createTestPdfFile("./inputs/input-protected.pdf");
        createTestPdfFile("./inputs/input-unprotected.pdf");
        createTestPdfFile("./inputs/back.pdf");
        createTestFolder("./outputs");
        createTestFile("./inputs/logo.png", getClass().getResourceAsStream("/image/draft.png"));

        createTestTextFile("./inputs/file1.txt", "this is a test file");
        createTestTextFile("/tmp/file1.txt", "this is a test file");
        createTestPdfFile("/tmp/file1.pdf");
        createTestPdfFile("/tmp/file2.pdf");
        createTestPdfFile("/tmp/back.pdf");
        createTestFile("/tmp/logo.png", getClass().getResourceAsStream("/image/draft.png"));
    }
}
