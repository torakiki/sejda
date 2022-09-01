/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com).
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.model;

import java.awt.Rectangle;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Rectangle representation that defines (0, 0) as top-left corner.
 * Increasing x increases its width.
 *
 * It's usually what the client sees, so 0,0 is the top left coordinate from the crop box.
 * If the crop and media boxes differ, these coordindates would need to be adjusted to be media box 0,0 relative.
 */
public class TopLeftRectangularBox {
    final int left;
    final int top;
    final int width;
    final int height;

    public TopLeftRectangularBox(int left, int top, int width, int height) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
    }

    public TopLeftRectangularBox(Rectangle r) {
        this(r.x, r.y, r.width, r.height);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("left", left).append("top", top).append("width", width)
                .append("height", height).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(left).append(top).append(width).append(height).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TopLeftRectangularBox instance)) {
            return false;
        }
        return new EqualsBuilder().append(left, instance.left).append(top, instance.top)
                .append(width, instance.width).append(height, instance.height).isEquals();
    }

    public Rectangle asRectangle() {
        return new Rectangle(left, top, width, height);
    }

    public TopLeftRectangularBox intersection(TopLeftRectangularBox other) {
        return new TopLeftRectangularBox(this.asRectangle().intersection(other.asRectangle()));
    }

    public TopLeftRectangularBox withPadding(int padding) {
        return new TopLeftRectangularBox(left - padding, top - padding, width + 2 * padding, height + 2 * padding);
    }

    public boolean containsPoint(float x, float y) {
        return this.left <= x && this.left + width  >= x && this.top <= y && this.top + height >= y;
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
