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
 * DSL interface to expose methods a single output task needs (tasks generating a single file as output).
 * 
 * <pre>
 * {@code
 * PopulatedFileOutput singleOut = file(tmpFile).name("newName");
 * AbstractPdfOutput output = ...
 * boolean overwrite = ...
 * singleOutput().flushSingleOutput(singleOut, output, overwrite);
 * }
 * </pre>
 * 
 * @author Andrea Vacondio
 * 
 */
public interface SingleOutputSupport {

    /**
     * flush of the given {@link PopulatedFileOutput} to the output destination and deletes it.
     * 
     * @param fileOutput
     *            the input file that will be written to the destination.
     * @param output
     *            the destination.
     * @param overwrite
     *            if true overwrite the destination of already exists.
     * @throws TaskIOException
     *             in case of error
     */
    void flushSingleOutput(PopulatedFileOutput fileOutput, AbstractPdfOutput output, boolean overwrite)
            throws TaskIOException;

}
