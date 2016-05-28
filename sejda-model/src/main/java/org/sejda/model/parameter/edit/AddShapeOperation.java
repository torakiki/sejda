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
import org.sejda.model.pdf.page.PageRange;

import java.awt.*;
import java.awt.geom.Point2D;

public class AddShapeOperation {
    private Shape shape;

    private float width;
    private float height;

    private Point2D position;
    private PageRange pageRange;

    private float borderWidth = 1.0F;
    private Color borderColor = Color.BLACK;
    private Color backgroundColor;

    public AddShapeOperation(Shape shape, float width, float height, Point2D position, PageRange pageRange, float borderWidth, Color borderColor, Color backgroundColor) {
        this.shape = shape;
        this.width = width;
        this.height = height;
        this.position = position;
        this.pageRange = pageRange;
        this.borderWidth = borderWidth;
        this.borderColor = borderColor;
        this.backgroundColor = backgroundColor;
    }

    public Shape getShape() {
        return shape;
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

    public float getBorderWidth() {
        return borderWidth;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AddShapeOperation that = (AddShapeOperation) o;

        return new EqualsBuilder()
                .append(width, that.width)
                .append(height, that.height)
                .append(borderWidth, that.borderWidth)
                .append(shape, that.shape)
                .append(position, that.position)
                .append(pageRange, that.pageRange)
                .append(borderColor, that.borderColor)
                .append(backgroundColor, that.backgroundColor)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(shape)
                .append(width)
                .append(height)
                .append(position)
                .append(pageRange)
                .append(borderWidth)
                .append(borderColor)
                .append(backgroundColor)
                .toHashCode();
    }
}
