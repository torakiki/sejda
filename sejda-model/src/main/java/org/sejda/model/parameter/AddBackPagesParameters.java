/*
 * Created on 20 gen 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.common.collection.NullSafeSet;
import org.sejda.model.input.PdfSource;
import org.sejda.model.parameter.base.MultiplePdfSourceMultipleOutputParameters;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.pdf.page.PageRangeSelection;
import org.sejda.model.pdf.page.PagesSelection;
import org.sejda.model.validation.constraint.NoIntersections;

/**
 * Parameters for a task that repeatedly adds a pages selection from a given PDF to a set of PDF documents, every 'n' pages. This allows to add back pages in a batch fashion.
 * 
 * @author Andrea Vacondio
 *
 */
@NoIntersections
public class AddBackPagesParameters extends MultiplePdfSourceMultipleOutputParameters
        implements PageRangeSelection, PagesSelection {

    @Min(value = 1)
    private int step = 1;
    @Valid
    private final Set<PageRange> pageSelection = new NullSafeSet<PageRange>();

    @Valid
    @NotNull
    private PdfSource<?> backPagesSource;

    public PdfSource<?> getBackPagesSource() {
        return backPagesSource;
    }

    public void setBackPagesSource(PdfSource<?> backPagesSource) {
        this.backPagesSource = backPagesSource;
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

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(backPagesSource).append(pageSelection)
                .append(step).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AddBackPagesParameters)) {
            return false;
        }
        AddBackPagesParameters parameter = (AddBackPagesParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other))
                .append(backPagesSource, parameter.getBackPagesSource()).append(pageSelection, parameter.pageSelection)
                .append(step, parameter.step).isEquals();
    }
}