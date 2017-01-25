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

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Available commands for the command line
 * 
 * @author Andrea Vacondio
 */
public class CliCommands {

    /**
     * Sorted list of available commands
     */
    public static final List<CliCommand> COMMANDS;

    static {
        COMMANDS = stream(ServiceLoader.load(CommandsProvider.class).spliterator(), false)
                .flatMap(p -> p.commands().stream()).sorted(Comparator.comparing(c -> c.getDisplayName()))
                .collect(toList());
    }

    public static CliCommand findByDisplayNameSilently(String displayName) {
        return COMMANDS.stream().filter(c -> c.getDisplayName().equalsIgnoreCase(displayName)).findFirst().orElse(null);
    }

}
