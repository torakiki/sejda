/*
 * Created on 31/mag/2010
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
package org.sejda.core;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Global constants
 * 
 * @author Andrea Vacondio
 * 
 */
public final class Sejda {
    private static final Logger LOG = LoggerFactory.getLogger(Sejda.class);

    public static final String UNETHICAL_READ_PROPERTY_NAME = "sejda.unethical.read";
    public static final String USER_CONFIG_FILE_PROPERTY_NAME = "sejda.config.file";
    public static final String PERFORM_SCHEMA_VALIDATION_PROPERTY_NAME = "sejda.perform.schema.validation";

    private Sejda() {
        // on purpose
    }

    public static final String VERSION = new SejdaVersionLoader().getSejdaVersion();
    public static String CREATOR = "Sejda " + VERSION + " (www.sejda.org)";

    /**
     * Loader for the sejda properties.
     * 
     * @author Andrea Vacondio
     * 
     */
    private static final class SejdaVersionLoader {

        private static final String SEJDA_PROPERTIES = "/sejda.properties";
        private String sejdaVersion = "";

        private SejdaVersionLoader() {
            Properties props = new Properties();
            try {
                props.load(SejdaVersionLoader.class.getResourceAsStream(SEJDA_PROPERTIES));
                sejdaVersion = props.getProperty("sejda.version", "UNKNOWN");

            } catch (IOException e) {
                LOG.warn("Unable to determine version of Sejda.", e);
            }
        }

        String getSejdaVersion() {
            return sejdaVersion;
        }

    }
}
