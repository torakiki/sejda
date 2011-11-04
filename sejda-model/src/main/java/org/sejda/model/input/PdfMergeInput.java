/*
 * Created on 10/ago/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.sejda.model.input;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sejda.common.collection.NullSafeSet;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.pdf.page.PageRangeSelection;
import org.sejda.model.pdf.page.PagesSelection;
import org.sejda.model.validation.constraint.NoIntersections;

/**
 * Model for a input source for a merge task. It contains the source and the page selection on the source.
 * 
 * @author Andrea Vacondio
 * 
 */
@NoIntersections
public class PdfMergeInput implements PageRangeSelection, PagesSelection {

    @NotNull
    @Valid
    private final AbstractPdfSource source;
    @Valid
    private final Set<PageRange> pageSelection = new NullSafeSet<PageRange>();

    public PdfMergeInput(AbstractPdfSource source) {
        this.source = source;
    }

    public AbstractPdfSource getSource() {
        return source;
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
        Set<Integer> retSet = new NullSafeSet<Integer>();
        for (PageRange range : getPageSelection()) {
            retSet.addAll(range.getPages(totalNumberOfPage));
        }
        return retSet;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("source", source).append("pageSelection", pageSelection).toString();
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
