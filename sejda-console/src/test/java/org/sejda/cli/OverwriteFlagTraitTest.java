/*
 * Created on Aug 29, 2011
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sejda.model.parameter.base.TaskParameters;

/**
 * Test verifying that the --overwrite flag can be specified for each task
 * 
 * @author Eduard Weissmann
 * 
 */
public class OverwriteFlagTraitTest extends AcrossAllTasksTraitTest {

    public OverwriteFlagTraitTest(TestableTask testableTask) {
        super(testableTask);
    }

    @Test
    public void onValue() {
        TaskParameters result = defaultCommandLine().withFlag("--overwrite").invokeSejdaConsole();
        assertTrue(describeExpectations(), result.isOverwrite());
    }

    @Test
    public void offValue() {
        TaskParameters result = defaultCommandLine().invokeSejdaConsole();

        assertFalse(describeExpectations(), result.isOverwrite());
    }
}
