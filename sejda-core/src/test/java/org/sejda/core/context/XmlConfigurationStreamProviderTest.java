/*
 * Created on 13/ott/2011
 * Copyright 2011 Sober Lemur S.r.l. and Sejda BV.
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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.sejda.model.exception.ConfigurationException;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrea Vacondio
 */
@Isolated
public class XmlConfigurationStreamProviderTest {

    @AfterEach
    public void tearDown() {
        System.setProperty("sejda.config.file", EMPTY);
    }

    @Test
    public void testCustomConfiguration() throws ConfigurationException {
        System.setProperty("sejda.config.file", "custom-sejda.xml");
        ConfigurationStreamProvider provider = new XmlConfigurationStreamProvider();
        assertNotNull(provider.getConfigurationStream());
    }

    @Test
    public void testNotExistingCustomConfiguration() {
        System.setProperty("sejda.config.file", "not-existing-sejda.xml");
        ConfigurationStreamProvider provider = new XmlConfigurationStreamProvider();
        assertThrows(ConfigurationException.class, provider::getConfigurationStream);
    }
}
