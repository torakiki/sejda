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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.sejda.core.manipulation.model.parameter.DecryptParameters;
import org.sejda.core.manipulation.model.pdf.PdfVersion;
import org.sejda.core.manipulation.model.task.itext.DecryptTask;

/**
 * Tests for the {@link DecryptTask} command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class DecryptConsoleTest extends BaseConsoleTest {

    @Before
    public void setUp() {
        createTestFile("./inputs/input.pdf");
        createTestFile("./inputs/input-protected.pdf");
        createTestFolder("./outputs");
    }

    @Test
    public void testExecuteCommandHelp() {
        assertConsoleOutputContains(
                "-h decrypt",
                "Usage: sejda-console decrypt options",
                "[--compressed] : compress output file (optional)",
                "--files -f value... : pdf files to decrypt: a list of existing pdf files (EX. -f /tmp/file1.pdf -f /tmp/file2.pdf) (required)",
                "--output -o value : output directory (required)",
                "--outputPrefix -p value : prefix for the output files name (optional)",
                "[--overwrite] : overwrite existing output file (optional)",
                "--pdfVersion -v value : pdf version of the output document/s. (optional)");
    }

    @Test
    public void testMandatoryOptions() {
        assertConsoleOutputContains("decrypt", "Option is mandatory: --files -f");
        assertConsoleOutputContains("decrypt --files inputs/input.pdf", "Option is mandatory: --output");
    }

    @Test
    public void testSourceFileNotFound() {
        assertConsoleOutputContains("decrypt --compressed --files input-doesntexist.pdf --output .",
                "File 'input-doesntexist.pdf' does not exist");
    }

    @Test
    public void testOutputDirectoryNotFound() {
        assertConsoleOutputContains("decrypt --compressed --files inputs/input.pdf -o output-doesntexist",
                "Path 'output-doesntexist' does not exist");
    }

    @Test
    public void testSourceFileNoPassword() {
        DecryptParameters result = invokeConsoleAndReturnTaskParameters("decrypt --files inputs/input.pdf --o ./outputs");

        assertEquals(1, result.getSourceList().size());
        assertHasFileSource(result, new File("inputs/input.pdf"), null);
    }

    @Test
    public void testSourceFileWithPassword() {
        DecryptParameters result = invokeConsoleAndReturnTaskParameters("decrypt --files inputs/input-protected.pdf;secret123 --o ./outputs");

        assertEquals(1, result.getSourceList().size());
        assertHasFileSource(result, new File("inputs/input-protected.pdf"), "secret123");
    }

    @Test
    public void testBaseParameters_AllOn() {
        DecryptParameters result = invokeConsoleAndReturnTaskParameters("decrypt --pdfVersion VERSION_1_2 --overwrite --compressed --files inputs/input.pdf --o ./outputs");
        assertTrue(result.isCompressXref());
        assertOutputFolder(result, new File("./outputs"));

        assertEquals(1, result.getSourceList().size());
        assertHasFileSource(result, new File("inputs/input.pdf"), null);
        assertEquals(PdfVersion.VERSION_1_2, result.getVersion());
        assertTrue(result.isOverwrite());
    }

    @Test
    public void testBaseParameters_AllOff() {
        DecryptParameters result = invokeConsoleAndReturnTaskParameters("decrypt --files inputs/input.pdf --o ./outputs");
        assertFalse(result.isCompressXref());
        assertOutputFolder(result, new File("./outputs"));

        assertEquals(1, result.getSourceList().size());
        assertHasFileSource(result, new File("inputs/input.pdf"), null);
        assertEquals(PdfVersion.VERSION_1_6, result.getVersion());
        assertFalse(result.isOverwrite());
    }
}
