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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.commons.collection.NullSafeSet;
import org.sejda.model.image.ImageColorType;
import org.sejda.model.image.ImageType;
import org.sejda.model.parameter.base.MultiplePdfSourceMultipleOutputParameters;
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
 */
public abstract class AbstractPdfToMultipleImageParameters extends MultiplePdfSourceMultipleOutputParameters
        implements PageRangeSelection, PagesSelection, PdfToImageParameters {

    public static final int DEFAULT_DPI = 72;

    @Min(1)
    private int resolutionInDpi = DEFAULT_DPI;
    @NotNull
    private ImageColorType outputImageColorType;

    AbstractPdfToMultipleImageParameters(ImageColorType outputImageColorType) {
        this.outputImageColorType = outputImageColorType;
    }

    @Override
    public ImageColorType getOutputImageColorType() {
        return outputImageColorType;
    }

    @Override
    public void setOutputImageColorType(ImageColorType outputImageColorType) {
        this.outputImageColorType = outputImageColorType;
    }

    /**
     * @return the type of image the task executing this parameter will convert the pdf source to.
     */
    @NotNull
    public abstract ImageType getOutputImageType();

    @Override
    public int getResolutionInDpi() {
        return resolutionInDpi;
    }

    @Override
    public void setResolutionInDpi(int resolutionInDpi) {
        this.resolutionInDpi = resolutionInDpi;
    }

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

        Set<Integer> retSet = new NullSafeSet<Integer>();
        for (PageRange range : getPageSelection()) {
            retSet.addAll(range.getPages(upperLimit));
        }
        return retSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AbstractPdfToMultipleImageParameters that = (AbstractPdfToMultipleImageParameters) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(resolutionInDpi, that.resolutionInDpi)
                .append(outputImageColorType, that.outputImageColorType)
                .append(pageSelection, that.pageSelection)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(resolutionInDpi)
                .append(outputImageColorType)
                .append(pageSelection)
                .toHashCode();
    }
}
