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

import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.common.collection.NullSafeSet;
import org.sejda.model.parameter.base.MultiplePdfSourceMultipleOutputParameters;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.repaginate.Repagination;
import org.sejda.model.split.SplitDownTheMiddleMode;

public class SplitDownTheMiddleParameters extends MultiplePdfSourceMultipleOutputParameters
        implements ExcludedPagesSelection {

    @NotNull
    private Repagination repagination = Repagination.NONE;
    @Valid
    public final Set<PageRange> excludedPagesSelection = new NullSafeSet<>();
    // Defaults to Auto, meaning the pages will be split horizontally if in portrait mode or vertically in landscape mode
    // Allows user to override this and force horizontal or vertical split
    @NotNull
    private SplitDownTheMiddleMode mode = SplitDownTheMiddleMode.AUTO;

    // Ratio is left part's width / right part's width or top part's height / bottom part's height
    // Defaults to equal sized parts, meaning a ratio of 1.
    // Allows user to override the ratio and split in unequal parts.
    private double ratio = 1.0d;

    public Repagination getRepagination() {
        return repagination;
    }

    public void setRepagination(Repagination repagination) {
        this.repagination = repagination;
    }

    @Override
    public Set<PageRange> getExcludedPagesSelection() {
        return excludedPagesSelection;
    }

    public SplitDownTheMiddleMode getMode() {
        return mode;
    }

    public void setMode(SplitDownTheMiddleMode mode) {
        this.mode = mode;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getRepagination()).append(getExcludedPagesSelection()).append(getRatio())
                .append(getMode()).appendSuper(super.hashCode()).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SplitDownTheMiddleParameters)) {
            return false;
        }
        SplitDownTheMiddleParameters parameter = (SplitDownTheMiddleParameters) other;
        return new EqualsBuilder().append(getRepagination(), parameter.getRepagination())
                .append(getExcludedPagesSelection(), parameter.getExcludedPagesSelection())
                .append(getRatio(), parameter.getRatio()).append(getMode(), parameter.getMode())
                .appendSuper(super.equals(other)).isEquals();
    }
}
