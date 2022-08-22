/*
 * Created on 11/giu/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.parameter;

import jakarta.validation.constraints.Min;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sejda.commons.collection.NullSafeSet;

import java.util.Set;

/**
 * Parameter class for a split by every X pages task. Used to perform split where an input pdf document is divided into documents of X pages.
 * 
 * @author Andrea Vacondio
 * 
 */
public class SplitByEveryXPagesParameters extends AbstractSplitByPageParameters {

    @Min(value = 1)
    private int step = 1;

    public SplitByEveryXPagesParameters(int step) {
        this.step = step;
    }

    @Override
    public Set<Integer> getPages(int upperLimit) {
        Set<Integer> pages = new NullSafeSet<Integer>();
        for (int i = step; i <= upperLimit; i += step) {
            pages.add(i);
        }
        return pages;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append(step).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(step).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SplitByEveryXPagesParameters)) {
            return false;
        }
        SplitByEveryXPagesParameters parameter = (SplitByEveryXPagesParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(step, parameter.step).isEquals();
    }
}
