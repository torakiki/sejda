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

import org.sejda.core.manipulation.model.output.AbstractOutput;
import org.sejda.core.manipulation.model.output.PdfFileOutput;
import org.sejda.core.manipulation.model.output.PdfStreamOutput;

/**
 * Concrete implementation of an output destination where an input pdf source will be written to.
 * <br />
 * Usage:
 * <p>
 * <code>destination(out).withPrefix("prefix");</code>
 * </p>
 * 
 * @author Andrea Vacondio
 * @see OutputHandler
 * 
 */
public final class OutputDestination implements DestinationWithPrefix {

    private AbstractOutput output;
    private String prefix;

    private OutputDestination(AbstractOutput output) {
        this.output = output;
    }

    /**
     * Entry point to create a destination with prefix capabilities
     * 
     * @param output
     *            where the input source will be written.
     * @return the destination
     */
    public static DestinationWithPrefix destination(PdfFileOutput output) {
        return new OutputDestination(output);
    }

    /**
     * Entry point to create a destination without prefix capabilities
     * 
     * @param output
     *            where the input source will be written.
     * @return the destination
     */
    public static DestinationWithoutPrefix destination(PdfStreamOutput output) {
        return new OutputDestination(output);
    }

    public DestinationWithPrefix withPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public AbstractOutput getOutputDestination() {
        return output;
    }

    public String getPrefix() {
        return prefix;
    }

}
