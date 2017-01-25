/*
 * Created on Jul 10, 2011
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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.mockito.ArgumentCaptor;
import org.sejda.cli.util.OnceWithMessage;
import org.sejda.cli.util.SystemOutRecordingStream;
import org.sejda.core.notification.context.GlobalNotificationContext;
import org.sejda.core.service.DefaultTaskExecutionService;
import org.sejda.core.service.TaskExecutionService;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.notification.EventListener;
import org.sejda.model.notification.event.TaskExecutionCompletedEvent;
import org.sejda.model.parameter.base.TaskParameters;

/**
 * Builder for command line arguments, used in tests
 * 
 * @author Eduard Weissmann
 * 
 */
public class CommandLineTestBuilder {

    private final String taskName;
    private final Map<String, String> optionsAndValues = new HashMap<>();

    public CommandLineTestBuilder(String taskName) {
        this.taskName = taskName;
    }

    /**
     * Populates 2 default input parameters
     * 
     * @return this builder (for telescopic usage)
     */
    public CommandLineTestBuilder defaultTwoInputs() {
        with("-f", "inputs/input.pdf inputs/second_input.pdf");
        return this;
    }

    /**
     * Populates multiple input parameters using PDF and non PDf inputs
     * 
     * @return this builder (for telescopic usage)
     */
    public CommandLineTestBuilder defaultMultipleNonPdfInputs() {
        with("-f", "inputs/input.pdf inputs/second_input.pdf inputs/file1.txt");
        return this;
    }

    /**
     * Populates default single input parameter
     * 
     * @return this builder (for telescopic usage)
     */
    public CommandLineTestBuilder defaultSingleInput() {
        with("-f", "inputs/input.pdf");
        return this;
    }

    /**
     * Populates default multiple input parameter
     *
     * @return this builder (for telescopic usage)
     */
    public CommandLineTestBuilder defaultMultiplePdfInputs() {
        with("-f", "inputs/input.pdf inputs/second_input.pdf");
        return this;
    }

    /**
     * Populates default output parameter as folder ./outputs
     * 
     * @return this builder (for telescopic usage)
     */
    public CommandLineTestBuilder defaultFolderOutput() {
        with("-o", "./outputs");
        return this;
    }

    /**
     * Populates default output parameter as file fileOutput.pdf
     * 
     * @return this builder (for telescopic usage)
     */
    public CommandLineTestBuilder defaultFileOutput() {
        with("-o", "./outputs/fileOutput.pdf");
        return this;
    }

    /**
     * Removes flag/option specified
     * 
     * @param option
     *            option to remove
     * @return this builder (for telescopic usage)
     */
    public CommandLineTestBuilder without(String option) {
        optionsAndValues.remove(option);
        return this;
    }

    /**
     * Removes any flags/options already added
     * 
     * @return this builder (for telescopic usage)
     * 
     */
    public CommandLineTestBuilder reset() {
        optionsAndValues.clear();
        return this;
    }

    /**
     * Adds a new option and it's value to the command
     * 
     * @param option
     * @param value
     * @return this builder (for telescopic usage)
     */
    public CommandLineTestBuilder with(String option, String value) {
        optionsAndValues.put(option, value);
        return this;
    }

    /**
     * Adds a new boolean flag option
     * 
     * @param option
     * @return this builder (for telescopic usage)
     */
    public CommandLineTestBuilder withFlag(String option) {
        optionsAndValues.put(option, null);
        return this;
    }

    /**
     * Builds a command line string, that calls the task specified as input, using the collected options & values
     * 
     * @return command line as string
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
        new CommandLineExecuteTestHelper(true).assertConsoleOutputContains(this.toCommandLineString(),
                expectedOutputContainedLines);
    }

    public <T> T invokeSejdaConsole() {
        return (T) new CommandLineExecuteTestHelper(true)
                .invokeConsoleAndReturnTaskParameters(this.toCommandLineString());
    }
}

/**
 * Helper test class for execution of the sejda-console<br/>
 * Contains helper methods such as {@link #assertConsoleOutputContains(String, String...)}, {@link #invokeConsoleAndReturnTaskParameters(String)}<br/>
 * 
 * @author Eduard Weissmann
 * 
 */
class CommandLineExecuteTestHelper {
    protected TaskExecutionService taskExecutionService;
    private SystemOutRecordingStream newSystemOut = new SystemOutRecordingStream(System.out);
    private Map<CustomizableProps, String> customs;

