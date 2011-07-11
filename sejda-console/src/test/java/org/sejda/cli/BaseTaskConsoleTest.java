/*
 * Created on Jul 9, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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
import org.sejda.core.manipulation.model.parameter.PdfSourceListParameters;
import org.sejda.core.manipulation.model.pdf.PdfVersion;

/**
 * @author Eduard Weissmann
 * 
 */
public abstract class BaseTaskConsoleTest extends BaseConsoleTest {

    /**
     * @return task name
     */
    abstract String getTaskName();

    /**
     * @return the minimum command line arguments that covers the mandatory arguments with reasonable defaults
     */
    abstract CommandLineTestBuilder getMandatoryCommandLineArgumentsWithDefaults();

    @Before
    public void setUp() {
        createTestFile("./inputs/input.pdf");
        createTestFile("./inputs/input-protected.pdf");
        createTestFile("./inputs/input-unprotected.pdf");
        createTestFolder("./outputs");
    }

    @Test
    public void testMandatoryOptions() {
        assertConsoleOutputContains(getTaskName(), "Option is mandatory: --files -f");
        assertConsoleOutputContains(getTaskName(), "Option is mandatory: --output");
    }

    @Test
    public void testSourceFileNotFound() {
        assertConsoleOutputContains(getMandatoryCommandLineArgumentsWithDefaults().with("-f", "input-doesntexist.pdf")
                .toString(), "File 'input-doesntexist.pdf' does not exist");
    }

    @Test
    public void testOutputDirectoryNotFound() {
        assertConsoleOutputContains(getMandatoryCommandLineArgumentsWithDefaults().with("-o", "output-doesntexist")
                .toString(), "Path 'output-doesntexist' does not exist");
    }

    @Test
    public void testSourceFileNoPassword() {
        PdfSourceListParameters result = invokeConsoleAndReturnTaskParameters(getMandatoryCommandLineArgumentsWithDefaults()
                .with("-f", "inputs/input.pdf").toString());

        assertEquals(1, result.getSourceList().size());
        assertHasFileSource(result, new File("inputs/input.pdf"), null);
    }

    @Test
    public void testSourceFileWithPassword() {
        PdfSourceListParameters result = invokeConsoleAndReturnTaskParameters(getMandatoryCommandLineArgumentsWithDefaults()
                .with("-f", "inputs/input-protected.pdf;secret123").toString());

        assertEquals(1, result.getSourceList().size());
        assertHasFileSource(result, new File("inputs/input-protected.pdf"), "secret123");
    }

    @Test
    public void testBaseParameters_AllOn() {
        PdfSourceListParameters result = invokeConsoleAndReturnTaskParameters(getMandatoryCommandLineArgumentsWithDefaults()
                .with("--pdfVersion", "VERSION_1_2").with("--overwrite").with("--compressed")
                .with("-f", "inputs/input.pdf").with("-o", "./outputs").toString());
        assertTrue(result.isCompressXref());
        assertOutputFolder(result, new File("./outputs"));

        assertEquals(1, result.getSourceList().size());
        assertHasFileSource(result, new File("inputs/input.pdf"), null);
        assertEquals(PdfVersion.VERSION_1_2, result.getVersion());
        assertTrue(result.isOverwrite());
    }

    @Test
    public void testBaseParameters_AllOff() {
        PdfSourceListParameters result = invokeConsoleAndReturnTaskParameters(getMandatoryCommandLineArgumentsWithDefaults()
                .toString());
        assertFalse(result.isCompressXref());
        assertOutputFolder(result, new File("./outputs"));

        assertEquals(1, result.getSourceList().size());
        assertHasFileSource(result, new File("inputs/input.pdf"), null);
        assertEquals(PdfVersion.VERSION_1_6, result.getVersion());
        assertFalse(result.isOverwrite());
    }

}
