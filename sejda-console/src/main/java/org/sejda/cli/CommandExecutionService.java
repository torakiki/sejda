/*
 * Created on Jul 4, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
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


/**
 * Service that executes command line commands, translating {@link CommandCliArguments} to {@link org.sejda.core.manipulation.model.parameter.TaskParameters}, and executing the
 * corresponding {@link org.sejda.core.manipulation.model.task.Task}
 * 
 * @author Eduard Weissmann
 * 
 */
public interface CommandExecutionService {

    /**
     * Executes a given command, using the specified options
     * 
     * @param commandCliArguments
     *            command line options (translated to arguments)
     * @param commandName
     *            name of the command
     */
    void executeCommand(CommandCliArguments commandCliArguments, String commandName);
}
