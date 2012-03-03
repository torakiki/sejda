/*
 * Created on 13/ott/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.sejda.model.exception.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a stream where the xml configuration can be read.
 * <p>
 * <ul>
 * <li>An xml configuration file path can be provided using the system property <b>sejda.config.file</b>. If provided it searches for the in the classpath and in the filesystem.</li>
 * <li>If the system property <b>sejda.config.file</b> is NOT provided it searches for the standard configuration file named<b> sejda.xml </b></li>
 * </ul>
 * </p>
 * 
 * @author Andrea Vacondio
 * 
 */
class XmlConfigurationStreamProvider implements ConfigurationStreamProvider {

    private static final Logger LOG = LoggerFactory.getLogger(XmlConfigurationStreamProvider.class);

    private static final String USER_CONFIG_FILE_NAME = "sejda.xml";
    private static final String USER_CONFIG_FILE_PROPERTY = "sejda.config.file";

    public InputStream getConfigurationStream() throws ConfigurationException {
        InputStream configurationStream = getConfiguration();
        if (configurationStream == null) {
            throw new ConfigurationException("Unable to find xml configuration file.");
        }
        return configurationStream;
    }

    private InputStream getConfiguration() throws ConfigurationException {
        String userConfigFileName = System.getProperty(USER_CONFIG_FILE_PROPERTY);
        if (isNotBlank(userConfigFileName)) {
            return getCustomConfigurationStream(userConfigFileName);
        }
        return getDefaultConfigurationStream();
    }

    private InputStream getCustomConfigurationStream(String userConfigFileName) throws ConfigurationException {
        LOG.debug("Loading Sejda configuration form {}", userConfigFileName);
        InputStream retVal = ClassLoader.getSystemResourceAsStream(userConfigFileName);
        if (retVal == null) {
            try {
                LOG.debug("Searching Sejda configuration on filesystem");
                return new FileInputStream(userConfigFileName);
            } catch (FileNotFoundException e) {
                throw new ConfigurationException(String.format("Unable to access the provided configuration file [%s]",
                        userConfigFileName), e);
            }
        }
        return retVal;
    }

    private InputStream getDefaultConfigurationStream() {
        LOG.debug("Loading Sejda configuration form default {}", USER_CONFIG_FILE_NAME);
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(USER_CONFIG_FILE_NAME);
    }
}
