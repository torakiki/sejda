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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.mockito.ArgumentCaptor;
import org.sejda.core.exception.SejdaRuntimeException;
import org.sejda.core.manipulation.model.input.PdfFileSource;
import org.sejda.core.manipulation.model.input.PdfSource;
import org.sejda.core.manipulation.model.output.OutputType;
import org.sejda.core.manipulation.model.output.PdfDirectoryOutput;
import org.sejda.core.manipulation.model.parameter.AbstractParameters;
import org.sejda.core.manipulation.model.parameter.PdfSourceListParameters;
import org.sejda.core.manipulation.model.parameter.TaskParameters;
import org.sejda.core.manipulation.service.TaskExecutionService;

/**
 * Base class for test that execute the console<br/>
 * Contains helper methods such as {@link #invokeConsoleAndReturnSystemOut(String)}, {@link #invokeConsoleAndReturnTaskParameters(String)}
 * 
 * @author Eduard Weissmann
 * 
 */
public class ConsoleTestBase {
    protected TaskExecutionService taskExecutionService = mock(TaskExecutionService.class);

    protected SejdaConsoleMain console = new SejdaConsoleMain() {
        @Override
        TaskExecutionAdapter getTaskExecutionAdapter() {
            return new DefaultTaskExecutionAdapter() {
                @Override
                TaskExecutionService getTaskExecutionService() {
                    return taskExecutionService;
                }
            };
        }
    };

    protected void assertConsoleOutputContains(String commandLineArguments, String... expectedOutputContainedLines) {
        String consoleOutput = invokeConsoleAndReturnSystemOut(commandLineArguments);
        for (String eachExpected : expectedOutputContainedLines) {
            assertThat(consoleOutput, containsString(eachExpected));
        }
    }

    private String invokeConsoleAndReturnSystemOut(String command) {
        SystemOutRecordingStream newSystemOut = new SystemOutRecordingStream(System.out);
        System.setOut(new PrintStream(newSystemOut));

        console.execute(StringUtils.stripAll(StringUtils.splitPreserveAllTokens(command)));

        return newSystemOut.getCapturedSystemOut();
    }

    /**
     * Records the contents of System.out, leaving the original stream untouched
     * 
     * @author Eduard Weissmann
     * 
     */
    class SystemOutRecordingStream extends FilterOutputStream {
        private final ByteArrayOutputStream capturedSystemOut = new ByteArrayOutputStream();

        public SystemOutRecordingStream(OutputStream underlyingSystemOut) {
            super(underlyingSystemOut);
        }

        @Override
        public void write(byte b[]) throws IOException {
            super.write(b);
            capturedSystemOut.write(b);
        }

        @Override
        public void write(byte b[], int off, int len) throws IOException {
            super.write(b, off, len);
            capturedSystemOut.write(b, off, len);
        }

        /**
         * @return the capturedSystemOut
         */
        public String getCapturedSystemOut() {
            return capturedSystemOut.toString();
        }
    }

    protected <T extends TaskParameters> T invokeConsoleAndReturnTaskParameters(String command) {
        ArgumentCaptor<TaskParameters> taskPrametersCaptor = ArgumentCaptor.forClass(TaskParameters.class);

        console.execute(StringUtils.stripAll(StringUtils.splitPreserveAllTokens(command)));

        verify(taskExecutionService).execute(taskPrametersCaptor.capture());
        return (T) taskPrametersCaptor.getValue();
    }

    protected File createTestFile(String path) {
        File file = new File(path);
        file.deleteOnExit();
        try {
            FileUtils.writeStringToFile(file, "pdf contents");
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

    /**
     * @param result
     * @param file
     */
    protected void assertHasFileSource(PdfSourceListParameters parameters, File file, String password) {
        boolean found = false;
        for (PdfSource each : parameters.getSourceList()) {
            if (((PdfFileSource) each).getFile().equals(file) && StringUtils.equals(each.getPassword(), password)) {
                found = true;
            }
        }

        assertTrue("File '" + file + "'"
                + (StringUtils.isEmpty(password) ? " and no password" : " and password '" + password + "'"), found);
    }

    /**
     * @param result
     */
    protected void assertOutputFolder(AbstractParameters result, File outputFolder) {
        assertEquals(result.getOutput().getOutputType(), OutputType.DIRECTORY_OUTPUT);
        assertEquals(((PdfDirectoryOutput) result.getOutput()).getFile(), outputFolder);
    }
}
