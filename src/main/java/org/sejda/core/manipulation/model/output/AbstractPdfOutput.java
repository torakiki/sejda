/*
 * Created on 30/mag/2010
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
package org.sejda.core.manipulation.model.output;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Abstract implementation of a pdf output destination where the results of a manipulation will be written.
 * 
 * @author Andrea Vacondio
 * 
 */
public abstract class AbstractPdfOutput implements Serializable {

    private static final long serialVersionUID = -3597460921220798765L;

    /**
     * @return the type of this output
     */
    public abstract OutputType getOutputType();

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(getOutputType()).toString();
    }
}
