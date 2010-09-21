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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.sejda.core.manipulation.model.rotation.PageRotation;

/**
 * Parameter class for the rotation manipulation. Accepts a list of {@link org.sejda.core.manipulation.model.input.PdfSource} where the {@link PageRotation} will be applied.
 * 
 * @author Andrea Vacondio
 * 
 */
public class RotateParameters extends PdfSourceListParameters {

    private static final long serialVersionUID = 8834767589689764537L;

    private String outputPrefix = "";
    @Valid
    @NotNull
    private PageRotation rotation = null;

    public String getOutputPrefix() {
        return outputPrefix;
    }

    public void setOutputPrefix(String outputPrefix) {
        this.outputPrefix = outputPrefix;
    }

    public PageRotation getRotation() {
        return rotation;
    }

    public void setRotation(PageRotation rotation) {
        this.rotation = rotation;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(outputPrefix).append(rotation).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof RotateParameters)) {
            return false;
        }
        RotateParameters parameter = (RotateParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(outputPrefix, parameter.getOutputPrefix())
                .append(rotation, parameter.getRotation()).isEquals();
    }
}
