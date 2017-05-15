/*
 * Created on Jun 30, 2011
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.sejda.StandardConsoleOnly;
import org.sejda.cli.command.CliCommand;
import org.sejda.cli.command.CliCommands;
import org.sejda.core.Sejda;

/**
 * Tests for the command line's general options
 * 
 * @author Eduard Weissmann
 * 
 */
@Category(StandardConsoleOnly.class)
public class GeneralConsoleOptionsTest extends AbstractTestSuite {

    @Test
    public void testExecuteWithoutArgs() {
        List<String> expectedStrings = getExpectedStringForGeneralHelp();

        assertConsoleOutputContains("", expectedStrings.toArray(new String[] {}));
    }

    @Test
    public void testExecuteHelp() {
        List<String> expectedStrings = getExpectedStringForGeneralHelp();

        assertConsoleOutputContains("-h", expectedStrings.toArray(new String[] {}));
    }

    private List<String> getExpectedStringForGeneralHelp() {
        List<String> expectedStrings = new ArrayList<String>();
        expectedStrings.add("Basic commands:");

        for (CliCommand eachCommand : CliCommands.COMMANDS) {
            // each command should be mentioned
            expectedStrings.add(eachCommand.getDisplayName());
            // together with its description
            // TODO: add a test verifying that descriptions are included. What to do with formatting, test should be formatting-unaware? Should formatting also be tested?
            // expectedStrings.add(eachCommand.getDescription());
        }
        return expectedStrings;
    }

    @Test
    public void testExecuteVersion() {
        Map<CustomizableProps, String> customs = new HashMap<>();
        customs.put(CustomizableProps.APP_NAME, "Sejda Console");
        customs.put(CustomizableProps.LICENSE_PATH, "/LICENSE.txt");
        new CommandLineExecuteTestHelper(false, customs).assertConsoleOutputContains("--version",
                "Sejda Console (Version " + Sejda.VERSION + ")");
    }

    @Test
    public void testExecuteLicense() throws IOException {
        Map<CustomizableProps, String> customs = new HashMap<>();
        customs.put(CustomizableProps.APP_NAME, "Sejda Console");
        customs.put(CustomizableProps.LICENSE_PATH, "/LICENSE.txt");
        try (InputStream resource = getClass().getResourceAsStream("/LICENSE.txt")) {
            new CommandLineExecuteTestHelper(false, customs).assertConsoleOutputContains("--license",
                    new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8)).lines()
                            .map(StringUtils::trim).toArray(String[]::new));
        }
    }

    @Test
    public void testExecuteUnknownCommandHelp() {
        assertConsoleOutputContains("-h unknownCommand", "Basic commands:");
        assertConsoleOutputContains("unknownCommand -h", "Basic commands:");
    }

    @Test
    public void testExecuteUnknownCommand() {
        assertConsoleOutputContains("unknownCommand", "Basic commands:");
    }
}
