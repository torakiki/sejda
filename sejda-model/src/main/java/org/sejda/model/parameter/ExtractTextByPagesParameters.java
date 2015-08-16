/*
 * Copyright 2013 by Edi Weissmann (edi.weissmann@gmail.com).
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

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.validation.Valid;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.common.collection.NullSafeSet;
import org.sejda.model.parameter.base.SinglePdfSourceMultipleOutputParameters;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.pdf.page.PageRangeSelection;
import org.sejda.model.pdf.page.PagesSelection;
import org.sejda.model.validation.constraint.NoIntersections;

/**
 * Parameters for extract text by pages manipulation. Accepts a set of page numbers, where the split occurs. Works similar to SplitByPages, with the difference that the output will
 * be extracted text files.
 * 
 * @author Edi Weissmann
 */
@NoIntersections
public class ExtractTextByPagesParameters extends SinglePdfSourceMultipleOutputParameters implements
        PageRangeSelection, PagesSelection {

    @Valid
    private final Set<PageRange> pageSelection = new NullSafeSet<PageRange>();
    private String textEncoding;

    public String getTextEncoding() {
        return textEncoding;
    }

    public void setTextEncoding(String textEncoding) {
        this.textEncoding = textEncoding;
    }

    /**
     * @return an unmodifiable view of the pageSelection
     */
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
     * @return true if page selection for this input contains all the pages of the input source.
     */
    public boolean isAllPages() {
        return pageSelection.isEmpty();
    }

    /**
     * @param totalNumberOfPage
     *            the number of pages of the document (upper limit).
     * @return the selected set of pages. Iteration ordering is predictable, it is the order in which elements were inserted into the {@link PageRange} set.
     * @see PagesSelection#getPages(int)
     */
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

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(pageSelection).append(textEncoding)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ExtractTextByPagesParameters)) {
            return false;
        }
        ExtractTextByPagesParameters parameter = (ExtractTextByPagesParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(pageSelection, parameter.pageSelection)
                .append(textEncoding, parameter.getTextEncoding()).isEquals();
    }

}
