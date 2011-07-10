package org.sejda.cli;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Represents the command line arguments passed to the {@link SejdaConsoleMain}
 * 
 * @author Eduard Weissmann
 * 
 */
class SejdaConsoleArguments {
    private final String[] arguments;

    /**
     * @param arguments
     *            string representation of the command line arguments
     */
    public SejdaConsoleArguments(String[] arguments) {
        super();
        this.arguments = arguments.clone();
    }

    public boolean isHelpRequest() {
        return (this.arguments.length >= 1 && StringUtils.equalsIgnoreCase("-h", arguments[0]))
                || this.arguments.length == 0;
    }

    /**
     * @return only arguments relevant for the {@link GeneralCliArguments}
     * @see GeneralCliArguments to understand the distinction between {@link GeneralCliArguments} and {@link TaskCliArguments}
     */
    String[] getGeneralOptionsArguments() {
        int min = Math.min(isHelpRequest() ? 2 : 1, arguments.length);
        return (String[]) ArrayUtils.subarray(arguments, 0, min);
    }

    /**
     * @return only arguments relevant for the {@link TaskCliArguments}
     * @see TaskCliArguments to understand the distinction between {@link GeneralCliArguments} and {@link TaskCliArguments}
     */
    String[] getCommandOptionArguments() {
        return (String[]) ArrayUtils.subarray(arguments, 1, arguments.length);
    }

}