/*
 * Created on 04/giu/2010
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

import org.sejda.core.exception.TaskIOException;

/**
 * DSL interface for a destination writer
 * 
 * @author Andrea Vacondio
 * 
 */
public interface ToDestinationWriter {

    /**
     * sets the destination where the input pdf source will be written.
     * 
     * @param destination
     *            with prefix
     * @throws TaskIOException
     *             in case of error
     */
    void to(DestinationWithPrefix destination) throws TaskIOException;

    /**
     * sets the destination where the input pdf source will be written.
     * 
     * @param destination
     *            without prefix
     * @throws TaskIOException
     *             in case of error
     */
    void to(DestinationWithoutPrefix destination) throws TaskIOException;
}
