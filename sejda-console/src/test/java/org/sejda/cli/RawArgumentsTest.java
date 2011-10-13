/*
 * Created on Jul 6, 2011
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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.sejda.cli.transformer.CliCommand;

/**
 * Tests for {@link RawArguments}
 * 
 * @author Eduard Weissmann
 * 
 */
public class RawArgumentsTest {

    @Test
    public void isVersionRequest() {
        assertTrue(rawArgumentsFrom("--version").isVersionRequest());
        assertTrue(rawArgumentsFrom("decrypt --version").isVersionRequest());
    }

    @Test
    public void isLicenseRequest() {
        assertTrue(rawArgumentsFrom("--license").isLicenseRequest());
        assertTrue(rawArgumentsFrom("decrypt --license").isLicenseRequest());
    }

    @Test
    public void isNoCommandSpecified() {
        assertTrue(rawArgumentsFrom("unknownCommand --options -f /tmp/file1.pdf").isNoCommandSpecified());
        assertTrue(rawArgumentsFrom(" ").isNoCommandSpecified());

        for (CliCommand eachCliCommand : CliCommand.values()) {
            assertFalse(rawArgumentsFrom(eachCliCommand.getDisplayName() + " -h").isNoCommandSpecified());
            assertFalse(rawArgumentsFrom("-h " + eachCliCommand.getDisplayName()).isNoCommandSpecified());
        }
    }

    @Test
    public void getCliCommand() {
        for (CliCommand eachCliCommand : CliCommand.values()) {
            assertEquals(eachCliCommand, rawArgumentsFrom(eachCliCommand.getDisplayName() + " -h").getCliCommand());
            assertEquals(eachCliCommand, rawArgumentsFrom("-h " + eachCliCommand.getDisplayName()).getCliCommand());
        }

        assertNull(rawArgumentsFrom(" ").getCliCommand());
        assertNull(rawArgumentsFrom("-h --license").getCliCommand());
    }

    @Test
    public void isHelpRequest() {
        assertTrue(rawArgumentsFrom("decrypt -h").isHelpRequest());
        assertTrue(rawArgumentsFrom("decrypt --help").isHelpRequest());

        assertTrue(rawArgumentsFrom("-h decrypt").isHelpRequest());
        assertTrue(rawArgumentsFrom("--help decrypt").isHelpRequest());
    }

    static RawArguments rawArgumentsFrom(String commandLine) {
        return new RawArguments(CommandLineExecuteTestHelper.parseCommandLineArgs(commandLine));
    }

    @Test
    public void testCommandOptionsArguments() {
        assertCommandOptionsArguments(new String[] { "command_param1", "command_param2", "command_param3" },
                new String[] { "command_name", "command_param1", "command_param2", "command_param3" });
    }

    private void assertCommandOptionsArguments(String[] expected, String[] input) {
        String[] actual = new RawArguments(input).getCommandArguments();
        assertArrayEquals("For input: '" + StringUtils.join(input, " ") + "'", expected, actual);
    }
}
