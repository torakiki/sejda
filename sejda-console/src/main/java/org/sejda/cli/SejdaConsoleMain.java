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

import org.apache.commons.lang.StringUtils;
import org.sejda.core.exception.SejdaRuntimeException;
import org.sejda.core.manipulation.service.DefaultTaskExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;
import uk.co.flamingpenguin.jewel.cli.Cli;
import uk.co.flamingpenguin.jewel.cli.CliFactory;

/**
 * Main entry point for the sejda command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class SejdaConsoleMain {
    private static final Logger LOG = LoggerFactory.getLogger(SejdaConsoleMain.class);
    /**
     * Executable binary name
     */
    public static final String NAME = "sejda-console";

    public static void main(String[] args) {
        SejdaConsoleMain sejdaConsoleMain = new SejdaConsoleMain();

        wireDependencies(sejdaConsoleMain);

        sejdaConsoleMain.execute(args);
    }

    /**
     * @param sejdaConsoleMain
     */
    private static void wireDependencies(SejdaConsoleMain sejdaConsoleMain) {
        // wire executor service and adapter
        DefaultTaskExecutionAdapter taskExecutionAdapter = new DefaultTaskExecutionAdapter();
        taskExecutionAdapter.setTaskExecutionService(new DefaultTaskExecutionService());
        sejdaConsoleMain.setTaskExecutionAdapter(taskExecutionAdapter);
    }

    private TaskExecutionAdapter taskExecutionAdapter;

    /**
     * Executes the sejda command line interface, using specified parameters as input
     * 
     * @param rawArguments
     *            command line arguments as strings
     */
    public void execute(final String[] rawArguments) {
        LOG.debug("Starting execution with arguments: " + StringUtils.join(rawArguments, " "));
        try {
            final SejdaConsoleArguments arguments = new SejdaConsoleArguments(rawArguments);
            final Cli<GeneralCliArguments> generalCli = CliFactory.createCli(GeneralCliArguments.class);
            final GeneralCliArguments generalCliArguments = generalCli.parseArguments(arguments.getGeneralArguments());

            // no command specified, print general Help
            if (!generalCliArguments.isCommand()) {
                LOG.info(generalCli.getHelpMessage());
                return;
            }

            CliCommand command = generalCliArguments.getCommand().getCommand();

            // print command specific help
            if (generalCliArguments.isHelp()) {
                LOG.info(command.getHelpMessage());
                return;
            }

            // execute command
            getTaskExecutionAdapter().execute(command.parseTaskParameters(arguments.getCommandArguments()));
            LOG.debug("Completed execution");
        } catch (ArgumentValidationException e) {
            LOG.info(e.getMessage());
        } catch (SejdaRuntimeException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    TaskExecutionAdapter getTaskExecutionAdapter() {
        return taskExecutionAdapter;
    }

    void setTaskExecutionAdapter(TaskExecutionAdapter taskExecutionAdapter) {
        this.taskExecutionAdapter = taskExecutionAdapter;
    }

};