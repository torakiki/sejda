/*
 * Created on Jul 8, 2011
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

import org.apache.commons.lang.StringUtils;
import org.sejda.core.exception.SejdaRuntimeException;

import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;
import uk.co.flamingpenguin.jewel.cli.Cli;
import uk.co.flamingpenguin.jewel.cli.CliFactory;

/**
 * Sejda command line interface (cli) wrapper. Could represent the cli for the general options, or a cli for a specific command<br/>
 * Composite of a command line interface and it's (raw) arguments
 * 
 * @author Eduard Weissmann
 * 
 */
public final class SejdaCli<T> {

    private final Cli<T> cli;
    private final String[] arguments;

    /**
     * @param cli
     * @param arguments
     */
    private SejdaCli(Cli<T> cli, String[] arguments) {
        super();
        this.cli = cli;
        this.arguments = arguments.clone();
    }

    /**
     * Create a command line interface specific for the general options
     * 
     * @see GeneralCliArguments
     * 
     * @param arguments
     * @return
     * @throws ArgumentValidationException
     */
    public static SejdaCli<GeneralCliArguments> newGeneralOptionsCli(SejdaConsoleArguments arguments)
            throws ArgumentValidationException {
        final Cli<GeneralCliArguments> generalCli = CliFactory.createCli(GeneralCliArguments.class);
        return new SejdaCli<GeneralCliArguments>(generalCli, arguments.getGeneralOptionsArguments());
    }

    /**
     * Create a command line interface specific for a named command
     * 
     * @param <C>
     * @param arguments
     * @param commandName
     * @return
     * @throws ArgumentValidationException
     */
    public static <C extends TaskCliArguments> SejdaCli<C> newCommandOptions(SejdaConsoleArguments arguments,
            String commandName) throws ArgumentValidationException {
        // find interface defining the options accepted by command
        Class<C> commandCliArgumentsClazz = getCommandCliArgumentsClassByCommandName(commandName);

        // build cli
        Cli<C> commandCli = CliFactory.createCli(commandCliArgumentsClazz);

        return new SejdaCli<C>(commandCli, arguments.getCommandOptionArguments());
    }

    /**
     * @return the options
     * @throws ArgumentValidationException
     */
    public T getParsedArguments() throws ArgumentValidationException {
        return cli.parseArguments(arguments);
    }

    /**
     * @return help string for the command line interface inside
     */
    String getHelpMessage() {
        return cli.getHelpMessage();
    }

    private static final String COMMAND_OPTIONS_CLAZZ_SUFFIX = TaskCliArguments.class.getSimpleName();

    /**
     * Each command has a corresponding interface implementing {@link TaskCliArguments} and configuring specific CLI options<br/>
     * This method retrieves the {@link TaskCliArguments} extending class corresponding to the command name specified<br/>
     * Eg: for command "decrypt" this method will return {@link DecryptTaskCliArguments} class
     * 
     * @param commandName
     *            the command name
     * @return an interface defining the command line options for the specified command
     */
    @SuppressWarnings("unchecked")
    private static <P extends TaskCliArguments> Class<P> getCommandCliArgumentsClassByCommandName(String commandName) {
        try {
            return (Class<P>) Class.forName(TaskCliArguments.class.getPackage().getName() + "."
                    + StringUtils.capitalize(commandName) + COMMAND_OPTIONS_CLAZZ_SUFFIX);
        } catch (Exception e) {
            throw new SejdaRuntimeException("Unknown command: '" + commandName + "'", e);
        }
    }
}
