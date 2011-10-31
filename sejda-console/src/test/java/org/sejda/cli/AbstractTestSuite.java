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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.input.PdfMergeInput;
import org.sejda.model.input.PdfSource;
import org.sejda.model.output.DirectoryOutput;
import org.sejda.model.output.FileOutput;
import org.sejda.model.output.OutputType;
import org.sejda.model.output.TaskOutput;
import org.sejda.model.parameter.AlternateMixParameters;
import org.sejda.model.parameter.MergeParameters;
import org.sejda.model.parameter.base.MultiplePdfSourceTaskParameters;
import org.sejda.model.parameter.base.SinglePdfSourceTaskParameters;
import org.sejda.model.parameter.base.TaskParameters;

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

    protected File createTestPdfFile(String path) {
        return createTestFile(path, getClass().getResourceAsStream("/pdf/test_outline.pdf"));
    }

    protected File createTestEncryptedPdfFile(String path) {
        return createTestFile(path, getClass().getResourceAsStream("/pdf/enc_test_test_file.pdf"));
    }

    protected File createTestTextFile(String path, String contents) {
        return createTestFile(path, new ByteArrayInputStream(contents.getBytes()));
    }

    protected File createTestTextFile(String path, InputStream contents) {
        return createTestFile(path, contents);
    }

    protected File createTestFile(String path, InputStream contents) {
        File file = new File(path);
        file.deleteOnExit();
        try {
            FileUtils.copyInputStreamToFile(contents, file);
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

    protected void assertHasFileSource(TaskParameters parameters, File file, String password) {
        if (parameters instanceof AlternateMixParameters) {
            assertHasFileSource((AlternateMixParameters) parameters, file, password);
        } else if (parameters instanceof MergeParameters) {
            assertHasFileSource((MergeParameters) parameters, file, password);
        } else if (parameters instanceof SinglePdfSourceTaskParameters) {
            assertHasFileSource((SinglePdfSourceTaskParameters) parameters, file, password);
        } else if (parameters instanceof MultiplePdfSourceTaskParameters) {
            assertHasFileSource((MultiplePdfSourceTaskParameters) parameters, file, password);
        } else {
            throw new SejdaRuntimeException("Cannot assert has file source: " + parameters);
        }
    }

    protected void assertHasFileSource(SinglePdfSourceTaskParameters parameters, File file, String password) {
        assertTrue("File '" + file + "'"
                + (StringUtils.isEmpty(password) ? " and no password" : " and password '" + password + "'"),
                matchesPdfFileSource(file, password, parameters.getSource()));
    }

    protected void assertHasFileSource(MergeParameters parameters, File file, String password) {
        List<PdfSource> sourcesList = new ArrayList<PdfSource>();
        for (PdfMergeInput eachInput : parameters.getInputList()) {
            sourcesList.add(eachInput.getSource());
        }

        assertHasFileSource(sourcesList, file, password);
    }

    protected void assertHasFileSource(AlternateMixParameters parameters, File file, String password) {
        assertHasFileSource(
                Arrays.asList(parameters.getFirstInput().getSource(), parameters.getSecondInput().getSource()), file,
                password);
    }

    protected void assertHasFileSource(MultiplePdfSourceTaskParameters parameters, File file, String password) {
        assertHasFileSource(parameters.getSourceList(), file, password);
    }

    protected void assertHasFileSource(Collection<PdfSource> parametersPdfSources, File file, String password) {
        boolean found = false;
        for (PdfSource each : parametersPdfSources) {
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
        new CommandLineExecuteTestHelper(false).assertConsoleOutputContains(commandLine, expectedOutputContainedLines);
    }

    public static void assertTaskCompletes(String commandLine) {
        new CommandLineExecuteTestHelper(false).assertTaskCompletes(commandLine);
    }

    protected static <T> Set<T> asSet(T... items) {
        return new HashSet<T>(Arrays.asList(items));
    }
}
