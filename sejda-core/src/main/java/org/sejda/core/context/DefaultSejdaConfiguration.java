/*
 * Created on 01/mag/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.core.context;

import org.sejda.core.Sejda;
import org.sejda.core.notification.strategy.NotificationStrategy;
import org.sejda.model.exception.ConfigurationException;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskNotFoundException;
import org.sejda.model.parameter.base.TaskParameters;
import org.sejda.model.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

/**
 * Default configuration singleton.
 * <p>
 * A user can submit a configuration including a file named "sejda.xml" in the classpath or using the system property sejda.config.file where the value of the property is the name
 * of the configuration file available in the classpath. If Both are specified then system property has precedence.
 * </p>
 *
 * @author Andrea Vacondio
 */
public final class DefaultSejdaConfiguration implements SejdaConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultSejdaConfiguration.class);

    private Class<? extends NotificationStrategy> notificationStrategy;
    private final TasksRegistry tasksRegistry = new DefaultTasksRegistry();
    ;
    private boolean validation;
    private boolean ignoreXmlConfiguration;

    //package access for tests
    DefaultSejdaConfiguration() {
        LOG.info("Configuring Sejda {}", Sejda.VERSION);
        initialize();
        if (LOG.isTraceEnabled()) {
            LOG.trace("Configured tasks:");
            tasksRegistry.getTasks().forEach((p, t) -> LOG.trace(String.format("%s executed by -> %s", p, t)));
        }
    }

    private void initialize() {
        ConfigurationStrategy configStrategy;
        try {
            configStrategy = XmlConfigurationStrategy.newInstance(new XmlConfigurationStreamProvider());
        } catch (ConfigurationException e) {
            throw new SejdaRuntimeException("Unable to complete Sejda configuration ", e);
        }
        notificationStrategy = configStrategy.getNotificationStrategy();
        LOG.trace("Notification strategy: {}", notificationStrategy);
        validation = configStrategy.isValidation();
        LOG.trace("Validation: {}", validation);
        ignoreXmlConfiguration = configStrategy.isIgnoreXmlConfiguration();
        LOG.trace("Validation, ignore xml configuration: {}", ignoreXmlConfiguration);
        configStrategy.getTasksMap().forEach(tasksRegistry::addTask);
    }

    /**
     * @return the global configuration instance
     * @throws SejdaRuntimeException if an error occur during the configuration loading
     */
    public static SejdaConfiguration getInstance() {
        return DefaultSejdaConfigurationHolder.CONFIGURATION;
    }

    @Override
    public Class<? extends NotificationStrategy> getNotificationStrategy() {
        return notificationStrategy;
    }

    @Override
    public Task<? extends TaskParameters> getTask(TaskParameters parameters) throws TaskException {
        var parametersClass = parameters.getClass();
        var taskClass = Optional.ofNullable(tasksRegistry.getTask(parametersClass)).orElseThrow(
                () -> new TaskNotFoundException(
                        String.format("Unable to find a Task class able to execute %s", parametersClass)));
        try {
            return taskClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
                 InvocationTargetException | SecurityException e) {
            throw new TaskException("Error instantiating the task", e);
        } catch (NoSuchMethodException e) {
            throw new TaskException(String.format("The task %s doesn't define a public no-args contructor.", taskClass),
                    e);
        }
    }

    @Override
    public boolean isValidation() {
        return validation;
    }

    @Override
    public boolean isValidationIgnoringXmlConfiguration() {
        return ignoreXmlConfiguration;
    }

    TasksRegistry getTasksRegistry() {
        return tasksRegistry;
    }

    /**
     * Lazy initialization holder class idiom (Joshua Bloch, Effective Java second edition, item 71).
     *
     * @author Andrea Vacondio
     */
    private static final class DefaultSejdaConfigurationHolder {

        private DefaultSejdaConfigurationHolder() {
            // hide constructor
        }

        static final SejdaConfiguration CONFIGURATION = new DefaultSejdaConfiguration();
    }
}
