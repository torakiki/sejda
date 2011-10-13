/*
 * Created on 25/ago/2011
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
package org.sejda.core.manipulation.model.parameter;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.validation.Valid;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.core.manipulation.model.parameter.base.SinglePdfSourceSingleOutputParameters;
import org.sejda.core.manipulation.model.pdf.page.PageRange;
import org.sejda.core.manipulation.model.pdf.page.PageRangeSelection;
import org.sejda.core.manipulation.model.pdf.page.PagesSelection;
import org.sejda.core.manipulation.model.pdf.page.PredefinedSetOfPages;
import org.sejda.core.support.NullSafeSet;
import org.sejda.core.validation.constraint.HasSelectedPages;
import org.sejda.core.validation.constraint.NoIntersections;
import org.sejda.core.validation.constraint.SingleOutputAllowedExtensions;

/**
 * Parameter class for an Extract pages task. Allow to specify a predefined set of pages to extract (odd, even) or a set of page ranges but not both. Page ranges are validated to
 * make sure that there is no intersection.
 * 
 * @author Andrea Vacondio
 * 
 */
@NoIntersections
@SingleOutputAllowedExtensions
@HasSelectedPages
public class ExtractPagesParameters extends SinglePdfSourceSingleOutputParameters implements PageRangeSelection,
        PagesSelection {

    private PredefinedSetOfPages predefinedSetOfPages;
    @Valid
    private final Set<PageRange> pageSelection = new NullSafeSet<PageRange>();

    /**
     * Creates an instance using a predefined set of pages to extract.
     * 
     * @param predefinedSetOfPages
     */
    public ExtractPagesParameters(PredefinedSetOfPages predefinedSetOfPages) {
        this.predefinedSetOfPages = predefinedSetOfPages;
    }

    /**
     * Creates an instance using the input page ranges selection.
     * 
     * @param pageRanges
     */
    public ExtractPagesParameters(Collection<PageRange> pageRanges) {
        this.pageSelection.addAll(pageRanges);
    }

    public PredefinedSetOfPages getPredefinedSetOfPages() {
        return predefinedSetOfPages;
    }

    /**
     * @return an unmodifiable view of the pageSelection
     */
    public Set<PageRange> getPageSelection() {
        return Collections.unmodifiableSet(pageSelection);
    }

    /**
     * @param totalNumberOfPage
     *            the number of pages of the document (upper limit).
     * @return the selected set of pages. Iteration ordering is predictable, it is the order in which elements were inserted into the {@link PageRange} set or the natural order in
     *         case of {@link PredefinedSetOfPages}.
     * @see PagesSelection#getPages(int)
     */
    public Set<Integer> getPages(int upperLimit) {
        if (predefinedSetOfPages != null) {
            return predefinedSetOfPages.getPages(upperLimit);
        }
        Set<Integer> retSet = new NullSafeSet<Integer>();
        for (PageRange range : getPageSelection()) {
            retSet.addAll(range.getPages(upperLimit));
        }
        return retSet;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(predefinedSetOfPages).append(pageSelection)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ExtractPagesParameters)) {
            return false;
        }
        ExtractPagesParameters parameter = (ExtractPagesParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other))
                .append(predefinedSetOfPages, parameter.predefinedSetOfPages)
                .append(pageSelection, parameter.pageSelection).isEquals();
    }
}
