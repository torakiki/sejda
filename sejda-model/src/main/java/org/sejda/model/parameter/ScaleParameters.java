/*
 * Created on 15 nov 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.model.parameter;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.parameter.base.MultiplePdfSourceMultipleOutputParameters;
import org.sejda.model.scale.ScaleType;
import org.sejda.model.validation.constraint.Positive;

/**
 * Parameters for a task to scale pages or pages content
 * 
 * @author Andrea Vacondio
 *
 */
public class ScaleParameters extends MultiplePdfSourceMultipleOutputParameters {
    @Positive
    public final double scale;
    @NotNull
    private ScaleType scaleType = ScaleType.CONTENT;

    public ScaleParameters(double scale) {
        this.scale = scale;
    }

    public ScaleType getScaleType() {
        return scaleType;
    }

    public void setScaleType(ScaleType scaleType) {
        this.scaleType = scaleType;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(scale).append(scaleType).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ScaleParameters)) {
            return false;
        }
        ScaleParameters parameter = (ScaleParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(scale, parameter.scale)
                .append(scaleType, parameter.scaleType).isEquals();
    }
}
