package org.sejda.cli;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.sejda.cli.transformer.CliCommand;

/**
 * Represents the command line arguments passed to the {@link SejdaConsole}
 * 
 * @author Eduard Weissmann
 * 
 */
class RawArguments {

    private final String[] arguments;

    /**
     * @param arguments
     *            string representation of the command line arguments
     */
    public RawArguments(String[] arguments) {
        super();
        this.arguments = arguments.clone();
    }

    public boolean isHelpRequest() {
        return ArrayUtils.contains(this.arguments, "-h") || ArrayUtils.contains(this.arguments, "--help");
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
     * @return {@link CliCommand} specified or null if no known command was specified (first or second supplied argument can be matched to a known command)
     */
    public CliCommand getCliCommand() {
        if (arguments.length >= 1 && CliCommand.findByDisplayNameSilently(arguments[0]) != null) {
            return CliCommand.findByDisplayNameSilently(arguments[0]);
        }

        if (arguments.length >= 2 && CliCommand.findByDisplayNameSilently(arguments[1]) != null) {
            return CliCommand.findByDisplayNameSilently(arguments[1]);
        }
        return null;
    }

    /**
     * @return true if no known command was specified
     */
    public boolean isNoCommandSpecified() {
        return getCliCommand() == null;
    }
}