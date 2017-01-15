/*
 * Created on 14 gen 2017
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.cli.command;

import org.sejda.cli.model.TaskCliArguments;
import org.sejda.model.parameter.base.TaskParameters;

/**
 * Base implementation for a command line command
 * 
 * @author Andrea Vacondio
 */
class BaseCliCommand implements CliCommand {

    private String displayName;
    private String description;
    private String exampleUsage;
    private CliInterfacedTask<? extends TaskCliArguments, ? extends TaskParameters> cliInterfacedTask;

    public BaseCliCommand(String displayName,
            CliInterfacedTask<? extends TaskCliArguments, ? extends TaskParameters> cliTask, String description,
            String exampleUsage) {
        this.displayName = displayName;
        this.exampleUsage = exampleUsage;
        this.cliInterfacedTask = cliTask;
        this.description = description;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getExampleUsage() {
        return exampleUsage;
    }

    /**
     * @param rawArguments
     * @return task parameters out of the raw string arguments passed as input (removing the command argument for example)
     */
    @Override
    public TaskParameters parseTaskParameters(String[] rawArguments) {
        return cliInterfacedTask.getTaskParameters(rawArguments);
    }

    /**
     * @return help message, detailing purpose, usage and parameter valid values
     */
    @Override
    public String getHelpMessage() {
        StringBuilder result = new StringBuilder();

        result.append(getDescription());
        result.append("\n\n");

        result.append("Example usage: ").append(TaskCliArguments.EXECUTABLE_NAME).append(" ").append(getExampleUsage());
        result.append("\n\n");

        result.append(cliInterfacedTask.createCli().getHelpMessage());

        return result.toString();
    }

    @Override
    public Class<?> getCliArgumentsClass() {
        return cliInterfacedTask.getCliArgumentsClass();
    }
}
