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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.sejda.cli.exception.ArgumentValidationException;
import org.sejda.cli.transformer.CliCommand;
import org.sejda.core.Sejda;
import org.sejda.core.exception.SejdaRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sejda command line service. Responsible for interpreting the arguments, displaying help requests or delegating execution of commands
 * 
 * @author Eduard Weissmann
 * 
 */
// TODO: fix javadoc warnings
public class SejdaConsole {
    private static final Logger LOG = LoggerFactory.getLogger(SejdaConsole.class);

    private final RawArguments arguments;

    private final TaskExecutionAdapter taskExecutionAdapter;

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

    private void doExecute() {
        LOG.debug("Starting execution with arguments: '" + arguments + "'");

        if (isNoCommandSpecified()) {
            if (isVersionRequest() || isLicenseRequest()) {
                printVersionAndLicense();
            } else {
                printGeneralHelp();
            }
        } else {
            CliCommand command = getCommandSpecified();

            if (isCommandHelpRequested()) {
                printCommandHelp(command);
            } else {
                validateNoDuplicateCommandArguments();
                executeCommand(command);
            }
        }

        LOG.debug("Completed execution");
    }

    /**
     * throws an exception if there are duplicate option:value pairs specified, that would override each other silently otherwise
     * 
     */
    private void validateNoDuplicateCommandArguments() {
        Map<String, Object> uniqueArguments = new HashMap<String, Object>();
        for (final String eachArgument : arguments.getCommandArguments()) {
            if (uniqueArguments.containsKey(eachArgument) && StringUtils.startsWith(eachArgument, "-")) {
                throw new ArgumentValidationException(
                        "Option '"
                                + eachArgument
                                + "' is specified twice. Please note that the correct way to specify a list of values for an option is to repeat the values after the option, without re-stating the option name. Example: --files /tmp/file1.pdf /tmp/files2.pdf");
            }
            uniqueArguments.put(eachArgument, eachArgument);
        }

    }

    private void executeCommand(CliCommand command) {
        getTaskExecutionAdapter().execute(command.parseTaskParameters(arguments.getCommandArguments()));
    }

    private void printCommandHelp(CliCommand command) {
        LOG.info(command.getHelpMessage());
    }

    private boolean isCommandHelpRequested() {
        return arguments.isHelpRequest();
    }

    private CliCommand getCommandSpecified() {
        return arguments.getCliCommand();
    }

    private void printGeneralHelp() {
        LOG.info(new GeneralHelpFormatter().getFormattedString());
    }

    private void printVersionAndLicense() {
        StringBuilder info = new StringBuilder(String.format("\nSejda Console (Version %s)\n", Sejda.VERSION));
        info.append("(see http://www.sejda.org for more information)\n\n");
        info.append("Copyright 2011 by Andrea Vacondio, Eduard Weissmann and others.\n" + "\n"
                + "Licensed under the Apache License, Version 2.0 (the \"License\");\n"
                + "you may not use this file except in compliance with the License.\n"
                + "You may obtain a copy of the License at \n" + "\n" + "http://www.apache.org/licenses/LICENSE-2.0\n"
                + "\n" + "Unless required by applicable law or agreed to in writing, software\n"
                + "distributed under the License is distributed on an \"AS IS\" BASIS,\n"
                + "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n"
                + "See the License for the specific language governing permissions and \n"
                + "limitations under the License. ");
        LOG.info(info.toString());
    }

    private boolean isNoCommandSpecified() {
        return arguments.isNoCommandSpecified();
    }

    private boolean isVersionRequest() {
        return arguments.isVersionRequest();
    }

    private boolean isLicenseRequest() {
        return arguments.isLicenseRequest();
    }

    TaskExecutionAdapter getTaskExecutionAdapter() {
        return taskExecutionAdapter;
    }
}