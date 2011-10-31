/*
 * Created on 05/set/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.sejda.model;

import java.awt.Point;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sejda.model.rotation.Rotation;
import org.sejda.model.validation.constraint.NotNegativeCoordinates;
import org.sejda.model.validation.constraint.ValidCoordinates;

/**
 * A rectangular box with rotation capabilities.
 * 
 * @author Andrea Vacondio
 * 
 */
@ValidCoordinates
@NotNegativeCoordinates
public final class RectangularBox {

    private int bottom;
    private int left;
    private int top;
    private int right;

    private RectangularBox(int bottom, int left, int top, int right) {
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
     * @throws IllegalArgumentException
     *             if one of the arguments is negative, top is lower then bottom or right is lower then left.
     */
    public static RectangularBox newInstance(int bottom, int left, int top, int right) {
        assertNotNegative(bottom);
        assertNotNegative(left);
        assertNotNegative(top);
        assertNotNegative(right);
        if (top <= bottom) {
            throw new IllegalArgumentException("Top must be greter then bottom.");
        }
        if (right <= left) {
            throw new IllegalArgumentException("Right must be greter then left.");
        }
        return new RectangularBox(bottom, left, top, right);
    }

    private static void assertNotNegative(int value) {
        if (value < 0) {
            throw new IllegalArgumentException(String.format("Found negative value %d", value));
        }
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
}
