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
package org.sejda.core.support.io;

/**
 * DSL interface to allow the user to set the overwrite flag.
 * 
 * @author Andrea Vacondio
 * 
 */
public interface OverwriteDestination {

    /**
     * 
     * @return true if the output destination should be overwritten
     */
    boolean isOverwrite();

    /**
     * set to overwrite the output destination if already exists
     * 
     * @param overwrite
     * @return the destination writer
     */
    ToDestinationWriter overwriting(boolean overwrite);
}
