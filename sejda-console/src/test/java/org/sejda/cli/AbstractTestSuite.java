/*
 * Created on Aug 18, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfMergeInput;
import org.sejda.model.input.PdfSource;
import org.sejda.model.output.*;
import org.sejda.model.parameter.AlternateMixMultipleInputParameters;
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
        return createTestFile(path, getClass().getResourceAsStream("/pdf/test_outline.pdf"));
    }

    protected File createTestTextFile(String path, String contents) {
        return createTestFile(path, new ByteArrayInputStream(contents.getBytes()));
    }

    protected File overwriteTestPdfFile(String path, String resource) {
        return createTestFile(path, getClass().getResourceAsStream(resource));
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
        if (parameters instanceof AlternateMixMultipleInputParameters) {
            assertHasFileSource((AlternateMixMultipleInputParameters) parameters, file, password);
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
        List<PdfSource<?>> sourcesList = new ArrayList<PdfSource<?>>();
        for (PdfMergeInput eachInput : parameters.getPdfInputList()) {
            sourcesList.add(eachInput.getSource());
        }

        assertHasFileSource(sourcesList, file, password);
    }

    protected void assertHasFileSource(AlternateMixMultipleInputParameters parameters, File file, String password) {
        assertHasFileSource(Arrays.asList(parameters.getInputList().get(0).getSource(),
                parameters.getInputList().get(1).getSource()), file, password);
    }

    protected void assertHasFileSource(MultiplePdfSourceTaskParameters parameters, File file, String password) {
        assertHasFileSource(parameters.getSourceList(), file, password);
    }

    protected void assertHasFileSource(Collection<PdfSource<?>> parametersPdfSources, File file, String password) {
        boolean found = false;
        for (PdfSource<?> each : parametersPdfSources) {
            if (matchesPdfFileSource(file, password, each)) {
                found = true;
            }
        }

        assertTrue(
                "File '" + file + "'"
                        + (StringUtils.isEmpty(password) ? " and no password" : " and password '" + password + "'"),
                found);
    }

    protected boolean matchesPdfFileSource(File file, String password, PdfSource<?> each) {
        return (each.getSource().equals(file) && StringUtils.equals(each.getPassword(), password));
    }

    protected void assertOutputFolder(TaskParameters result, final Path expected) throws TaskException {
        result.getOutput().accept(new TaskOutputDispatcher() {

            @Override
            public void dispatch(DirectoryTaskOutput output) {
                assertEquals(expected.toAbsolutePath().normalize().toFile(), output.getDestination());
            }

            @Override
            public void dispatch(FileTaskOutput output) {
                fail("wrong dispached method");
            }

            @Override
            public void dispatch(FileOrDirectoryTaskOutput output) {
                assertEquals(expected.toAbsolutePath().normalize().toFile(), output.getDestination());
            }
        });
    }

    protected void assertOutputFile(TaskOutput output, final Path expected) throws TaskException {
        output.accept(new TaskOutputDispatcher() {

            @Override
            public void dispatch(DirectoryTaskOutput output) {
                fail("wrong dispached method");
            }

            @Override
            public void dispatch(FileTaskOutput output) {
                assertEquals(expected.toAbsolutePath().normalize().toFile(), output.getDestination());
            }

            @Override
            public void dispatch(FileOrDirectoryTaskOutput output) {
                assertEquals(expected.toAbsolutePath().normalize().toFile(), output.getDestination());
            }
        });
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
