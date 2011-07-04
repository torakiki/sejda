package org.sejda.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static void main(String[] cliArgs) {
        new SejdaConsole().execute(cliArgs);
    }

    public void execute(String[] cliArgs) {
        try {
            List<String> args = Arrays.asList(cliArgs);

            Cli<GeneralOptions> generalOptionsCli = CliFactory.createCli(GeneralOptions.class);
            final GeneralOptions generalOptions = generalOptionsCli.parseArguments(extractGeneralArguments(args));

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
            getTaskExecutionFacade().executeCommand(commandOptionsCli.parseArguments(extractCommandArguments(args)),
                    commandName);

            println("Successfully executed command <" + commandName + ">");
        } catch (ArgumentValidationException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private final TaskExecutionFacade taskExecutionFacade = new DefaultTaskExecutionFacade();

    TaskExecutionFacade getTaskExecutionFacade() {
        return taskExecutionFacade;
    }

    /**
     * @param args
     * @return
     */
    private static String[] extractGeneralArguments(List<String> args) {
        if (args.size() > 1 && StringUtils.equalsIgnoreCase("-h", args.get(0))) {
            return new String[] { args.get(0), args.get(1) };
        }

        if (args.size() > 0) {
            return new String[] { args.get(0) };
        }

        return new String[] {};
    }

    private static String[] extractCommandArguments(List<String> args) {
        List<String> argsCopy = new ArrayList<String>(args);
        if (argsCopy.size() > 0) {
            argsCopy.remove(0);
        }
        String[] result = new String[argsCopy.size()];
        return argsCopy.toArray(result);
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
