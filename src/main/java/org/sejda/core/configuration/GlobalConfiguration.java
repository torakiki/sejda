/*
 * Created on 01/mag/2010
 * Copyright (C) 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.sejda.core.configuration;

import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.sejda.core.Sejda;
import org.sejda.core.exception.ConfigurationException;
import org.sejda.core.exception.SejdaRuntimeException;
import org.sejda.core.manipulation.model.parameter.TaskParameters;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.manipulation.registry.DefaultTasksRegistry;
import org.sejda.core.manipulation.registry.TasksRegistry;
import org.sejda.core.notification.strategy.NotificationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Global configuration singleton. Holds a global configuration obtained as the union of the default configuration and, if available, the one submitted by the user as system
 * property or default expected configuration file in the classpath where the user configuration has precedence.
 * <p>
 * A user can submit a custom configuration including a file name "sejda-config.xml" in the classpath or using the system property sejda.config.file where the value of the property
 * is the name of the configuration file available in the classpath. If Both are specified then system property has precedence.
 * </p>
 * 
 * @author Andrea Vacondio
 * 
 */
@SuppressWarnings("unchecked")
public final class GlobalConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalConfiguration.class);

    private static final String DEFAULT_CONFIG_FILE_NAME = "default-sejda-config.xml";
    private static final String USER_CONFIG_FILE_NAME = "sejda-config.xml";
    private static final String USER_CONFIG_FILE_PROPERTY = "sejda.config.file";

    private Class<? extends NotificationStrategy> notificationStrategy;
    private TasksRegistry taskRegistry;
    private boolean validation;

    private GlobalConfiguration() {
        LOG.info("Configuring Sejda {}", Sejda.VERSION);
        initialize();
    }

    private void initialize() {
        taskRegistry = new DefaultTasksRegistry();
        InputStream defaultConfigStream = GlobalConfiguration.class.getClassLoader().getResourceAsStream(
                DEFAULT_CONFIG_FILE_NAME);
        if (defaultConfigStream == null) {
            throw new SejdaRuntimeException(String.format("Unable to find default configuration file %s in classpath.",
                    DEFAULT_CONFIG_FILE_NAME));
        }
        LOG.debug("Loading default Sejda configuration.");
        initializeConfigurationFromStream(defaultConfigStream);

        String userConfigFileName = getUserConfigFileName();
        InputStream userConfigStream = getClass().getResourceAsStream(userConfigFileName);
        if (userConfigStream != null) {
            LOG.debug("Loading custom user Sejda configuration form " + userConfigFileName);
            initializeConfigurationFromStream(userConfigStream);
        }
    }

    /**
     * initialize the configuration singleton values from the input stream.
     * 
     * @param stream
     * @throws SejdaRuntimeException
     *             in case of error loading the configuration
     */
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
     * 
     * @return file name of the configuration file submitted by the user
     */
    private String getUserConfigFileName() {
        String userConfigFileName = System.getProperty(USER_CONFIG_FILE_PROPERTY);
        if (StringUtils.isEmpty(userConfigFileName)) {
            userConfigFileName = USER_CONFIG_FILE_NAME;
        }
        return userConfigFileName;
    }

    /**
     * @return the global configuration instance
     * @throws SejdaRuntimeException
     *             if an error occur during the configuration loading
     */
    public static GlobalConfiguration getInstance() {
        return GlobalConfigurationHolder.CONFIGURATION;
    }

    /**
     * @return the configured {@link NotificationStrategy}
     */
    public Class<? extends NotificationStrategy> getNotificationStrategy() {
        return notificationStrategy;
    }

    /**
     * @return the taskRegistry
     */
    public TasksRegistry getTaskRegistry() {
        return taskRegistry;
    }

    /**
     * @return true if validation should be performed or false if incoming parameters instances are already validate externally.
     */
    public boolean isValidation() {
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
