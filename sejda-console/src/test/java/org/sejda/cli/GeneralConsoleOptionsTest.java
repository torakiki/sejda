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

import org.junit.Test;

/**
 * Tests for the command line's general options
 * 
 * @author Eduard Weissmann
 * 
 */
public class GeneralConsoleOptionsTest extends AbstractTestSuite {

    @Test
    public void testExecuteWithoutArgs() {
        assertConsoleOutputContains(
                "",
                "Usage: sejda-console [options] command to execute {[concat], [split], [encrypt], [mix], [unpack], [setviewer], [slideshow], [decrypt], [rotate], [pagelabels]}",
                "[--help -h] : prints this usage information. Can be used to detail options for a command '-h command' (optional)");
    }

    @Test
    public void testExecuteHelp() {
        assertConsoleOutputContains(
                "-h",
                "Usage: sejda-console [options] command to execute {[concat], [split], [encrypt], [mix], [unpack], [setviewer], [slideshow], [decrypt], [rotate], [pagelabels]}",
                "[--help -h] : prints this usage information. Can be used to detail options for a command '-h command' (optional)");
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
