/*
 * Copyright 2016 by Eduard Weissmann (edi.weissmann@gmail.com).
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

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.nup.PageOrder;
import org.sejda.model.parameter.base.MultiplePdfSourceMultipleOutputParameters;

/**
 * N-up task
 *
 * In printing, 2-up, 3-up, or more generally N-up refers to a page layout strategy in which multiple pre-rendered pages are composed onto a single page; achieved by reduction in
 * size, possible rotations, and subsequent arrangement in a grid pattern. The primary purpose of N-up printing is to reduce the number of pages that a printed work would otherwise
 * require without having to re-edit, index, or flow the layout of the individual pages of an existing work.
 *
 * https://en.wikipedia.org/wiki/N-up
 */
public class NupParameters extends MultiplePdfSourceMultipleOutputParameters {
    @Min(2)
    private final int n;
    @NotNull
    private final PageOrder pageOrder;

    public NupParameters(int n) {
        this(n, PageOrder.HORIZONTAL);
    }

    public NupParameters(int n, PageOrder pageOrder) {
        this.n = n;
        this.pageOrder = pageOrder;
    }

    public int getN() {
        return n;
    }

    public PageOrder getPageOrder() {
        return pageOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        NupParameters that = (NupParameters) o;

        return new EqualsBuilder().appendSuper(super.equals(o)).append(n, that.n).append(pageOrder, that.pageOrder)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(n).append(pageOrder).toHashCode();
    }
}
