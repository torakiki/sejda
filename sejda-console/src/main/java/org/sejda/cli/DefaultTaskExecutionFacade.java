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

import org.apache.commons.lang.StringUtils;
import org.sejda.core.exception.SejdaRuntimeException;
import org.sejda.core.manipulation.service.DefaultTaskExecutionService;
import org.sejda.core.manipulation.service.TaskExecutionService;
import org.sejda.core.support.util.ReflectionUtility;

/**
 * @author Eduard Weissmann
 * 
 */
public class DefaultTaskExecutionFacade implements TaskExecutionFacade {

    private final TaskExecutionService taskExecutionService = new DefaultTaskExecutionService();

    /**
     * @param commandName
     * @return
     */
    private static CommandOptionsTransformer createTransformer(String commandName) {
        String transformerClazzName = DefaultTaskExecutionFacade.class.getPackage().getName() + "."
                + StringUtils.capitalize(commandName) + "OptionsTransformer";
        CommandOptionsTransformer localTransformer = ReflectionUtility.newInstanceSilently(transformerClazzName);
        if (localTransformer == null) {
            throw new SejdaRuntimeException("No transformer found for command " + commandName + ". Does class "
                    + transformerClazzName + " exist?");
        }
        return localTransformer;
    }

    @Override
    public void executeCommand(CommandOptions commandOptions, String commandName) {
        getTaskExecutionService().execute(createTransformer(commandName).toParameters(commandOptions));
    }

    TaskExecutionService getTaskExecutionService() {
        return taskExecutionService;
    }
}
