/*
 * Created on 09/set/2011
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
package org.sejda.model.parameter;

import java.util.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.common.collection.NullSafeSet;
import org.sejda.model.RectangularBox;
import org.sejda.model.parameter.base.MultiplePdfSourceMultipleOutputParameters;
import org.sejda.model.pdf.form.AcroFormPolicy;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.validation.constraint.NotEmpty;

/**
 * Parameter containing rectangular selection to be cropped on an input pdf source.
 * 
 * @author Andrea Vacondio
 * 
 */
public class CropParameters extends MultiplePdfSourceMultipleOutputParameters {

    @Valid
    @NotEmpty
    private final Set<RectangularBox> cropAreas = new NullSafeSet<RectangularBox>();

    @NotNull
    private AcroFormPolicy acroFormPolicy = AcroFormPolicy.MERGE;

    @Valid
    private final Set<PageRange> excludedPagesSelection = new NullSafeSet<PageRange>();

    /**
     * @return an unmodifiable view of the crop areas.
     */
    public Set<RectangularBox> getCropAreas() {
        return Collections.unmodifiableSet(cropAreas);
    }

    /**
     * clear crop areas
     */
    public void clearCropAreas() {
        cropAreas.clear();
    }

    /**
     * adds a area to the crop areas set.
     * 
     * @param area
     */
    public void addCropArea(RectangularBox area) {
        cropAreas.add(area);
    }

    public AcroFormPolicy getAcroFormPolicy() {
        return acroFormPolicy;
    }

    public void setAcroFormPolicy(AcroFormPolicy acroFormPolicy) {
        this.acroFormPolicy = acroFormPolicy;
    }

    public void addExcludedPage(Integer page) {
        addExcludedPageRange(new PageRange(page, page));
    }

    public void addExcludedPageRange(PageRange range) {
        excludedPagesSelection.add(range);
    }

    public void addAllExcludedPageRanges(Collection<PageRange> ranges) {
        excludedPagesSelection.addAll(ranges);
    }

    /**
     * @return an unmodifiable view of the excludedPagesSelection
     */
    public Set<PageRange> getExcludedPagesSelection() {
        return Collections.unmodifiableSet(excludedPagesSelection);
    }

    /**
     * @param upperLimit
     *            the number of pages of the document (upper limit).
     * @return the set of excluded pages.
     */
    public Set<Integer> getExcludedPages(int upperLimit) {
        Set<Integer> pages = new NullSafeSet<Integer>();
        for (PageRange range : getExcludedPagesSelection()) {
            pages.addAll(range.getPages(upperLimit));
        }
        return pages;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(cropAreas).append(acroFormPolicy)
                .append(excludedPagesSelection)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CropParameters)) {
            return false;
        }
        CropParameters parameter = (CropParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(cropAreas, parameter.cropAreas)
                .append(acroFormPolicy, parameter.acroFormPolicy)
                .append(excludedPagesSelection, parameter.excludedPagesSelection)
                .isEquals();
    }
}
