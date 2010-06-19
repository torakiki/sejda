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
package org.sejda.core.support.io.handler;

import org.sejda.core.manipulation.model.output.AbstractOutput;
import org.sejda.core.manipulation.model.output.PdfDirectoryOutput;
import org.sejda.core.manipulation.model.output.PdfFileOutput;
import org.sejda.core.manipulation.model.output.PdfStreamOutput;
import org.sejda.core.support.io.handler.Destination.FileDestination;
import org.sejda.core.support.io.handler.OverwriteDestination.OverwriteFileDestination;

/**
 * Concrete implementation of an output destination where an input pdf source will be written to. <br />
 * Usage:
 * 
 * <pre>
 * {@code
 * PdfFileOutput out = ...
 * destination(out).overwriting(false);
 * }
 * </pre>
 * 
 * Or:
 * 
 * <pre>
 * {@code
 * PdfStreamOutput out = ...
 * destination(out);
 * }
 * </pre>
 * 
 * @author Andrea Vacondio
 * @see OutputHandler
 * 
 */
public final class OutputDestination implements OverwriteFileDestination, FileDestination {

    private AbstractOutput outputDestination;
    private boolean overwrite = false;

    private OutputDestination(AbstractOutput outputDestination) {
        this.outputDestination = outputDestination;
    }

    /**
     * Entry point to create a file destination that can be overwritten
     * 
     * @param output
     *            where the input source will be written.
     * @return the destination
     */
    public static OverwriteFileDestination destination(PdfFileOutput output) {
        return new OutputDestination(output);
    }

    /**
     * Entry point to create a directory destination that can be overwritten
     * 
     * @param output
     *            where the input source will be written.
     * @return the destination
     */
    public static OverwriteDestination destination(PdfDirectoryOutput output) {
        return new OutputDestination(output);
    }

    /**
     * Entry point to create a destination that cannot be overwritten
     * 
     * @param output
     *            where the input source will be written.
     * @return the destination
     */
    public static Destination destination(PdfStreamOutput output) {
        return new OutputDestination(output);
    }

    public AbstractOutput getOutputDestination() {
        return outputDestination;
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public FileDestination overwriting(boolean overwrite) {
        this.overwrite = overwrite;
        return this;
    }

}
