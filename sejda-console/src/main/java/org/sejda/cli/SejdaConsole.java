package org.sejda.cli;

import org.apache.commons.lang.StringUtils;

import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;
import uk.co.flamingpenguin.jewel.cli.Cli;
import uk.co.flamingpenguin.jewel.cli.CliFactory;

/**
 * Main entry point for the sejda console interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class SejdaConsole {

    public static void main(String[] args) {
        try {
            Cli<GeneralOptions> generalOptionsCli = CliFactory.createCli(GeneralOptions.class);
            final GeneralOptions generalOptions = generalOptionsCli.parseArguments(args);

            // no command specified, print general Help
            if (!generalOptions.isCommand()) {
                println(generalOptionsCli.getHelpMessage());
                return;
            }

            // command specified
            String commandName = generalOptions.getCommand();
            Class<? extends CommandOptions> commandOptionsClazz = classForNameSilently(StringUtils
                    .capitalize(commandName) + "CommandOptions");
            if (commandOptionsClazz == null) {
                println("Unknown command: " + commandName);
                return;
            }

            Cli<? extends CommandOptions> commandOptionsCli = CliFactory.createCli(commandOptionsClazz);

            // print command Help
            if (generalOptions.isHelp()) {
                println(commandOptionsCli.getHelpMessage());
                return;
            }

            // execute command
            println("Executing command <" + commandName + ">");
            new CliCommandExecutionFacade().toTaskParameters(commandOptionsCli.parseArguments(args));
        } catch (ArgumentValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    private static Class classForNameSilently(String shortName) {
        try {
            return Class.forName("org.sejda.cli." + shortName);
        } catch (Exception e) {
            return null;
        }
    }

    private static void println(String line) {
        System.out.println(line);
    }
}
