/*
 * Created on 06/giu/2010
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
package org.sejda.core.support.io.model;

import org.sejda.core.support.io.model.Destination.FileDestination;

/**
 * DSL interface to allow the user to set the overwrite flag.
 * 
 * @author Andrea Vacondio
 * 
 */
public interface OverwriteDestination {

    /**
     * set to overwrite or not the output destination if already exists
     * 
     * @param overwrite
     * @return the destination
     */
    Destination overwriting(boolean overwrite);

    /**
     * DSL interface to allow the user to set the overwrite flag for a file destination.
     * 
     * @author Andrea Vacondio
     * 
     */
    public static interface OverwriteFileDestination extends OverwriteDestination {

        /**
         * set to overwrite or not the output file destination if already exists
         * 
         * @param overwrite
         * @return the destination
         */
        FileDestination overwriting(boolean overwrite);
    }
}
