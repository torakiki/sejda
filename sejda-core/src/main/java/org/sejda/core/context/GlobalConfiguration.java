/*
 * Created on 01/mag/2010
 *
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
package org.sejda.core.context;

import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.sejda.core.Sejda;
import org.sejda.core.exception.ConfigurationException;
import org.sejda.core.exception.SejdaRuntimeException;
import org.sejda.core.manipulation.model.parameter.TaskParameters;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.notification.strategy.NotificationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Global configuration singleton.
 * <p>
 * A user can submit a configuration including a file named "sejda.xml" in the classpath or using the system property sejda.config.file where the value of the property is the name
 * of the configuration file available in the classpath. If Both are specified then system property has precedence.
 * </p>
 * 
 * @author Andrea Vacondio
 * 
 */
final class GlobalConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalConfiguration.class);

    private static final String USER_CONFIG_FILE_NAME = "sejda.xml";
    private static final String USER_CONFIG_FILE_PROPERTY = "sejda.config.file";

    private Class<? extends NotificationStrategy> notificationStrategy;
    private TasksRegistry taskRegistry;
    private boolean validation;

    private GlobalConfiguration() {
        LOG.info("Configuring Sejda {}", Sejda.VERSION);
        initialize();
        if (LOG.isDebugEnabled()) {
            logConfiguredTasks();
        }
    }

    private void logConfiguredTasks() {
        LOG.debug("Configured tasks:");
        for (@SuppressWarnings("rawtypes")
        Entry<Class<? extends TaskParameters>, Class<? extends Task>> entry : taskRegistry.getTasks().entrySet()) {
            LOG.debug(String.format("%s executed by -> %s", entry.getKey(), entry.getValue()));
        }
    }

    private void initialize() {
        taskRegistry = new DefaultTasksRegistry();
        String userConfigFileName = System.getProperty(USER_CONFIG_FILE_PROPERTY, USER_CONFIG_FILE_NAME);
        InputStream userConfigStream = ClassLoader.getSystemResourceAsStream(userConfigFileName);
        if (userConfigStream != null) {
            LOG.debug("Loading Sejda configuration form " + userConfigFileName);
            initializeConfigurationFromStream(userConfigStream);
        } else {
            throw new SejdaRuntimeException(String.format("Unable to find configuration file [%s] in classpath.",
                    userConfigFileName));
        }
    }

    /**
     * initialize the configuration values from the input stream.
     * 
     * @param stream
     * @throws SejdaRuntimeException
     *             in case of error loading the configuration
     */
    @SuppressWarnings("rawtypes")
    private void initializeConfigurationFromStream(InputStream stream) {
        ConfigurationStrategy configStrategy;
        try {
            configStrategy = new XmlConfigurationStrategy(stream);
        } catch (ConfigurationException e) {
            throw new SejdaRuntimeException("Unable to complete Sejda configuration ", e);
        } finally {
            IOUtils.closeQuietly(stream);
        }
        notificationStrategy = configStrategy.getNotificationStrategy();
        validation = configStrategy.isValidation();
        Map<Class<? extends TaskParameters>, Class<? extends Task>> userTasks = configStrategy.getTasksMap();
        for (Entry<Class<? extends TaskParameters>, Class<? extends Task>> entry : userTasks.entrySet()) {
            taskRegistry.addTask(entry.getKey(), entry.getValue());
        }
    }

    /**
     * @return the global configuration instance
     * @throws SejdaRuntimeException
     *             if an error occur during the configuration loading
     */
    static GlobalConfiguration getInstance() {
        return GlobalConfigurationHolder.CONFIGURATION;
    }

    /**
     * @return the configured {@link NotificationStrategy}
     */
    Class<? extends NotificationStrategy> getNotificationStrategy() {
        return notificationStrategy;
    }

    /**
     * @return the taskRegistry
     */
    TasksRegistry getTaskRegistry() {
        return taskRegistry;
    }

    /**
     * @return true if validation should be performed or false if incoming parameters instances are already validate externally.
     */
    boolean isValidation() {
        return validation;
    }

    /**
     * Lazy initialization holder class idiom (Joshua Bloch, Effective Java second edition, item 71).
     * 
     * @author Andrea Vacondio
     * 
     */
    private static final class GlobalConfigurationHolder {

        private GlobalConfigurationHolder() {
            // hide constructor
        }

        static final GlobalConfiguration CONFIGURATION = new GlobalConfiguration();
    }
}
