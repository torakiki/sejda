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

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sejda.core.exception.ConfigurationException;
import org.sejda.core.exception.SejdaRuntimeException;
import org.sejda.core.manipulation.model.parameter.TaskParameters;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.manipulation.registry.DefaultTasksRegistry;
import org.sejda.core.manipulation.registry.TasksRegistry;
import org.sejda.core.notification.strategy.NotificationStrategy;
import org.sejda.core.notification.strategy.SyncNotificationStrategy;

/**
 * Global configuration singleton. Holds a global configuration obtained as the union of the default configuration and, if available, the one submitted by the user as system property or default
 * expected configuration file in the classpath where the user configuration has precedence.
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

    private static final Logger LOG = Logger.getLogger(GlobalConfiguration.class.getPackage().getName());

    private static final String DEFAULT_CONFIG_FILE_NAME = "default-sejda-config.xml";
    private static final String USER_CONFIG_FILE_NAME = "sejda-config.xml";
    private static final String USER_CONFIG_FILE_PROPERTY = "sejda.config.file";

    private static GlobalConfiguration instance;
    private Class<?extends NotificationStrategy> notificationStrategy;
    private TasksRegistry taskRegistry;

    private GlobalConfiguration() {
        initialize();
    }

    private void initialize() {
        taskRegistry = new DefaultTasksRegistry();
        InputStream defaultConfigStream = GlobalConfiguration.class.getClassLoader().getResourceAsStream(DEFAULT_CONFIG_FILE_NAME);
        if(defaultConfigStream == null){
            throw new SejdaRuntimeException(String.format("Unable to find default configuration file %s in classpath.", DEFAULT_CONFIG_FILE_NAME));
        }
        LOG.debug("Loading default Sejda configuration.");
        initializeConfigurationFromStream(defaultConfigStream);
        
        String userConfigFileName = getUserConfigFileName();
        InputStream userConfigStream = getClass().getResourceAsStream(userConfigFileName);
        if(userConfigStream != null){
            LOG.debug("Loading custom user Sejda configuration form "+userConfigFileName);
            initializeConfigurationFromStream(userConfigStream);
        }
    }

    /**
     * initialize the configuration singleton values from the input stream.
     * @param stream
     * @throws SejdaRuntimeException in case of error loading the configuration
     */
    private void initializeConfigurationFromStream(InputStream stream){
        ConfigurationStrategy configStrategy;
        try {
            configStrategy = new XmlConfigurationStrategy(stream);
        } catch (ConfigurationException e) {
            throw new SejdaRuntimeException("Unable to complete Sejda configuration ", e);
        }finally{
            try {
                stream.close();
            } catch (IOException e) {
                LOG.error("Unable to close the stream", e);
            }
        }
        notificationStrategy = configStrategy.getNotificationStrategy();
        Map<Class<? extends TaskParameters>, Class<? extends Task>> userTasks = configStrategy.getTasksMap();
        for(Entry<Class<? extends TaskParameters>, Class<? extends Task>> entry : userTasks.entrySet()){
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
     * @throws SejdaRuntimeException if an error occur during the configuration loading
     */
    public static synchronized GlobalConfiguration getInstance() {
        if (instance == null) {
            instance = new GlobalConfiguration();
        }
        return instance;
    }

    /**
     * @return a new instance of the configured notification strategy
     */
    public synchronized NotificationStrategy getNotificationStrategy() {
            try {
                return notificationStrategy.newInstance();
            } catch (InstantiationException e) {
                LOG.warn("An error occur while instantiating a new NotificationStrategy. Default strategy will be used.", e);
            } catch (IllegalAccessException e) {
                LOG.warn("Unable to access constructor for the configured NotificationStrategy. Default strategy will be used.", e);
            }
            return new SyncNotificationStrategy();
    }

    /**
     * @return the taskRegistry
     */
    public synchronized TasksRegistry getTaskRegistry() {
        return taskRegistry.clone();
    }
 
}
