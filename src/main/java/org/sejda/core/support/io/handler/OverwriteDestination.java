/*
 * Created on 06/giu/2010
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
package org.sejda.core.support.io.handler;

import org.sejda.core.support.io.handler.Destination.FileDestination;

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
