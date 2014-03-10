/*
 * Created on 31/mag/2010
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

    public static final String UNETHICAL_READ_PROPERTY_NAME = "org.sejda.unethical.read";

    private Sejda() {
        // on purpose
    }

    public static final String VERSION = new SejdaVersionLoader().getSejdaVersion();
    public static final String CREATOR = "Sejda (Ver. " + VERSION + ")";

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
