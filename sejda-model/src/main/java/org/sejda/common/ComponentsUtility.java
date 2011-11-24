/*
 * Created on 15/set/2011
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
            LOG.warn("An error occurred closing the document handler.", e);
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
