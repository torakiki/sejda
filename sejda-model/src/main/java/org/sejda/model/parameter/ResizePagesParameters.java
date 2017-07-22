/*
 * Copyright 2017 by Eduard Weissmann (edi.weissmann@gmail.com).
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

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.validation.Valid;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.common.collection.NullSafeSet;
import org.sejda.model.parameter.base.MultiplePdfSourceMultipleOutputParameters;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.pdf.page.PageRangeSelection;
import org.sejda.model.pdf.page.PagesSelection;
import org.sejda.model.validation.constraint.NotNegative;

/**
 * Parameters for a task to resize pages or add margins. All sizes expected in inches.
 * 
 * @author Eduard Weissmann
 *
 */
public class ResizePagesParameters extends MultiplePdfSourceMultipleOutputParameters implements PageRangeSelection, PagesSelection {
    @NotNegative
    public double margin;

    @NotNegative
    public double pageSizeWidth;

    @NotNegative
    public double aspectRatio;

    @Valid
    private final Set<PageRange> pageSelection = new NullSafeSet<PageRange>();

    /**
     * @return an unmodifiable view of the pageSelection
     */
    @Override
    public Set<PageRange> getPageSelection() {
        return Collections.unmodifiableSet(pageSelection);
    }

    public void addPageRange(PageRange range) {
        pageSelection.add(range);
    }

    public void addAllPageRanges(Collection<PageRange> ranges) {
        pageSelection.addAll(ranges);
    }

    /**
     * @param totalNumberOfPage
     *            the number of pages of the document (upper limit).
     * @return the selected set of pages. Iteration ordering is predictable, it is the order in which elements were inserted into the {@link PageRange} set.
     * @see PagesSelection#getPages(int)
     */
    @Override
    public Set<Integer> getPages(int totalNumberOfPage) {
        if (pageSelection.isEmpty()) {
            return new PageRange(1).getPages(totalNumberOfPage);
        }
        Set<Integer> retSet = new NullSafeSet<Integer>();
        for (PageRange range : getPageSelection()) {
            retSet.addAll(range.getPages(totalNumberOfPage));
        }
        return retSet;
    }

    public double getMargin() {
        return margin;
    }

    public void setMargin(double margin) {
        this.margin = margin;
    }

    public double getPageSizeWidth() {
        return pageSizeWidth;
    }

    /**
     * Set the target page width in inches
     * 
     * @param pageSizeWidth
     */
    public void setPageSizeWidth(double pageSizeWidth) {
        this.pageSizeWidth = pageSizeWidth;
    }

    public double getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(double aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode())
                .append(margin).append(pageSelection).append(pageSizeWidth).append(aspectRatio)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ResizePagesParameters)) {
            return false;
        }
        ResizePagesParameters parameter = (ResizePagesParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(margin, parameter.margin)
                .append(pageSizeWidth, parameter.pageSizeWidth)
                .append(aspectRatio, parameter.aspectRatio)
                .append(pageSelection, parameter.pageSelection).isEquals();
    }
}
