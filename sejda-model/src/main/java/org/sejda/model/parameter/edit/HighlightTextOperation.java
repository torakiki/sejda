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
import org.sejda.model.RectangularBox;

import java.awt.*;
import java.util.Set;

public class HighlightTextOperation {

    private int pageNumber;
    private Set<RectangularBox> boundingBoxes;
    private Color color;

    public HighlightTextOperation(int pageNumber, Set<RectangularBox> boundingBoxes, Color color) {
        this.pageNumber = pageNumber;
        this.boundingBoxes = boundingBoxes;
        this.color = color;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public Set<RectangularBox> getBoundingBoxes() {
        return boundingBoxes;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        HighlightTextOperation that = (HighlightTextOperation) o;

        return new EqualsBuilder()
                .append(pageNumber, that.pageNumber)
                .append(boundingBoxes, that.boundingBoxes)
                .append(color, that.color)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(pageNumber)
                .append(boundingBoxes)
                .append(color)
                .toHashCode();
    }
}
