/*
 * Created on Jul 1, 2011
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

import org.apache.commons.lang.StringUtils;
import org.sejda.core.exception.SejdaRuntimeException;
import org.sejda.core.manipulation.service.DefaultTaskExecutionService;
import org.sejda.core.manipulation.service.TaskExecutionService;

/**
 * Default implementation of {@link CommandExecutionService}
 * 
 * @author Eduard Weissmann
 * 
 */
public class DefaultCommandExecutionService implements CommandExecutionService {

    private final TaskExecutionService taskExecutionService = new DefaultTaskExecutionService();

    private static final String TRANSFORMER_CLASS_SUFFIX = "CliArgumentsTransformer";

    /**
     * Finds a {@link CommandCliArgumentsTransformer} for the specified command<br/>
     * Uses reflection.<br/>
     * Eg: for "decrypt" command it will return a new instance of a {@link DecryptCliArgumentsTransformer}
     * 
     * @param commandName
     *            name of the command
     * @return
     */
    private CommandCliArgumentsTransformer findTransformer(String commandName) {
        String transformerClazzName = DefaultCommandExecutionService.class.getPackage().getName() + "."
                + StringUtils.capitalize(commandName) + TRANSFORMER_CLASS_SUFFIX;
        CommandCliArgumentsTransformer localTransformer = newInstanceSilently(transformerClazzName);
        if (localTransformer == null) {
            throw new SejdaRuntimeException("No transformer found for command " + commandName + ". Does class "
                    + transformerClazzName + " exist?");
        }
        return localTransformer;
    }

    public void executeCommand(TaskCliArguments taskCliArguments, String commandName) {
        getTaskExecutionService().execute(findTransformer(commandName).toTaskParameters(taskCliArguments));
    }

    TaskExecutionService getTaskExecutionService() {
        return taskExecutionService;
    }

    /**
     * Creates a new instance of the specified class (by name). Silently returns null if an exception occurs
     * 
     * @param <T>
     *            expected return type
     * @param className
     *            fully qualified class name
     * @return an instance of the class specified
     */
    // TODO: EW: Make ReflectionUtils class, or move to ReflectionUtility in sejda-core, or just use dependency injection (guice?)
    @SuppressWarnings("unchecked")
    private static <T> T newInstanceSilently(String className) {
        try {
            return (T) Class.forName(className).newInstance();
        } catch (Exception e) {
            return null;
        }
    }
}
