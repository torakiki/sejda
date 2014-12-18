/*
 * Created on Aug 1, 2011
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
