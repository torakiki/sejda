/*
 * Created on Jul 4, 2011
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

import java.io.PrintStream;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.sejda.core.exception.SejdaRuntimeException;

import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;
import uk.co.flamingpenguin.jewel.cli.Cli;
import uk.co.flamingpenguin.jewel.cli.CliFactory;

/**
 * Main entry point for the sejda command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class SejdaConsole {
    public static final String NAME = "sejda-console";

    public static void main(String[] args) {
        new SejdaConsole().execute(args);
    }

    /**
     * Executes the sejda command line interface, using specified parameters as input
     * 
     * @param args
     *            command line arguments as strings
     */
    public void execute(final String[] args) {
        try {
            final SejdaConsoleArguments arguments = new SejdaConsoleArguments(args);

            Cli<GeneralOptions> generalCli = CliFactory.createCli(GeneralOptions.class);
            final GeneralOptions generalOptions = generalCli.parseArguments(arguments.getGeneralOptionsArguments());

            // no command specified, print general Help
            if (!generalOptions.isCommand()) {
                println(generalCli.getHelpMessage());
                return;
            }

            String commandName = generalOptions.getCommand();
            Class<? extends CommandOptions> commandOptionsClazz = getCommandOptionsClassByCommandName(commandName);

            Cli<? extends CommandOptions> commandCli = CliFactory.createCli(commandOptionsClazz);

            // print command Help
            if (generalOptions.isHelp()) {
                println(commandCli.getHelpMessage());
                return;
            }

            // execute command
            getTaskExecutionFacade().executeCommand(commandCli.parseArguments(arguments.getCommandOptionArguments()),
                    commandName);
        } catch (ArgumentValidationException e) {
            printError(e);
            println(e.getMessage());
        } catch (SejdaRuntimeException e) {
            printError(e);
            println(e.getMessage());
        }
    }

    /**
     * Prints the specified error to default err print stream
     * 
     * @param e
     */
    private void printError(Exception e) {
        e.printStackTrace(getDefaultErrorPrintStream());
    }

    private PrintStream getDefaultErrorPrintStream() {
        return System.err;
    }

    private PrintStream getDefaultOutPrintStream() {
        return System.out;
    }

    /**
     * Prints the specified line to the default out print stream
     * 
     * @param line
     */
    private void println(String line) {
        getDefaultOutPrintStream().println(line);
    }

    private final CommandExecutionService taskExecutionFacade = new DefaultCommandExecutionService();

    CommandExecutionService getTaskExecutionFacade() {
        return taskExecutionFacade;
    }

    private static final String COMMAND_OPTIONS_CLAZZ_SUFFIX = "CommandOptions";

    /**
     * Each command has a corresponding interface implementing {@link CommandOptions} and configuring specific CLI options<br/>
     * This method retrieves the {@link CommandOptions} extending class corresponding to the command name specified<br/>
     * Eg: for command "decrypt" this method will return {@link DecryptCommandOptions} class
     * 
     * @param commandName
     *            the command name
     * @return an interface defining the command line options for the specified command
     */
    @SuppressWarnings("unchecked")
    private Class<? extends CommandOptions> getCommandOptionsClassByCommandName(String commandName) {
        try {
            return (Class<? extends CommandOptions>) Class.forName("org.sejda.cli."
                    + StringUtils.capitalize(commandName) + COMMAND_OPTIONS_CLAZZ_SUFFIX);
        } catch (Exception e) {
            throw new SejdaRuntimeException("Unknown command: '" + commandName + "'", e);
        }
    }
}

/**
 * Represents the command line arguments passed to the {@link SejdaConsole}
 * 
 * @author Eduard Weissmann
 * 
 */
class SejdaConsoleArguments {
    private final String[] arguments;

    /**
     * @param arguments
     *            string representation of the command line arguments
     */
    public SejdaConsoleArguments(String[] arguments) {
        super();
        this.arguments = arguments.clone();
    }

    public boolean isHelpRequest() {
        return (this.arguments.length >= 1 && StringUtils.equalsIgnoreCase("-h", arguments[0]))
                || this.arguments.length == 0;
    }

    /**
     * @return only arguments relevant for the {@link GeneralOptions}
     * @see GeneralOptions to understand the distinction between {@link GeneralOptions} and {@link CommandOptions}
     */
    String[] getGeneralOptionsArguments() {
        int min = Math.min(isHelpRequest() ? 2 : 1, arguments.length);
        return (String[]) ArrayUtils.subarray(arguments, 0, min);
    }

    /**
     * @return only arguments relevant for the {@link CommandOptions}
     * @see CommandOptions to understand the distinction between {@link GeneralOptions} and {@link CommandOptions}
     */
    String[] getCommandOptionArguments() {
        return (String[]) ArrayUtils.subarray(arguments, 1, arguments.length);
    }

};