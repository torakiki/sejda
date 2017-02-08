/*
 * Created on 13/ott/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.sejda.core.Sejda;
import org.sejda.model.exception.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a stream where the xml configuration can be read.
 * <p>
 * <ul>
 * <li>An xml configuration file path can be provided using the system property <b>sejda.config.file</b>. If provided it searches for the in the classpath and in the filesystem.
 * </li>
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
    private static final String DEFAULT_CONFIG_FILE_NAME = "sejda.default.xml";

    @Override
    public InputStream getConfigurationStream() throws ConfigurationException {
        InputStream configurationStream = getConfiguration();
        if (configurationStream == null) {
            throw new ConfigurationException("Unable to find xml configuration file.");
        }
        return configurationStream;
    }

    private InputStream getConfiguration() throws ConfigurationException {
        String userConfigFileName = System.getProperty(Sejda.USER_CONFIG_FILE_PROPERTY_NAME);
        if (isNotBlank(userConfigFileName)) {
            return getCustomConfigurationStream(userConfigFileName);
        }
        return getDefaultConfigurationStream();
    }

    private InputStream getCustomConfigurationStream(String userConfigFileName) throws ConfigurationException {
        LOG.trace("Loading Sejda configuration form {}", userConfigFileName);
        InputStream retVal = ClassLoader.getSystemResourceAsStream(userConfigFileName);
        if (retVal == null) {
            try {
                LOG.trace("Searching Sejda configuration on filesystem");
                return new FileInputStream(userConfigFileName);
            } catch (FileNotFoundException e) {
                throw new ConfigurationException(
                        String.format("Unable to access the provided configuration file [%s]", userConfigFileName), e);
            }
        }
        return retVal;
    }

    private InputStream getDefaultConfigurationStream() {
        LOG.trace("Loading Sejda configuration form {}", USER_CONFIG_FILE_NAME);
        InputStream result = Thread.currentThread().getContextClassLoader().getResourceAsStream(USER_CONFIG_FILE_NAME);
        if (result == null) {
            LOG.trace("Couldn't find {}, loading Sejda configuration form default {}", USER_CONFIG_FILE_NAME,
                    DEFAULT_CONFIG_FILE_NAME);
            result = Thread.currentThread().getContextClassLoader().getResourceAsStream(DEFAULT_CONFIG_FILE_NAME);
        }
        return result;
    }
}
