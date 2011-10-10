package org.sejda.cli;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

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
        return (this.arguments.length >= 1 && StringUtils.equalsIgnoreCase("-h", arguments[0]))
                || this.arguments.length == 0;
    }

    /**
     * @return only arguments relevant for the {@link org.sejda.cli.GeneralCliArguments}
     * @see org.sejda.cli.GeneralCliArguments to understand the distinction between {@link org.sejda.cli.GeneralCliArguments} and
     *      {@link org.sejda.cli.model.TaskCliArguments}
     */
    String[] getGeneralArguments() {
        int min = Math.min(isHelpRequest() ? 2 : 1, arguments.length);
        return (String[]) ArrayUtils.subarray(arguments, 0, min);
    }

    /**
     * @return only arguments relevant for the {@link org.sejda.cli.model.TaskCliArguments}
     * @see org.sejda.cli.model.TaskCliArguments to understand the distinction between {@link org.sejda.cli.GeneralCliArguments} and
     *      {@link org.sejda.cli.model.TaskCliArguments}
     */
    String[] getCommandArguments() {
        return (String[]) ArrayUtils.subarray(arguments, 1, arguments.length);
    }

    @Override
    public String toString() {
        return StringUtils.join(arguments, " ");
    }

}