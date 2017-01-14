/*
 * Created on Jul 6, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.sejda.cli.command.CliCommand;
import org.sejda.cli.command.CliCommands;

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

        for (CliCommand eachCliCommand : CliCommands.COMMANDS) {
            assertFalse(rawArgumentsFrom(eachCliCommand.getDisplayName() + " -h").isNoCommandSpecified());
            assertFalse(rawArgumentsFrom("-h " + eachCliCommand.getDisplayName()).isNoCommandSpecified());
        }
    }

    @Test
    public void getCliCommand() {
        for (CliCommand eachCliCommand : CliCommands.COMMANDS) {
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
