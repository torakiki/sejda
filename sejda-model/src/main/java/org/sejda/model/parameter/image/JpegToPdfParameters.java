/*
 * Copyright 2016 by Edi Weissmann (edi.weissmann@gmail.com).
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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.parameter.PageOrientation;
import org.sejda.model.parameter.PageSize;
import org.sejda.model.parameter.base.MultipleSourceMultipleOutputParameters;

public class JpegToPdfParameters extends MultipleSourceMultipleOutputParameters {

    private PageSize pageSize = PageSize.A4;
    private boolean pageSizeMatchImageSize = false;
    private PageOrientation pageOrientation = PageOrientation.AUTO;
    private float marginInches;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;
        JpegToPdfParameters other = (JpegToPdfParameters) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(pageSizeMatchImageSize, other.pageSizeMatchImageSize)
                .append(pageSize, other.pageSize)
                .append(pageOrientation, other.pageOrientation)
                .append(marginInches, other.marginInches)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(pageSize)
                .append(pageSizeMatchImageSize)
                .append(pageOrientation)
                .append(marginInches)
                .toHashCode();
    }

    public PageSize getPageSize() {
        return pageSize;
    }

    public void setPageSize(PageSize pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isPageSizeMatchImageSize() {
        return pageSizeMatchImageSize;
    }

    public void setPageSizeMatchImageSize(boolean pageSizeMatchImageSize) {
        this.pageSizeMatchImageSize = pageSizeMatchImageSize;
    }

    public PageOrientation getPageOrientation() {
        return pageOrientation;
    }

    public void setPageOrientation(PageOrientation pageOrientation) {
        this.pageOrientation = pageOrientation;
    }

    public float getMarginInches() {
        return marginInches;
    }

    public void setMarginInches(float marginInches) {
        this.marginInches = marginInches;
    }
}
