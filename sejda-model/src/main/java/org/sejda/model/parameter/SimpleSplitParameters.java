/*
 * Created on 28/jul/2011
 *
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sejda.model.pdf.page.PredefinedSetOfPages;
import org.sejda.model.validation.constraint.NotAllowed;

import java.util.Set;

/**
 * Parameter class for a simple split task. Used to perform split types which have a predefined set of pages based on the selected split type.
 *
 * @author Andrea Vacondio
 */
public class SimpleSplitParameters extends AbstractSplitByPageParameters {

    @NotNull
    @NotAllowed(disallow = { PredefinedSetOfPages.NONE })
    private PredefinedSetOfPages setOfPages;

    public SimpleSplitParameters(PredefinedSetOfPages setOfPages) {
        this.setOfPages = setOfPages;
    }

    @Override
    public Set<Integer> getPages(int upperLimit) {
        return setOfPages.getPages(upperLimit);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append(setOfPages).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(setOfPages).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SimpleSplitParameters parameter)) {
            return false;
        }
        return new EqualsBuilder().appendSuper(super.equals(other)).append(setOfPages, parameter.setOfPages).isEquals();
    }

    public PredefinedSetOfPages getSetOfPages() {
        return setOfPages;
    }
}
