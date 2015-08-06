/*
 * Created on 03/ago/2011
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.model.parameter;

import javax.validation.constraints.Min;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.parameter.base.SinglePdfSourceMultipleOutputParameters;

/**
 * Parameter class for a split by size task.
 * 
 * @author Andrea Vacondio
 * 
 */
public class SplitBySizeParameters extends SinglePdfSourceMultipleOutputParameters {

    @Min(1)
    private long sizeToSplitAt;

    public SplitBySizeParameters(long sizeToSplitAt) {
        this.sizeToSplitAt = sizeToSplitAt;
    }

    public long getSizeToSplitAt() {
        return sizeToSplitAt;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(sizeToSplitAt).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SplitBySizeParameters)) {
            return false;
        }
        SplitBySizeParameters parameter = (SplitBySizeParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(sizeToSplitAt, parameter.getSizeToSplitAt())
                .isEquals();
    }
}
