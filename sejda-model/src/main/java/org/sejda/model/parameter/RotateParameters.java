/*
 * Created on 30/mag/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.common.collection.NullSafeSet;
import org.sejda.model.parameter.base.MultiplePdfSourceMultipleOutputParameters;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.pdf.page.PageRangeSelection;
import org.sejda.model.pdf.page.PagesSelection;
import org.sejda.model.pdf.page.PredefinedSetOfPages;
import org.sejda.model.rotation.Rotation;

/**
 * Parameter class for the rotation manipulation. Accepts a list of {@link org.sejda.model.input.PdfSource} where the {@link Rotation} will be applied to the given
 * {@link PredefinedSetOfPages} or to the given {@link PageRange}s.
 * 
 * @author Andrea Vacondio
 * 
 */
public class RotateParameters extends MultiplePdfSourceMultipleOutputParameters
        implements PagesSelection, PageRangeSelection {

    @Valid
    @NotNull
    private Rotation rotation = null;

    @NotNull
    private PredefinedSetOfPages predefinedSetOfPages;
    @Valid
    private final Map<PageRange, Rotation> pageSelection = new HashMap<>();

    public RotateParameters(Rotation rotation, PredefinedSetOfPages predefinedSetOfPages) {
        this.rotation = rotation;
        this.predefinedSetOfPages = predefinedSetOfPages;
    }

    public RotateParameters(Rotation rotation) {
        this(rotation, PredefinedSetOfPages.NONE);
    }

    public RotateParameters() {
        this(Rotation.DEGREES_0);
    }

    @Deprecated
    public Rotation getRotation() {
        return rotation;
    }

    public Rotation getRotation(int page) {
        return pageSelection.keySet().stream().filter(range -> range.contains(page)).findFirst().map(pageSelection::get)
                .orElse(rotation);
    }

    public void addPageRange(PageRange range) {
        pageSelection.put(range, this.rotation);
    }

    public void addPageRange(PageRange range, Rotation rotation) {
        pageSelection.put(range, rotation);
    }

    public void addAllPageRanges(Collection<PageRange> ranges) {
        ranges.forEach(this::addPageRange);
    }

    public PredefinedSetOfPages getPredefinedSetOfPages() {
        return predefinedSetOfPages;
    }

    /**
     * @return an unmodifiable view of the pageSelection
     */
    @Override
    public Set<PageRange> getPageSelection() {
        return Collections.unmodifiableSet(pageSelection.keySet());
    }

    /**
     * @param upperLimit
     *            the number of pages of the document (upper limit).
     * @return the selected set of pages. Iteration ordering is predictable, it is the order in which elements were inserted into the {@link PageRange} set or the natural order in
     *         case of {@link PredefinedSetOfPages}.
     * @see PagesSelection#getPages(int)
     */
    @Override
    public Set<Integer> getPages(int upperLimit) {
        if (predefinedSetOfPages != PredefinedSetOfPages.NONE) {
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
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(rotation).append(predefinedSetOfPages)
                .append(pageSelection).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RotateParameters)) {
            return false;
        }
        RotateParameters parameter = (RotateParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other))
                .append(predefinedSetOfPages, parameter.predefinedSetOfPages)
                .append(pageSelection, parameter.pageSelection).append(rotation, parameter.getRotation()).isEquals();
    }
}
