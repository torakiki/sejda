/*
 * Created on Aug 1, 2011
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.cli;

import org.sejda.core.service.DefaultTaskExecutionService;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * Main entry point for the sejda console executable
 * 
 * @author Eduard Weissmann
 * 
 */
public final class Main {

    private Main() {
        // don't instantiate
    }

    public static void main(String[] args) {
        // bridging between jul and slf4j
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        new SejdaConsole(args, getTaskExecutionAdapter()).execute();
    }

    private static TaskExecutionAdapter getTaskExecutionAdapter() {
        return new DefaultTaskExecutionAdapter(new DefaultTaskExecutionService());
    }

}
