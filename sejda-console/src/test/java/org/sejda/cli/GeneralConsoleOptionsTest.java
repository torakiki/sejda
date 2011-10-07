/*
 * Created on Jun 30, 2011
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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.sejda.core.Sejda;

/**
 * Tests for the command line's general options
 * 
 * @author Eduard Weissmann
 * 
 */
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

        for (CliCommand eachCommand : CliCommand.values()) {
            // each command should be mentioned
            expectedStrings.add(eachCommand.getDisplayName());
            // together with its description
            // TODO: add a test verifying that descriptions are included. What to do with formatting, test should be formatting-unaware? Should formatting also be tested?
            // expectedStrings.add(eachCommand.getDescription());
        }
        return expectedStrings;
    }

    @Test
    public void testCommandHelp() {
        for (CliCommand eachCliCommand : CliCommand.values()) {
            // it should contain description, example usage and usage details
            assertConsoleOutputContains("-h " + eachCliCommand.getDisplayName(), eachCliCommand.getDescription(),
                    "Example usage: ", "Usage: ");
        }
    }

    @Test
    public void testExecuteVersion() {
        assertConsoleOutputContains("--version", "Sejda Console (Version " + Sejda.VERSION + ")");
    }

    @Test
    public void testExecuteLicense() {
        assertConsoleOutputContains("--license", "Licensed under the Apache License, Version 2.0");
    }

    @Test
    public void testExecuteUnknownCommandHelp() {
        assertConsoleOutputContains("-h unknownCommand", "Unknown command: 'unknownCommand'");
    }

    @Test
    public void testExecuteUnknownCommand() {
        assertConsoleOutputContains("unknownCommand", "Unknown command: 'unknownCommand'");
    }
}
