/*
 * Copyright 2016 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.model.parameter.edit;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.input.Source;
import org.sejda.model.pdf.page.PageRange;

import java.awt.geom.Point2D;

public class AddImageOperation {
    private Source<?> imageSource;
    private float width;
    private float height;

    private Point2D position;
    private PageRange pageRange;

    public AddImageOperation(Source<?> imageSource, float width, float height, Point2D position, PageRange pageRange) {
        this.imageSource = imageSource;
        this.width = width;
        this.height = height;
        this.position = position;
        this.pageRange = pageRange;
    }

    public Source<?> getImageSource() {
        return imageSource;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Point2D getPosition() {
        return position;
    }

    public PageRange getPageRange() {
        return pageRange;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AddImageOperation that = (AddImageOperation) o;

        return new EqualsBuilder()
                .append(width, that.width)
                .append(height, that.height)
                .append(imageSource, that.imageSource)
                .append(position, that.position)
                .append(pageRange, that.pageRange)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(imageSource)
                .append(width)
                .append(height)
                .append(position)
                .append(pageRange)
                .toHashCode();
    }
}
