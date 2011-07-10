/*
 * Created on Jul 1, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sejda.core.manipulation.model.parameter.RotateParameters;
import org.sejda.core.manipulation.model.rotation.PageRotation;
import org.sejda.core.manipulation.model.rotation.Rotation;

/**
 * Tests for the DecryptTask command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class RotateConsoleTest extends BaseTaskConsoleTest {

    @Override
    String getTaskName() {
        return "rotate";
    }

    @Override
    protected CommandLineTestBuilder getMandatoryCommandLineArgumentsWithDefaults() {
        return new CommandLineTestBuilder(getTaskName()).with("-r", "1:DEGREES_90");
    }

    @Test
    public void testExecuteCommandHelp() {
        assertConsoleOutputContains("-h " + getTaskName(), "Usage: sejda-console rotate options");
    }

    @Test
    public void testOutputPrefix_Specified() {
        RotateParameters parameters = invokeConsoleAndReturnTaskParameters(getMandatoryCommandLineArgumentsWithDefaults()
                .with("-p", "fooPrefix").toString());
        assertEquals("fooPrefix", parameters.getOutputPrefix());
    }

    @Test
    public void testOutputPrefix_Default() {
        RotateParameters parameters = invokeConsoleAndReturnTaskParameters(getMandatoryCommandLineArgumentsWithDefaults()
                .toString());
        assertEquals("", parameters.getOutputPrefix());
    }

    @Test
    public void testPageRotation() {
        RotateParameters parameters = invokeConsoleAndReturnTaskParameters(getMandatoryCommandLineArgumentsWithDefaults()
                .with("-r", "34:DEGREES_90").toString());
        assertEquals(PageRotation.createSinglePageRotation(34, Rotation.DEGREES_90), parameters.getRotation());
    }
}
