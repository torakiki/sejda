package org.sejda.cli;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.sejda.cli.command.CliCommand;
import org.sejda.cli.command.CliCommands;
import org.sejda.cli.command.StandardCliCommand;
import org.sejda.cli.util.CommandLineUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the command line arguments passed to the {@link SejdaConsole}
 * 
 * @author Eduard Weissmann
 * 
 */
class RawArguments {

    private String[] arguments;

    private static final Logger LOG = LoggerFactory.getLogger(RawArguments.class);

    /**
     * @param arguments
     *            string representation of the command line arguments
     */
    public RawArguments(String[] arguments) {
        super();
        this.arguments = arguments.clone();
        // support for Windows long command lines https://support.microsoft.com/en-us/kb/830473
        loadArgumentsFromFileIfRequired();
    }

    public boolean isHelpRequest() {
        return ArrayUtils.contains(this.arguments, "-h") || ArrayUtils.contains(this.arguments, "--help") || ArrayUtils.contains(this.arguments, "help");
    }

    public boolean isVersionRequest() {
        return ArrayUtils.contains(this.arguments, "--version");
    }

    public boolean isLicenseRequest() {
        return ArrayUtils.contains(this.arguments, "--license");
    }

    /**
     * @return only arguments relevant for the {@link org.sejda.cli.model.TaskCliArguments}
     */
    String[] getCommandArguments() {
        return ArrayUtils.subarray(arguments, 1, arguments.length);
    }

    @Override
    public String toString() {
        return StringUtils.join(arguments, " ");
    }

    /**
     * @return {@link StandardCliCommand} specified or null if no known command was specified (first or second supplied argument can be matched to a known command)
     */
    public CliCommand getCliCommand() {
        if (arguments.length >= 1 && CliCommands.findByDisplayNameSilently(arguments[0]) != null) {
            return CliCommands.findByDisplayNameSilently(arguments[0]);
        }

        if (arguments.length >= 2 && CliCommands.findByDisplayNameSilently(arguments[1]) != null) {
            return CliCommands.findByDisplayNameSilently(arguments[1]);
        }
        return null;
    }

    /**
     * @return true if no known command was specified
     */
    public boolean isNoCommandSpecified() {
        return getCliCommand() == null;
    }

    public boolean isCommandSpecified() {
        return !isNoCommandSpecified();
    }

    public boolean isEmptyCommandArguments() {
        return isCommandSpecified() && getCommandArguments().length == 0;
    }

    private void loadArgumentsFromFileIfRequired() {
        // Read args from file to overcome https://support.microsoft.com/en-us/kb/830473
        if(this.arguments.length == 1 && this.arguments[0].endsWith("args.txt")) {
            try {
                String argsAsString = FileUtils.readFileToString(new File(this.arguments[0]), Charset.defaultCharset());
                this.arguments = CommandLineUtils.translateCommandline(argsAsString);
                LOG.info("Read arguments from file");
            } catch (IOException e) {
                LOG.warn("Could not read arguments from file", e);
            }
        }
    }
}