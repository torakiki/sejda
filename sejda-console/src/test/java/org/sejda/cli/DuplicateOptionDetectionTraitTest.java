/*
 * Created on Oct 9, 2011
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

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sejda.cli.command.TestableTask;

/**
 * Test for duplicate option detection
 * 
 * @author Eduard Weissmann
 * 
 */
public class DuplicateOptionDetectionTraitTest extends AcrossAllTasksTraitTest {

    public DuplicateOptionDetectionTraitTest(TestableTask testableTask) {
        super(testableTask);
    }

    @Test
    public void testDuplicateOptionsAreDetected() {
        assertTrue(testableTask.getCommand().getExampleUsage().contains("-f")); // -f option is already specified
        assertConsoleOutputContains(testableTask.getCommand().getExampleUsage() + " -f /tmp/file2.pdf",
                "Option '-f' is specified twice"); // specifying it again will override the first one, creating confusing outputs. user probably wants to specify a list of inputs,
                                                   // which is done in a different way
    }

    @Test
    public void testDuplicateArgumentValuesAreNotDetectedAsFalsePositives() {
        assertTaskCompletes("decrypt -f /tmp/file1.pdf /tmp/file1.pdf -o /tmp --existingOutput overwrite");
    }
}
