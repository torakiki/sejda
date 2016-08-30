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
import org.sejda.common.collection.NullSafeSet;
import org.sejda.model.parameter.base.MultiplePdfSourceMultipleOutputParameters;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.repaginate.Repagination;

import javax.validation.Valid;
import java.util.Set;

public class SplitDownTheMiddleParameters extends MultiplePdfSourceMultipleOutputParameters implements ExcludedPagesSelection {

    private Repagination repagination = Repagination.NONE;

    @Valid
    public final Set<PageRange> excludedPagesSelection = new NullSafeSet<PageRange>();

    public Repagination getRepagination() {
        return repagination;
    }

    public void setRepagination(Repagination repagination) {
        this.repagination = repagination;
    }

    public Set<PageRange> getExcludedPagesSelection() {
        return excludedPagesSelection;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getRepagination())
                .append(getExcludedPagesSelection())
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
                .append(getExcludedPagesSelection(), ((SplitDownTheMiddleParameters) other).getExcludedPagesSelection())
                .appendSuper(super.equals(other))
                .isEquals();
    }
}
