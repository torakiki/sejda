/*
 * Created on 17/set/2010
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
import org.sejda.core.manipulation.model.input.PdfSource;

/**
 * Base parameters class for manipulations with a single {@link PdfSource}
 * 
 * @author Andrea Vacondio
 * 
 */
public class SinglePdfSourceParameters extends AbstractParameters {

    private static final long serialVersionUID = 5169993669392616684L;

    @Valid
    @NotNull
    private PdfSource source;

    public PdfSource getSource() {
        return source;
    }

    public void setSource(PdfSource source) {
        this.source = source;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(source).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof SinglePdfSourceParameters)) {
            return false;
        }
        SinglePdfSourceParameters parameter = (SinglePdfSourceParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(source, parameter.getSource()).isEquals();
    }

}
