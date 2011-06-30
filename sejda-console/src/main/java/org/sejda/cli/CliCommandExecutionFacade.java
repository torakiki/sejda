/*
 * Created on Jul 1, 2011
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import org.sejda.core.manipulation.model.parameter.DecryptParameters;
import org.sejda.core.manipulation.model.parameter.EncryptParameters;
import org.sejda.core.manipulation.model.parameter.TaskParameters;
import org.sejda.core.manipulation.service.DefaultTaskExecutionService;
import org.sejda.core.manipulation.service.TaskExecutionService;

/**
 * @author Eduard Weissmann
 * 
 */
public class CliCommandExecutionFacade {

    private final TaskExecutionService taskExecutionService = new DefaultTaskExecutionService();

    public <T extends CommandOptions> void executeCommand(T commandOptions) {
        TaskParameters taskParameters = toTaskParameters(commandOptions);
        taskExecutionService.execute(taskParameters);
    }

    DecryptParameters toTaskParameters(DecryptCommandOptions commandOptions) {
        DecryptParameters params = new DecryptParameters();
        params.setCompress(commandOptions.isCompressed());
        return params;
    }

    EncryptParameters toTaskParameters(EncryptCommandOptions commandOptions) {
        return commandOptions != null ? null : null;
    }

    <T extends CommandOptions> TaskParameters toTaskParameters(T commandOptions) {
        throw new RuntimeException("Can't translate command options " + commandOptions.getClass().getSimpleName()
                + " into task parameters");
    }
}
