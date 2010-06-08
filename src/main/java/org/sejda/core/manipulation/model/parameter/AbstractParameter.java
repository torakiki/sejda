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
package org.sejda.core.manipulation.model.parameter;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.sejda.core.manipulation.model.TaskParameters;
import org.sejda.core.manipulation.model.pdf.PdfVersion;

/**
 * Abstract parameter implementation with attributes commonly used by all the parameters implementation
 * 
 * @author Andrea Vacondio
 * 
 */
public abstract class AbstractParameter implements Serializable, TaskParameters {

    private static final long serialVersionUID = -6100370016710146349L;

    private boolean overwrite = false;
    private boolean compress = false;
    private PdfVersion version = PdfVersion.VERSION_1_2;

    public boolean isOverwrite() {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    public boolean isCompress() {
        return compress;
    }

    public void setCompress(boolean compress) {
        this.compress = compress;
    }

    public PdfVersion getVersion() {
        return version;
    }

    public void setVersion(PdfVersion version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(overwrite).append(compress).append(version).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(overwrite).append(compress).append(version).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof AbstractParameter)) {
            return false;
        }
        AbstractParameter parameter = (AbstractParameter) other;
        return new EqualsBuilder().append(overwrite, parameter.isOverwrite()).append(compress, parameter.isCompress())
                .append(version, parameter.getVersion()).isEquals();
    }
}
