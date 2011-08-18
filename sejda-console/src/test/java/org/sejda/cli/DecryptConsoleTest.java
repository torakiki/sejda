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
import org.sejda.core.manipulation.model.parameter.DecryptParameters;

/**
 * Tests for the DecryptTask command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class DecryptConsoleTest extends BaseTaskConsoleTest {

    @Override
    String getTaskName() {
        return "decrypt";
    }

    @Override
    protected CommandLineTestBuilder getMandatoryCommandLineArgumentsWithDefaults() {
        return new CommandLineTestBuilder(getTaskName());
    }

    @Test
    public void testExecuteCommandHelp() {
        assertConsoleOutputContains("-h " + getTaskName(), "Usage: sejda-console decrypt options");
    }

    @Test
    public void testOutputPrefix_Specified() {
        DecryptParameters parameters = invokeConsoleAndReturnTaskParameters(getMandatoryCommandLineArgumentsWithDefaults()
                .with("-p", "fooPrefix").toString());
        assertEquals("fooPrefix", parameters.getOutputPrefix());
    }

    @Test
    public void testOutputPrefix_Default() {
        DecryptParameters parameters = invokeConsoleAndReturnTaskParameters(getMandatoryCommandLineArgumentsWithDefaults()
                .toString());
        assertEquals("decrypted_", parameters.getOutputPrefix());
    }
}
