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
import org.sejda.model.parameter.RotateParameters;
import org.sejda.model.rotation.PageRotation;
import org.sejda.model.rotation.Rotation;
import org.sejda.model.rotation.RotationType;

/**
 * Tests for the RotateTask command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class RotateTaskTest extends AbstractTaskTest {

    public RotateTaskTest() {
        super(TestableTask.ROTATE);
    }

    @Test
    public void testOutputPrefix_Specified() {
        RotateParameters parameters = defaultCommandLine().with("-p", "fooPrefix").invokeSejdaConsole();
        assertEquals("fooPrefix", parameters.getOutputPrefix());
    }

    @Test
    public void testOutputPrefix_Default() {
        RotateParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertEquals("", parameters.getOutputPrefix());
    }

    @Test
    public void pageRotation_singlePage() {
        RotateParameters parameters = defaultCommandLine().with("-r", "34:90").invokeSejdaConsole();
        assertEquals(PageRotation.createSinglePageRotation(34, Rotation.DEGREES_90), parameters.getRotation());
    }

    @Test
    public void pageRotation_multiplePages() {
        RotateParameters parameters = defaultCommandLine().with("-r", "odd:0").invokeSejdaConsole();
        assertEquals(PageRotation.createMultiplePagesRotation(Rotation.DEGREES_0, RotationType.ODD_PAGES),
                parameters.getRotation());
    }

    @Test
    public void pageRotation_invalidRotationType() {
        defaultCommandLine().with("-r", "odd:99990").assertConsoleOutputContains("Unknown rotation: '99990'");
    }

    @Test
    public void pageRotation_invalidPageDefinition() {
        defaultCommandLine().with("-r", "abc:0").assertConsoleOutputContains("Unknown page definition: 'abc'");
    }

    @Test
    public void pageRotation_invalidOption() {
        defaultCommandLine().with("-r", "invalid").assertConsoleOutputContains(
                "Invalid input: 'invalid'. Expected format: 'pageDefinition:rotation'");
    }

    @Test
    public void mandatoryParams() {
        defaultCommandLine().without("-r").assertConsoleOutputContains("Option is mandatory: --pageRotation");
    }
}
