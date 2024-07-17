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

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.commons.collection.NullSafeSet;
import org.sejda.model.image.ImageColorType;
import org.sejda.model.output.SingleTaskOutput;
import org.sejda.model.parameter.base.SingleOutputTaskParameters;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.pdf.page.PageRangeSelection;
import org.sejda.model.pdf.page.PagesSelection;
import org.sejda.model.pdf.page.PredefinedSetOfPages;
import org.sejda.model.validation.constraint.ValidSingleOutput;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Base class for a parameter meant to convert an existing pdf source to a single image of a specified type. The type must support multiple images into one image file.
 * 
 * @author Andrea Vacondio
 * 
 */
@ValidSingleOutput
public abstract class AbstractPdfToSingleImageParameters extends AbstractPdfToImageParameters
        implements PageRangeSelection, PagesSelection, SingleOutputTaskParameters {

    AbstractPdfToSingleImageParameters(ImageColorType outputImageColorType) {
        super(outputImageColorType);
    }

    @Valid
    @NotNull
    private SingleTaskOutput output;

    @Override
    public SingleTaskOutput getOutput() {
        return output;
    }

    @Override
    public void setOutput(SingleTaskOutput output) {
        this.output = output;
    }

    @Valid
    private final Set<PageRange> pageSelection = new NullSafeSet<>();

    public void addPageRange(PageRange range) {
        pageSelection.add(range);
    }

    public void addAllPageRanges(Collection<PageRange> ranges) {
        pageSelection.addAll(ranges);
    }

    /**
     * @return an unmodifiable view of the pageSelection
     */
    @Override
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
    @Override
    public Set<Integer> getPages(int upperLimit) {
        if (pageSelection.isEmpty()) {
            return PredefinedSetOfPages.ALL_PAGES.getPages(upperLimit);
        }

        Set<Integer> retSet = new NullSafeSet<>();
        for (PageRange range : getPageSelection()) {
            retSet.addAll(range.getPages(upperLimit));
        }
        return retSet;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(output).append(pageSelection).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AbstractPdfToSingleImageParameters parameter)) {
            return false;
        }
        return new EqualsBuilder().appendSuper(super.equals(other))
                .append(output, parameter.getOutput())
                .append(pageSelection, parameter.getPageSelection())
                .isEquals();
    }

}
