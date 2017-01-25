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

import org.sejda.model.parameter.base.TaskParameters;

/**
 * Defines a command line command
 * 
 * @author Andrea Vacondio
 *
 */
public interface CliCommand {
    /**
     * @return the user friendly name
     */
    String getDisplayName();

    /**
     * @return task description, explaining what the task does in a nutshell
     */
    String getDescription();

    /**
     * @return extended task description, explaining what the task does in detail, providing an example
     */
    String getExampleUsage();

    /**
     * @return help message, detailing purpose, usage and parameter valid values
     */
    String getHelpMessage();

    /**
     * @param rawArguments
     * @return task parameters out of the raw string arguments passed as input (removing the command argument for example)
     */
    TaskParameters parseTaskParameters(String[] rawArguments);

    /**
     * @return the argument class for this command
     */
    Class<?> getCliArgumentsClass();
}
