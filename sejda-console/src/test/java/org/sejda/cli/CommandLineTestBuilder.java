/*
 * Created on Jul 10, 2011
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

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mockito.ArgumentCaptor;
import org.sejda.core.manipulation.model.parameter.TaskParameters;
import org.sejda.core.manipulation.service.TaskExecutionService;
import org.sejda.util.OnceWithMessage;
import org.sejda.util.SystemOutRecordingStream;

/**
 * Builder for command line arguments, used in tests
 * 
 * @author Eduard Weissmann
 * 
 */
public class CommandLineTestBuilder {

    private final String taskName;
    private final Map<String, String> optionsAndValues = new HashMap<String, String>();

    public CommandLineTestBuilder(String taskName) {
        this.taskName = taskName;
        defaultInput();
    }

    /**
     * Populates default input parameter as inputs/input.pdf
     */
    private void defaultInput() {
        with("-f", "inputs/input.pdf inputs/second_input.pdf");
    }

    /**
     * Populates default output parameter as folder ./outputs
     */
    public void defaultFolderOutput() {
        with("-o", "./outputs");
    }

    /**
     * Populates default output parameter as file fileOutput.pdf
     */
    public void defaultFileOutput() {
        with("-o", "./outputs/fileOutput.pdf");
    }

    /**
     * Adds a new option and it's value to the command
     * 
     * @param option
     * @param value
     * @return
     */
    public CommandLineTestBuilder with(String option, String value) {
        optionsAndValues.put(option, value);
        return this;
    }

    public CommandLineTestBuilder with(String option) {
        optionsAndValues.put(option, null);
        return this;
    }

    /**
     * Builds a command line string, that calls the task specified as input, using the collected options & values
     * 
     * @param taskName
     * @return
     */
    public String toCommandLineString() {
        StringBuilder result = new StringBuilder(taskName);

        for (Map.Entry<String, String> eachOptionAndValue : optionsAndValues.entrySet()) {
            result.append(" ").append(eachOptionAndValue.getKey());
            if (eachOptionAndValue.getValue() != null) {
                result.append(" ").append(eachOptionAndValue.getValue());
            }
        }

        return result.toString();
    }

    public void assertConsoleOutputContains(String... expectedOutputContainedLines) {
        new CommandLineExecuteTestHelper().assertConsoleOutputContains(this.toCommandLineString(),
                expectedOutputContainedLines);
    }

    public <T> T invokeSejdaConsole() {
        return (T) new CommandLineExecuteTestHelper().invokeConsoleAndReturnTaskParameters(this.toCommandLineString());
    }
}

/**
 * Helper test class for execution of the sejda-console<br/>
 * Contains helper methods such as {@link #assertConsoleOutputContains(String...)}, {@link #invokeConsoleAndReturnTaskParameters(String)}<br/>
 * 
 * @author Eduard Weissmann
 * 
 */
class CommandLineExecuteTestHelper {
    protected TaskExecutionService taskExecutionService = mock(TaskExecutionService.class);
    private SystemOutRecordingStream newSystemOut = new SystemOutRecordingStream(System.out);

    private SejdaConsole getConsole(String commandLine) {
        String[] args = parseCommandLineArgs(commandLine);
        return new SejdaConsole(args, new DefaultTaskExecutionAdapter(taskExecutionService));
    }

    private String[] parseCommandLineArgs(String commandLine) {
        return StringUtils.stripAll(StringUtils.splitPreserveAllTokens(commandLine));
    }

    public void assertConsoleOutputContains(String commandLine, String... expectedOutputContainedLines) {
        String consoleOutput = invokeConsoleAndReturnSystemOut(commandLine);
        for (String eachExpected : expectedOutputContainedLines) {
            assertThat(consoleOutput, containsString(eachExpected));
        }
    }

    private String invokeConsoleAndReturnSystemOut(String commandLine) {
        invokeConsole(commandLine);

        return getCapturedSystemOut();
    }

    private void invokeConsole(String commandLine) {
        prepareSystemOutCapture();
        getConsole(commandLine).execute();
    }

    private void prepareSystemOutCapture() {
        newSystemOut = new SystemOutRecordingStream(System.out);
        System.setOut(new PrintStream(newSystemOut));
    }

    private String getCapturedSystemOut() {
        return newSystemOut.getCapturedSystemOut();
    }

    @SuppressWarnings("unchecked")
    public <T extends TaskParameters> T invokeConsoleAndReturnTaskParameters(String commandLine) {
        ArgumentCaptor<TaskParameters> taskPrametersCaptor = ArgumentCaptor.forClass(TaskParameters.class);

        invokeConsole(commandLine);

        // now Mockito can provide some context to verification failures, yay
        verify(
                taskExecutionService,
                once("Command '" + commandLine
                        + "' did not reach task execution, as was expected. Console output was: \n"
                        + getCapturedSystemOut())).execute(taskPrametersCaptor.capture());
        return (T) taskPrametersCaptor.getValue();
    }

    private static OnceWithMessage once(String failureDescribingMessage) {
        return new OnceWithMessage(failureDescribingMessage);
    }
}
