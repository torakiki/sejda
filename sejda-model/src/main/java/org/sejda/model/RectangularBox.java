/*
 * Created on 05/set/2011
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
package org.sejda.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sejda.model.rotation.Rotation;
import org.sejda.model.validation.constraint.ValidCoordinates;

import java.awt.Point;

import static org.sejda.commons.util.RequireUtils.requireArg;

/**
 * A rectangular box with rotation capabilities.
 *
 * @author Andrea Vacondio
 */
@ValidCoordinates
public final class RectangularBox {

    private int bottom;
    private int left;
    private int top;
    private int right;

    public RectangularBox(int bottom, int left, int top, int right) {
        requireArg(top > bottom, "Top must be greater then bottom");
        requireArg(right > left, "Right must be greater then left");

        this.bottom = bottom;
        this.left = left;
        this.top = top;
        this.right = right;
    }

    public int getBottom() {
        return bottom;
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public int getRight() {
        return right;
    }

    /**
     * rotates this {@link RectangularBox} using the given rotation. This method is null safe.
     *
     * @param desiredRotation
     */
    public void rotate(Rotation desiredRotation) {
        if (desiredRotation != null) {
            Rotation currentRotation = Rotation.DEGREES_0;
            while (!currentRotation.equals(desiredRotation)) {
                switchTopRight();
                switchBottomLeft();
                currentRotation = currentRotation.rotateClockwise();
            }
        }
    }

    private void switchTopRight() {
        int tmp = top;
        top = right;
        right = tmp;
    }

    private void switchBottomLeft() {
        int tmp = bottom;
        bottom = left;
        left = tmp;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("right", right).append("top", top).append("left", left)
                .append("bottom", bottom).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(top).append(right).append(left).append(bottom).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RectangularBox)) {
            return false;
        }
        RectangularBox instance = (RectangularBox) other;
        return new EqualsBuilder().append(top, instance.getTop()).append(right, instance.getRight())
                .append(left, instance.getLeft()).append(bottom, instance.getBottom()).isEquals();
    }

    /**
     * static factory method.
     *
     * @param bottom
     * @param left
     * @param top
     * @param right
     * @return the newly created instance.
     * @throws IllegalArgumentException if one of the arguments is negative, top is lower then bottom or right is lower then left.
     */
    public static RectangularBox newInstance(int bottom, int left, int top, int right) {
        return new RectangularBox(bottom, left, top, right);
    }

    /**
     * static factory method creating a new instance of the {@link RectangularBox} from two points.
     *
     * @param bottomLeft
     * @param topRight
     * @return newly created instance.
     * @see #newInstance(int, int, int, int)
     */
    public static RectangularBox newInstanceFromPoints(Point bottomLeft, Point topRight) {
        if (bottomLeft == null || topRight == null) {
            throw new IllegalArgumentException("null point is not allowed.");
        }
        return newInstance(bottomLeft.y, bottomLeft.x, topRight.y, topRight.x);
    }

    public TopLeftRectangularBox toTopLeftRectangularBox() {
        return new TopLeftRectangularBox(left, top, right - left, bottom - top);
    }
}
