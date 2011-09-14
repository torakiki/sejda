/*
 * Created on Aug 18, 2011
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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.hasItem;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.sejda.core.exception.SejdaRuntimeException;
import org.sejda.core.manipulation.model.input.PdfFileSource;
import org.sejda.core.manipulation.model.input.PdfSource;
import org.sejda.core.manipulation.model.output.DirectoryOutput;
import org.sejda.core.manipulation.model.output.FileOutput;
import org.sejda.core.manipulation.model.output.OutputType;
import org.sejda.core.manipulation.model.output.TaskOutput;
import org.sejda.core.manipulation.model.parameter.MultiplePdfSourceParameters;
import org.sejda.core.manipulation.model.parameter.SinglePdfSourceParameters;
import org.sejda.core.manipulation.model.parameter.TaskParameters;

/**
 * Base class for test suites, provides helper methods to ease testing
 * 
 * @author Eduard Weissmann
 * 
 */
public abstract class AbstractTestSuite {

    public <T> void assertContains(T expectedItem, Collection<T> actualItems) {
        assertThat(actualItems, hasItem(expectedItem));
    }

    public <T> void assertContainsAll(Collection<T> expectedItems, Collection<T> actualItems) {
        assertContainsAll("", expectedItems, actualItems);
    }

    public <T> void assertContainsAll(String message, Collection<T> expectedItems, Collection<T> actualItems) {
        for (T eachExpectedItem : expectedItems) {
            assertThat(message, actualItems, hasItem(eachExpectedItem));
        }
    }

    protected File createTestFile(String path) {
        return createTestFile(path, "default contents");
    }

    protected File createTestFile(String path, String contents) {
        File file = new File(path);
        file.deleteOnExit();
        try {
            FileUtils.writeStringToFile(file, contents);
        } catch (IOException e) {
            throw new SejdaRuntimeException("Can't create test file. Reason: " + e.getMessage(), e);
        }

        return file;
    }

    protected File createTestFolder(String path) {
        File file = new File(path);
        file.mkdirs();
        file.deleteOnExit();

        return file;
    }

    protected void assertHasFileSource(SinglePdfSourceParameters parameters, File file, String password) {
        assertTrue("File '" + file + "'"
                + (StringUtils.isEmpty(password) ? " and no password" : " and password '" + password + "'"),
                matchesPdfFileSource(file, password, parameters.getSource()));
    }

    protected void assertHasFileSource(MultiplePdfSourceParameters parameters, File file, String password) {
        boolean found = false;
        for (PdfSource each : parameters.getSourceList()) {
            if (matchesPdfFileSource(file, password, each)) {
                found = true;
            }
        }

        assertTrue("File '" + file + "'"
                + (StringUtils.isEmpty(password) ? " and no password" : " and password '" + password + "'"), found);
    }

    protected boolean matchesPdfFileSource(File file, String password, PdfSource each) {
        return ((PdfFileSource) each).getFile().equals(file) && StringUtils.equals(each.getPassword(), password);
    }

    protected void assertOutputFolder(TaskParameters result, File outputFolder) {
        assertEquals(result.getOutput().getOutputType(), OutputType.DIRECTORY_OUTPUT);
        assertEquals(((DirectoryOutput) result.getOutput()).getDirectory(), outputFolder);
    }

    protected void assertOutputFile(TaskOutput output, File outputFile) {
        assertEquals(output.getOutputType(), OutputType.FILE_OUTPUT);
        assertEquals(((FileOutput) output).getFile(), outputFile);
    }

    public static void assertConsoleOutputContains(String commandLine, String... expectedOutputContainedLines) {
        new CommandLineExecuteTestHelper().assertConsoleOutputContains(commandLine, expectedOutputContainedLines);
    }
}
