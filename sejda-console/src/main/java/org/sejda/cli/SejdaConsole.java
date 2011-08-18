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

import org.sejda.core.exception.SejdaRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;
import uk.co.flamingpenguin.jewel.cli.Cli;
import uk.co.flamingpenguin.jewel.cli.CliFactory;

/**
 * Sejda command line service. Responsible for interpreting the arguments, displaying help requests or delegating execution of commands
 * 
 * @author Eduard Weissmann
 * 
 */
public class SejdaConsole {
    public static final String EXECUTABLE_NAME = "sejda-console";
    private static final Logger LOG = LoggerFactory.getLogger(SejdaConsole.class);

    private final Cli<GeneralCliArguments> generalCli = CliFactory.createCli(GeneralCliArguments.class);
    private final RawArguments arguments;

    private final TaskExecutionAdapter taskExecutionAdapter;
    private GeneralCliArguments generalCliArguments;

    public SejdaConsole(String[] rawArguments, TaskExecutionAdapter taskExecutionAdapter) {
        this.arguments = new RawArguments(rawArguments.clone());
        this.taskExecutionAdapter = taskExecutionAdapter;
    }

    /**
     * Interprets and executes the console command
     */
    public void execute() {
        try {
            doExecute();
        } catch (ArgumentValidationException e) {
            LOG.info(e.getMessage());
        } catch (SejdaRuntimeException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void doExecute() throws ArgumentValidationException {
        LOG.debug("Starting execution with arguments: " + arguments);

        parseGeneralCliArguments();

        if (isNoCommandSpecified()) {
            printGeneralHelp();
        } else {
            CliCommand command = getCommandSpecified();

            if (isCommandHelpRequested()) {
                printCommandHelp(command);
            } else {
                executeCommand(command);
            }
        }

        LOG.debug("Completed execution");
    }

    private void executeCommand(CliCommand command) throws ArgumentValidationException {
        getTaskExecutionAdapter().execute(command.parseTaskParameters(arguments.getCommandArguments()));
    }

    private void parseGeneralCliArguments() throws ArgumentValidationException {
        generalCliArguments = generalCli.parseArguments(arguments.getGeneralArguments());
    }

    private void printCommandHelp(CliCommand command) {
        LOG.info(command.getHelpMessage());
    }

    private boolean isCommandHelpRequested() {
        return generalCliArguments.isHelp();
    }

    private CliCommand getCommandSpecified() {
        return generalCliArguments.getCommand().getCommand();
    }

    private void printGeneralHelp() {
        LOG.info(generalCli.getHelpMessage());
    }

    private boolean isNoCommandSpecified() {
        return !generalCliArguments.isCommand();
    }

    TaskExecutionAdapter getTaskExecutionAdapter() {
        return taskExecutionAdapter;
    }
}