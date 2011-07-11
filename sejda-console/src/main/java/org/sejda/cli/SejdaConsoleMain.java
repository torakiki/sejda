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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;

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
        new SejdaConsoleMain().execute(args);
    }

    /**
     * Executes the sejda command line interface, using specified parameters as input
     * 
     * @param args
     *            command line arguments as strings
     */
    public void execute(final String[] args) {
        LOG.debug("Starting execution with arguments: " + StringUtils.join(args, " "));
        try {
            final SejdaConsoleArguments arguments = new SejdaConsoleArguments(args);
            final SejdaCli<GeneralCliArguments> generalCli = SejdaCli.newGeneralOptionsCli(arguments);

            // no command specified, print general Help
            if (!generalCli.getParsedArguments().isCommand()) {
                LOG.info(generalCli.getHelpMessage());
                return;
            }

            String commandName = generalCli.getParsedArguments().getCommand();
            SejdaCli<? extends TaskCliArguments> commandCli = SejdaCli.newCommandOptions(arguments, commandName);

            // print command specific help
            if (generalCli.getParsedArguments().isHelp()) {
                LOG.info(commandCli.getHelpMessage());
                return;
            }

            // execute command
            getTaskExecutionFacade().executeCommand(commandCli.getParsedArguments(), commandName);
            LOG.debug("Completed execution");
        } catch (ArgumentValidationException e) {
            LOG.info(e.getMessage());
        } catch (SejdaRuntimeException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private final CommandExecutionService taskExecutionFacade = new DefaultCommandExecutionService();

    CommandExecutionService getTaskExecutionFacade() {
        return taskExecutionFacade;
    }
};