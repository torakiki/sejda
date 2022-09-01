/*
 * Created on 01 ago 2017
 * Copyright 2017 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.scale;

import jakarta.validation.constraints.PositiveOrZero;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Margins in inches to be added to a PDF document
 * 
 * @author Andrea Vacondio
 */
public class Margins {
    @PositiveOrZero
    public final double top;
    @PositiveOrZero
    public final double right;
    @PositiveOrZero
    public final double bottom;
    @PositiveOrZero
    public final double left;

    public Margins(double top, double right, double bottom, double left) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    /**
     * @param inches
     * @return the value in points
     */
    public static double inchesToPoints(double inches) {
        return 72 * inches;
    }

    public static double pointsToInches(double points) {
        return points / 72;
    }

    public Margins rotate() {
        return new Margins(left, top, right, bottom);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("top", top).append("right", right)
                .append("bottom", bottom).append("left", left).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(top).append(right).append(bottom).append(left).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Margins instance)) {
            return false;
        }
        return new EqualsBuilder().append(top, instance.top).append(right, instance.right)
                .append(bottom, instance.bottom).append(left, instance.left).isEquals();
    }
}
