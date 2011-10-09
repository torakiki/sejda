/*
 * Created on Oct 9, 2011
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

import static org.junit.Assert.assertTrue;

import org.junit.Test;

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
        assertTrue(testableTask.getExampleUsage().contains("-f")); // -f option is already specified
        assertConsoleOutputContains(testableTask.getExampleUsage() + " -f /tmp/file2.pdf",
                "Option '-f' is specified twice"); // specifying it again will override the first one, creating confusing outputs. user probably wants to specify a list of inputs,
                                                   // which is done in a different way
    }

    @Test
    public void testDuplicateArgumentValuesAreNotDetectedAsFalsePositives() {
        assertTaskCompletes("decrypt -f /tmp/file1.pdf /tmp/file1.pdf -o /tmp --overwrite");
    }
}
