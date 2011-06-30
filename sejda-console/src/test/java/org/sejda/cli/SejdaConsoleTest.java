/*
 * Created on Jun 30, 2011
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

/**
 * @author Eduard Weissmann
 * 
 */
public class SejdaConsoleTest {

    @Test
    public void testExecuteWithoutArgs() {
        assertOutput(
                "",
                "Usage: sejda-console [options] command to execute {[concat], [split], [encrypt], [mix], [unpack], [setviewer], [slideshow], [decrypt], [rotate], [pagelabels]}",
                "[--help -h] : prints usage to stdout; exits (optional)");
    }

    @Test
    public void testExecuteHelp() {
        assertOutput(
                "-h",
                "Usage: sejda-console [options] command to execute {[concat], [split], [encrypt], [mix], [unpack], [setviewer], [slideshow], [decrypt], [rotate], [pagelabels]}",
                "[--help -h] : prints usage to stdout; exits (optional)");
    }

    @Test
    public void testExecuteCommandHelp() {
        assertOutput("-h decrypt", "Usage: sejda-console decrypt [options]",
                "[--compressed] : compress output file (optional)",
                "[--help -?] : prints usage to stdout; exits (optional)");
    }

    @Test
    public void testExecuteKnownCommand() {
        assertOutput("decrypt", "Executing command <decrypt>");
    }

    @Test
    public void testExecuteUnknownCommand() {
        assertOutput("-h unknownCommand", "Unknown command: unknownCommand");
    }

    private void assertOutput(String commandLine, String... expectedOutputLines) {
        assertEquals(StringUtils.join(expectedOutputLines, "\n"),
                StringUtils.join(invokeConsoleAndReturnSystemOut(commandLine), "\n"));
    }

    private String[] invokeConsoleAndReturnSystemOut(String command) {
        ByteArrayOutputStream capturedSystemOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedSystemOut));

        SejdaConsole.main(StringUtils.splitPreserveAllTokens(command));

        return StringUtils.stripAll(StringUtils.split(capturedSystemOut.toString(), "\n"));
    }
}
