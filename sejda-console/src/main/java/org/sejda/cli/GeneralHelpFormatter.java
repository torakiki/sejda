/*
 * Created on Oct 3, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
 * 
 * This file is part of the Sejda source code
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.cli;

import java.util.Map;

import org.sejda.cli.command.CliCommand;
import org.sejda.cli.command.CliCommands;
import org.sejda.cli.util.FormattingUtils;

/**
 * Formats the general help for the sejda-console, printing all the available commands, together with their description
 * 
 * @author Eduard Weissmann
 * 
 */
public final class GeneralHelpFormatter {

    private static final char SPACE = ' ';
    private static final String DOUBLE_LINE_BREAK = "\n\n";
    private static final int LINE_WIDTH = 80;
    private final int maximumLengthForCommandDisplayName;
    private final Map<CustomizableProps, String> customs;

    public GeneralHelpFormatter(Map<CustomizableProps, String> customs) {
        this.maximumLengthForCommandDisplayName = getMaxWidthOfCommandDisplayName();
        this.customs = customs;
    }

    public String getFormattedString() {
        StringBuilder helpMessage = new StringBuilder();
        helpMessage.append(customs.get(CustomizableProps.APP_NAME)).append(DOUBLE_LINE_BREAK).append("Basic commands:")
                .append(DOUBLE_LINE_BREAK);

        for (CliCommand each : CliCommands.COMMANDS) {
            helpMessage.append(formatCommandText(each));
        }

        helpMessage.append("Use \"sejda-console <command> -h\" for help regarding a specific command")
                .append(DOUBLE_LINE_BREAK);

        return helpMessage.toString();
    }

    /**
     * @param each
     * @return formatted command text
     */
    private String formatCommandText(CliCommand each) {
        String formattedDescription = formatCommandDescriptionText(each);
        return String.format(" %-" + maximumLengthForCommandDisplayName + "s %s%n%n", each.getDisplayName(),
                formattedDescription);
    }

    private String formatCommandDescriptionText(CliCommand each) {
        return FormattingUtils.leftPadMultiline(
                FormattingUtils.justifyLeft(getDescriptionTextWidth(), each.getDescription()), SPACE,
                maximumLengthForCommandDisplayName + 2);
    }

    private int getDescriptionTextWidth() {
        return LINE_WIDTH - maximumLengthForCommandDisplayName;
    }

    private static int getMaxWidthOfCommandDisplayName() {
        int max = 0;
        for (CliCommand each : CliCommands.COMMANDS) {
            max = Math.max(max, each.getDisplayName().length());
        }

        return max;
    }
}
