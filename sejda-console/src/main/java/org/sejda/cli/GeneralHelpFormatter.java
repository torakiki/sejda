/*
 * Created on Oct 3, 2011
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

import org.sejda.cli.transformer.CliCommand;
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

    public GeneralHelpFormatter() {
        this.maximumLengthForCommandDisplayName = getMaxWidthOfCommandDisplayName();
    }

    public String getFormattedString() {
        StringBuilder helpMessage = new StringBuilder();
        helpMessage.append("Sejda Console").append(DOUBLE_LINE_BREAK).append("Basic commands:")
                .append(DOUBLE_LINE_BREAK);

        for (CliCommand each : CliCommand.sortedValues()) {
            helpMessage.append(formatCommandText(each));
        }

        helpMessage.append("Use \"sejda-console <command> -h\" for help regarding a specific command").append(
                DOUBLE_LINE_BREAK);

        return helpMessage.toString();
    }

    /**
     * @param each
     * @param formattedDescription
     * @return
     */
    private String formatCommandText(CliCommand each) {
        String formattedDescription = formatCommandDescriptionText(each);
        return String.format(" %-" + maximumLengthForCommandDisplayName + "s %s\n\n", each.getDisplayName(),
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
        for (CliCommand each : CliCommand.values()) {
            max = Math.max(max, each.getDisplayName().length());
        }

        return max;
    }
}
