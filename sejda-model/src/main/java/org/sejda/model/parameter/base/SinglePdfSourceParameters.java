/*
 * Created on 17/set/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * This file is part of the Sejda source code
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.model.parameter.base;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.input.PdfSource;

/**
 * Base parameters class for manipulations with a single {@link PdfSource}
 * 
 * @author Andrea Vacondio
 * 
 */
abstract class SinglePdfSourceParameters extends AbstractPdfOutputParameters implements SinglePdfSourceTaskParameters {

    @Valid
    @NotNull
    private PdfSource<?> source;

    @Override
    public PdfSource<?> getSource() {
        return source;
    }

    @Override
    public void setSource(PdfSource<?> source) {
        this.source = source;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(source).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SinglePdfSourceParameters parameter)) {
            return false;
        }
        return new EqualsBuilder().appendSuper(super.equals(other)).append(source, parameter.getSource()).isEquals();
    }

}
