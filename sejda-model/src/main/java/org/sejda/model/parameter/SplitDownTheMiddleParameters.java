/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com).
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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.parameter.base.MultiplePdfSourceMultipleOutputParameters;
import org.sejda.model.repaginate.Repagination;

public class SplitDownTheMiddleParameters extends MultiplePdfSourceMultipleOutputParameters {

    private Repagination repagination = Repagination.NONE;

    public Repagination getRepagination() {
        return repagination;
    }

    public void setRepagination(Repagination repagination) {
        this.repagination = repagination;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getRepagination())
                .appendSuper(super.hashCode()).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SplitDownTheMiddleParameters)) {
            return false;
        }
        return new EqualsBuilder()
                .append(getRepagination(), ((SplitDownTheMiddleParameters) other).getRepagination())
                .appendSuper(super.equals(other))
                .isEquals();
    }
}
