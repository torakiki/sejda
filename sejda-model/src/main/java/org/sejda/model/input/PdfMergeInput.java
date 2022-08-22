/*
 * Created on 10/ago/2011
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
package org.sejda.model.input;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sejda.commons.collection.NullSafeSet;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.pdf.page.PageRangeSelection;
import org.sejda.model.pdf.page.PagesSelection;
import org.sejda.model.validation.constraint.NoIntersections;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Model for a input source for a merge task. It contains the source and the page selection on the source.
 * 
 * @author Andrea Vacondio
 * 
 */
@NoIntersections
public class PdfMergeInput implements PageRangeSelection, PagesSelection, MergeInput {

    @NotNull
    @Valid
    private PdfSource<?> source;
    @Valid
    private final Set<PageRange> pageSelection = new NullSafeSet<>();

    public PdfMergeInput(PdfSource<?> source, Set<PageRange> pageSelection) {
        this.source = source;
        this.pageSelection.addAll(pageSelection);
    }

    public PdfMergeInput(PdfSource<?> source) {
        this.source = source;
    }

    public PdfSource<?> getSource() {
        return source;
    }

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
    @Override
    public Set<Integer> getPages(int totalNumberOfPage) {
        Set<Integer> retSet = new NullSafeSet<Integer>();
        if (isAllPages()) {
            for (int i = 1; i <= totalNumberOfPage; i++) {
                retSet.add(i);
            }
        } else {
            for (PageRange range : getPageSelection()) {
                retSet.addAll(range.getPages(totalNumberOfPage));
            }
        }
        return retSet;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(source).append(pageSelection).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(source).append(pageSelection).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PdfMergeInput)) {
            return false;
        }
        PdfMergeInput input = (PdfMergeInput) other;
        return new EqualsBuilder().append(source, input.getSource()).append(pageSelection, input.pageSelection)
                .isEquals();
    }

}
