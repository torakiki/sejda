/*
 * Created on 20 ott 2016
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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.common.collection.NullSafeSet;
import org.sejda.model.input.Source;
import org.sejda.model.parameter.base.MultiplePdfSourceMultipleOutputParameters;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.pdf.page.PageRangeSelection;
import org.sejda.model.pdf.page.PagesSelection;
import org.sejda.model.validation.constraint.PositiveDimensions;
import org.sejda.model.watermark.Location;

/**
 * Parameters to set a watermark image on multiple PDF documents
 * 
 * @author Andrea Vacondio
 */
public class WatermarkParameters extends MultiplePdfSourceMultipleOutputParameters
        implements PageRangeSelection, PagesSelection {

    @Valid
    private final Set<PageRange> pageSelection = new NullSafeSet<PageRange>();
    @Valid
    @NotNull
    private Source<?> watermark;
    @NotNull
    private Location location = Location.BEHIND;
    @Min(0)
    @Max(100)
    private int opacity = 100;
    @PositiveDimensions
    private Dimension dimension;
    @NotNull
    private Point2D position = new Point();
    private int rotationDegrees;

    public WatermarkParameters(Source<?> watermark) {
        this.watermark = watermark;
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
        return getPageSelection().stream().flatMap(r -> r.getPages(totalNumberOfPage).stream())
                .collect(NullSafeSet::new, NullSafeSet::add, NullSafeSet::addAll);
    }

    public Source<?> getWatermark() {
        return watermark;
    }

    public int getOpacity() {
        return opacity;
    }

    /**
     * The watermark opacity where 0 is invisible and 100 is fully opaque
     * 
     * @param opacity
     */
    public void setOpacity(int opacity) {
        this.opacity = opacity;
    }

    public Location getLocation() {
        return location;
    }

    /**
     * Where the watermark image should appear relative to the page content
     * 
     * @param location
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimenstion) {
        this.dimension = dimenstion;
    }

    public int getRotationDegrees() {
        return rotationDegrees;
    }

    public void setRotationDegrees(int rotationDegrees) {
        this.rotationDegrees = rotationDegrees;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(watermark).append(pageSelection)
                .append(location).append(dimension).append(opacity).append(position)
                .append(rotationDegrees)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof WatermarkParameters)) {
            return false;
        }
        WatermarkParameters parameter = (WatermarkParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(watermark, parameter.watermark)
                .append(pageSelection, parameter.pageSelection).append(location, parameter.location)
                .append(dimension, parameter.dimension).append(opacity, parameter.opacity)
                .append(position, parameter.position)
                .append(rotationDegrees, parameter.rotationDegrees)
                .isEquals();
    }
}
