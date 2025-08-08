/*
 * Copyright 2017 by Edi Weissmann (edi.weissmann@gmail.com).
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
import org.apache.commons.lang3.builder.ToStringStyle;

public class PageSize {

    private final float width;
    private final float height;
    private final String name;

    /** user space units per inch */
    private static final float POINTS_PER_INCH = 72;

    /** user space units per millimeter */
    private static final float POINTS_PER_MM = 1 / (10 * 2.54f) * POINTS_PER_INCH;

    /** A rectangle the size of U.S. Letter, 8.5" x 11". */
    public static final PageSize LETTER = new PageSize(8.5f * POINTS_PER_INCH, 11f * POINTS_PER_INCH, "Letter");
    /** A rectangle the size of U.S. Legal, 8.5" x 14". */
    public static final PageSize LEGAL = new PageSize(8.5f * POINTS_PER_INCH, 14f * POINTS_PER_INCH, "Legal");
    /** A rectangle the size of U.S. Ledger, 11" x 17". */
    public static final PageSize LEDGER = new PageSize(11f * POINTS_PER_INCH,17f * POINTS_PER_INCH, "Ledger");
    /** A rectangle the size of U.S. Tabloid, 17" x 11". */
    public static final PageSize TABLOID = new PageSize(17f * POINTS_PER_INCH,11f * POINTS_PER_INCH, "Tabloid");
    /** A rectangle the size of U.S. Executive, 7.25" x 10.55". */
    public static final PageSize EXECUTIVE = new PageSize(7.25f * POINTS_PER_INCH,10.55f * POINTS_PER_INCH, "Executive");

    /** A rectangle the size of A0 Paper. */
    public static final PageSize A0 = new PageSize(841 * POINTS_PER_MM, 1189 * POINTS_PER_MM, "A0");

    /** A rectangle the size of A1 Paper. */
    public static final PageSize A1 = new PageSize(594 * POINTS_PER_MM, 841 * POINTS_PER_MM, "A1");

    /** A rectangle the size of A2 Paper. */
    public static final PageSize A2 = new PageSize(420 * POINTS_PER_MM, 594 * POINTS_PER_MM, "A2");

    /** A rectangle the size of A3 Paper. */
    public static final PageSize A3 = new PageSize(297 * POINTS_PER_MM, 420 * POINTS_PER_MM, "A3");

    /** A rectangle the size of A4 Paper. */
    public static final PageSize A4 = new PageSize(210 * POINTS_PER_MM, 297 * POINTS_PER_MM, "A4");

    /** A rectangle the size of A5 Paper. */
    public static final PageSize A5 = new PageSize(148 * POINTS_PER_MM, 210 * POINTS_PER_MM, "A5");

    /** A rectangle the size of A6 Paper. */
    public static final PageSize A6 = new PageSize(105 * POINTS_PER_MM, 148 * POINTS_PER_MM, "A6");

    public PageSize(float width, float height, String name) {
        this.width = width;
        this.height = height;
        this.name = name;
    }

    public PageSize(float width, float height) {
        this(width, height, null);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    /**
     * @return a new {@link PageSize} instance with dimensions rotated.
     */
    public PageSize rotate() {
        return new PageSize(height, width);
    }

    public boolean isLandscape() {
        return getWidth() > getHeight();
    }

    /**
     * @return a new {@link PageSize} instance with dimensions rotated if the current instance
     * is in landscape orientation, otherwise returns the current instance.
     */
    public PageSize toPortrait() {
        if (isLandscape()) {
            return rotate();
        }
        return this;
    }

    /**
     * @return a new {@link PageSize} instance with dimensions rotated if the current instance
     * is not in landscape orientation, otherwise returns the current instance.
     */
    public PageSize toLandscape() {
        if (!isLandscape()) {
            return rotate();
        }
        return this;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof PageSize instance)) {
            return false;
        }

        return new EqualsBuilder().append(width, instance.width).append(height, instance.height).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(width).append(height).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("widthInches", width)
                .append("heightInches", height).append("widthInches", width).append("height", height).toString();
    }

    public static PageSize fromInches(float widthInch, float heightInch) {
        return new PageSize(widthInch * POINTS_PER_INCH, heightInch * POINTS_PER_INCH);
    }

    public static PageSize fromMillimeters(float widthInch, float heightInch) {
        return new PageSize(widthInch * POINTS_PER_MM, heightInch * POINTS_PER_MM);
    }
}
