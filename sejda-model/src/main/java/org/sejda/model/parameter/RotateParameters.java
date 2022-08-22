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

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.parameter.base.MultiplePdfSourceMultipleOutputParameters;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.pdf.page.PredefinedSetOfPages;
import org.sejda.model.rotation.Rotation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Parameter class for the rotation manipulation.
 *
 * Accepts a list of {@link org.sejda.model.input.PdfSource}
 *
 * Can specify a rotation to be applied to a predefined set of pages (all, even, odd), all sources.
 * Can specify a rotation to be applied per page range, all sources.
 * Can specify a rotation to be applied per page range, per source.
 * 
 * @author Andrea Vacondio
 * @author Edi Weissmann
 * 
 */
public class RotateParameters extends MultiplePdfSourceMultipleOutputParameters {

    @Valid
    @NotNull
    // same rotation all pages, all sources
    private Rotation rotation = null;

    @NotNull
    // same rotation per page set, all sources
    private PredefinedSetOfPages predefinedSetOfPages;

    @Valid
    // different rotations per page, all sources
    private final Map<PageRange, Rotation> pageSelection = new HashMap<>();

    // different rotations per page, per source
    private Map<Integer, Map<PageRange, Rotation>> pageSelectionPerSource = new HashMap<>();

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
        Rotation defaultRotation = Rotation.DEGREES_0;
        if(predefinedSetOfPages.includes(page)) {
            defaultRotation = rotation;
        }

        return pageSelection.keySet().stream().filter(range -> range.contains(page)).findFirst().map(pageSelection::get)
                .orElse(defaultRotation);
    }

    public Rotation getRotation(int sourceIndex, int page) {
        Map<PageRange, Rotation> pageSelection = pageSelectionPerSource.get(sourceIndex);
        if(pageSelection != null) {
            for(PageRange range: pageSelection.keySet()) {
                if(range.contains(page)) {
                    return pageSelection.get(range);
                }
            }
        }

        // no source specific rotation defined for page
        return getRotation(page);
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

    public void addPageRangePerSource(int sourceIndex, PageRange range, Rotation rotation) {
        if(!pageSelectionPerSource.containsKey(sourceIndex)) {
            pageSelectionPerSource.put(sourceIndex, new HashMap<>());
        }

        Map<PageRange, Rotation> pageSelection = pageSelectionPerSource.get(sourceIndex);
        pageSelection.put(range, rotation);
    }

    public PredefinedSetOfPages getPredefinedSetOfPages() {
        return predefinedSetOfPages;
    }

    public Map<PageRange, Rotation> getPageSelection() {
        return pageSelection;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(rotation).append(predefinedSetOfPages)
                .append(pageSelection).append(pageSelectionPerSource).toHashCode();
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
                .append(pageSelection, parameter.pageSelection)
                .append(rotation, parameter.rotation)
                .append(pageSelectionPerSource, parameter.pageSelectionPerSource)
                .isEquals();
    }
}
