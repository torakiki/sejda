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
import org.sejda.core.support.io.model.PopulatedFileOutput;

/**
 * DSL interface to expose methods a multiple output task needs (tasks generating multiple files as output).
 * 
 * <pre>
 * {@code
 * multipleOutputs().add(file(tmpFile).name("newName"));
 * ....
 * AbstractPdfOutput output = ...
 * boolean overwrite = ...
 * multipleOutputs().flushOutputs(output, overwrite);
 * }
 * </pre>
 * 
 * @author Andrea Vacondio
 * 
 */
public interface MultipleOutputSupport {

    /**
     * flush of the multiple outputs added to the output destination. Once flushed they are deleted and the collection emptied.
     * 
     * @param output
     *            manipulation output parameter where multiple outputs will be written.
     * @param overwrite
     *            true if the output should be overwritten if already exists
     * @throws TaskIOException
     *             in case of error
     */
    void flushOutputs(AbstractPdfOutput output, boolean overwrite) throws TaskIOException;

    /**
     * Adds the given file output (typically a temporary file) to the collection of multiple outputs ready to be flushed.
     * 
     * @param fileOutput
     */
    void addOutput(PopulatedFileOutput fileOutput);
}
