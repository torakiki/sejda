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

import java.lang.reflect.ParameterizedType;

import org.sejda.cli.model.TaskCliArguments;
import org.sejda.cli.transformer.CommandCliArgumentsTransformer;
import org.sejda.model.parameter.base.TaskParameters;

import com.lexicalscope.jewel.cli.Cli;
import com.lexicalscope.jewel.cli.CliFactory;

/**
 * @author Andrea Vacondio
 *
 */
public class BaseCliCommand implements CliCommand {

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

/**
 * Base class defining the contract for {@link org.sejda.model.task.Task}s with a cli interface
 * 
 * @author Eduard Weissmann
 * 
 * @param <T>
 * @param <P>
 */
abstract class CliInterfacedTask<T extends TaskCliArguments, P extends TaskParameters> {

    @SuppressWarnings("unchecked")
    protected Class<T> getCliArgumentsClass() {
        // returning T.class see http://www.artima.com/weblogs/viewpost.jsp?thread=208860
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<T>) parameterizedType.getActualTypeArguments()[0];
    }

    protected abstract CommandCliArgumentsTransformer<T, P> getArgumentsTransformer();

    protected Cli<T> createCli() {
        return CliFactory.createCli(getCliArgumentsClass());
    }

    protected P getTaskParameters(String[] rawArguments) {
        try {
            T cliArguments = createCli().parseArguments(rawArguments);
            return getArgumentsTransformer().toTaskParameters(cliArguments);
        } catch (com.lexicalscope.jewel.cli.ArgumentValidationException e) {
            throw new org.sejda.cli.exception.ArgumentValidationException(e.getMessage(), e);
        }
    }
}
