/*
 * Created on Jun 30, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.cli.model;

import com.lexicalscope.jewel.cli.Option;

/**
 * Base interface for specifications of the command line interface for {@link org.sejda.model.task.Task}s
 * 
 * @author Eduard Weissmann
 * 
 */
public interface TaskCliArguments {
    String EXECUTABLE_NAME = "sejda-console";

    @Option(shortName = "h", description = "prints usage information. Can be used to detail options for a command '-h command' (optional)")
    boolean isHelp();

    @Option(description = "executes the task in 'lenient' mode trying to overcome errors, at the expense of data integrity (optional)")
    boolean isLenient();
}
