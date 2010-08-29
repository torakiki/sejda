/*
 * Created on 29/ago/2010
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
import org.sejda.core.manipulation.model.output.OutputType;
import org.sejda.core.support.io.model.PopulatedFileOutput;

/**
 * Provides support methods for the tasks to handle write output. Can hold multiple output temporary files created by a task and write them to the destination when the task
 * requires to flush. An extending class can call the {@link MultipleOutputSupport} interface methods to add temporary files or flush them.
 * 
 * <pre>
 * {@code
 * addOutput(file(tmpFile).name("newName"));
 * ....
 * AbstractPdfOutput output = ...
 * boolean overwrite = ...
 * flushOutputs(output, overwrite);
 * }
 * </pre>
 * 
 * 
 * @author Andrea Vacondio
 * 
 */
public class MultipleOutputWriterSupport extends OutputWriterSupport implements MultipleOutputSupport {

    public void flushOutputs(AbstractPdfOutput output, boolean overwrite) throws TaskIOException {
        try {
            if (OutputType.FILE_OUTPUT.equals(output.getOutputType())) {
                throw new TaskIOException("Unsupported file ouput for a multiple output task.");
            } else {
                writeToNonFileDestination(output, overwrite);
            }
        } finally {
            clear();
        }
    }

    public void addOutput(PopulatedFileOutput fileOutput) {
        add(fileOutput);
    }
}
