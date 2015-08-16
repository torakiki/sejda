/*
 * Created on 18/set/2011
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
package org.sejda.model.parameter.image;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.common.collection.NullSafeSet;
import org.sejda.model.image.ImageColorType;
import org.sejda.model.output.MultipleTaskOutput;
import org.sejda.model.parameter.base.MultipleOutputTaskParameters;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.pdf.page.PageRangeSelection;
import org.sejda.model.pdf.page.PagesSelection;
import org.sejda.model.pdf.page.PredefinedSetOfPages;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Base class for a parameter meant to convert an existing pdf source to multiple images of a specified type.
 * 
 * @author Andrea Vacondio
 * 
 */
public abstract class AbstractPdfToMultipleImageParameters extends AbstractPdfToImageParameters implements
        MultipleOutputTaskParameters, PageRangeSelection, PagesSelection {

    AbstractPdfToMultipleImageParameters(ImageColorType outputImageColorType) {
        super(outputImageColorType);
    }

    private String outputPrefix = "";
    @Valid
    @NotNull
    private MultipleTaskOutput<?> output;

    @Valid
    private final Set<PageRange> pageSelection = new NullSafeSet<PageRange>();

    public void addPageRange(PageRange range) {
        pageSelection.add(range);
    }

    public void addAllPageRanges(Collection<PageRange> ranges) {
        pageSelection.addAll(ranges);
    }

    /**
     * @return an unmodifiable view of the pageSelection
     */
    public Set<PageRange> getPageSelection() {
        return Collections.unmodifiableSet(pageSelection);
    }

    /**
     * @param upperLimit
     *            the number of pages of the document (upper limit).
     * @return the selected set of pages. Iteration ordering is predictable, it is the order in which elements were inserted into the {@link PageRange} set or the natural order in
     *         case of all pages.
     * @see org.sejda.model.pdf.page.PagesSelection#getPages(int)
     */
    public Set<Integer> getPages(int upperLimit) {
        if (pageSelection.isEmpty()) {
            return PredefinedSetOfPages.ALL_PAGES.getPages(upperLimit);
        }

        Set<Integer> retSet = new NullSafeSet<Integer>();
        for (PageRange range : getPageSelection()) {
            retSet.addAll(range.getPages(upperLimit));
        }
        return retSet;
    }

    public String getOutputPrefix() {
        return outputPrefix;
    }

    public void setOutputPrefix(String outputPrefix) {
        this.outputPrefix = outputPrefix;
    }

    public MultipleTaskOutput<?> getOutput() {
        return output;
    }

    public void setOutput(MultipleTaskOutput<?> output) {
        this.output = output;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(output).append(outputPrefix).append(pageSelection).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AbstractPdfToMultipleImageParameters)) {
            return false;
        }
        AbstractPdfToMultipleImageParameters parameter = (AbstractPdfToMultipleImageParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(output, parameter.getOutput())
                .append(outputPrefix, parameter.getOutputPrefix()).append(pageSelection, parameter.pageSelection).isEquals();
    }
}
