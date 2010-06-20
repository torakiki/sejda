/*
 * Created on 19/giu/2010
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
import org.sejda.core.manipulation.model.output.AbstractPdfOutput;

/**
 * DSL interface to expose methods a multiple output task needs (tasks generating multiple files as output).
 * 
 * @author Andrea Vacondio
 * 
 */
public interface MultipleOutputSupport {

    /**
     * flush of the multiple outputs added to the output destination
     * 
     * @param output
     *            manipulation output parameter
     * @param overwrite
     *            true if the output should be overwritten if already exists
     * @throws TaskIOException
     *             in case of error
     */
    void flushOutputs(AbstractPdfOutput output, boolean overwrite) throws TaskIOException;

    /**
     * Adds the given file output to the multiple outputs collection
     * 
     * @param fileOutput
     */
    void add(PopulatedFileOutput fileOutput);
}
