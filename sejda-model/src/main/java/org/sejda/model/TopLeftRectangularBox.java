package org.sejda.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.awt.*;

/**
 * Rectangle representation that defines (0, 0) as top-left corner.
 * Increasing x increases its width.
 */
public class TopLeftRectangularBox {
    final int x;
    final int y;
    final int width;
    final int height;

    public TopLeftRectangularBox(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("x", x).append("y", y).append("width", width)
                .append("height", height).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(x).append(y).append(width).append(height).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TopLeftRectangularBox)) {
            return false;
        }
        TopLeftRectangularBox instance = (TopLeftRectangularBox) other;
        return new EqualsBuilder().append(x, instance.x).append(y, instance.y)
                .append(width, instance.width).append(height, instance.height).isEquals();
    }

    public Rectangle asRectangle() {
        return new Rectangle(x, y, width, height);
    }
}