    CommandLineExecuteTestHelper(boolean useMockTaskExecutionService) {
        this(useMockTaskExecutionService, new HashMap<>());
        customs.put(CustomizableProps.APP_NAME, "Sejda Console");
        customs.put(CustomizableProps.LICENSE_PATH, "/sejda-console/LICENSE.txt");
    }

    CommandLineExecuteTestHelper(boolean useMockTaskExecutionService, Map<CustomizableProps, String> customs) {
        if (useMockTaskExecutionService) {
            taskExecutionService = mock(TaskExecutionService.class);
        } else {
            taskExecutionService = new DefaultTaskExecutionService();
        }
        this.customs = customs;
    }

    private SejdaConsole getConsole(String commandLine) {
        String[] args = parseCommandLineArgs(commandLine);
        return new SejdaConsole(args, new DefaultTaskExecutionAdapter(taskExecutionService), customs);
    }

    /**
     * Simulate's java's cli argument parsing. That means {@code java MyProgram 1234 www.caltech.edu "olive festival"} has 3 arguments: <br/>
     * <ul>
     * <li>args[0] = "1234"</li>
     * <li>args[1] = "www.caltech.edu"</li>
     * <li>args[2] = "olive festival"</li>
     * <ul/>
     * 
     */
    static String[] parseCommandLineArgs(String commandLine) {
        List<String> result = new ArrayList<String>();

        Matcher m = Pattern.compile("\"([^\"]*)\"|(\\S+)").matcher(commandLine);
        while (m.find()) {
            if (m.group(1) != null) {
                result.add(m.group(1));
            } else {
                result.add(m.group(2));
            }
        }
        return result.toArray(new String[] {});
    }

    public void assertConsoleOutputContains(String commandLine, String... expectedOutputContainedLines) {
        String consoleOutput = invokeConsoleAndReturnSystemOut(commandLine);
        for (String eachExpected : expectedOutputContainedLines) {
            assertThat(consoleOutput, containsString(eachExpected));
        }
    }

    public void assertTaskCompletes(String commandLine) {
        final MutableBoolean taskCompleted = new MutableBoolean(false);
        GlobalNotificationContext.getContext().addListener(new EventListener<TaskExecutionCompletedEvent>() {

            @Override
            public void onEvent(TaskExecutionCompletedEvent event) {
                taskCompleted.setValue(true);
            }

        });

        String consoleOutput = invokeConsoleAndReturnSystemOut(commandLine);
        assertThat("Task did not complete. Console output was:\n" + consoleOutput, taskCompleted.toBoolean(), is(true));
    }

    private String invokeConsoleAndReturnSystemOut(String commandLine) {
        invokeConsoleIgnoringExpectedExceptions(commandLine);

        return getCapturedSystemOut();
    }

    private void invokeConsoleIgnoringExpectedExceptions(String commandLine) {
        prepareSystemOutCapture();
        try {
            getConsole(commandLine).execute();
            // fail("Console execution should have failed, no? " + commandLine);
        } catch (SejdaRuntimeException e) {
            // no-op
        } catch (Exception e) {
            throw new SejdaRuntimeException("An unexpected exception occured while executing the console", e);
        }
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

        invokeConsoleIgnoringExpectedExceptions(commandLine);

        // now Mockito can provide some context to verification failures, yay
        verify(taskExecutionService,
                once("Command '" + commandLine
                        + "' did not reach task execution, as was expected. Console output was: \n"
                        + getCapturedSystemOut())).execute(taskPrametersCaptor.capture());
        return (T) taskPrametersCaptor.getValue();
    }

    private static OnceWithMessage once(String failureDescribingMessage) {
        return new OnceWithMessage(failureDescribingMessage);
    }
}
