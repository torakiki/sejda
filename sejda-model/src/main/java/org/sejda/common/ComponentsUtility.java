/*
 * Created on 15/set/2011
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
package org.sejda.common;

import java.io.Closeable;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides general utility methods to tasks components.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class ComponentsUtility {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentsUtility.class);

    private ComponentsUtility() {
        // hide
    }

    /**
     * closes the {@link Closeable} component if it is not null logging exceptions.
     * 
     * @param closeable
     */
    public static void nullSafeCloseQuietly(Closeable closeable) {
        try {
            nullSafeClose(closeable);
        } catch (Exception e) {
            LOG.warn("An error occurred closing the component.", e);
        }
    }

    /**
     * closes the {@link Closeable} component if it is not null.
     * 
     * @param closeable
     * @throws IOException
     */
    public static void nullSafeClose(Closeable closeable) throws IOException {
        if (closeable != null) {
            closeable.close();
        }
    }
}
