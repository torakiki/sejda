/*
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
import org.sejda.model.pdf.StandardType1Font;
import org.sejda.model.pdf.page.PageRange;

import java.awt.*;
import java.awt.geom.Point2D;

public class AddTextOperation {

    private String text;
    private StandardType1Font font;
    private double fontSize;
    private Color color = Color.BLACK;
    private Point2D position;
    private PageRange pageRange;

    public AddTextOperation(String text, StandardType1Font font, double fontSize, Color color, Point2D position, PageRange pageRange) {
        this.text = text;
        this.font = font;
        this.fontSize = fontSize;
        this.color = color;
        this.position = position;
        this.pageRange = pageRange;
    }

    public String getText() {
        return text;
    }

    public StandardType1Font getFont() {
        return font;
    }

    public double getFontSize() {
        return fontSize;
    }

    public Color getColor() {
        return color;
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

        AddTextOperation that = (AddTextOperation) o;

        return new EqualsBuilder()
                .append(fontSize, that.fontSize)
                .append(text, that.text)
                .append(font, that.font)
                .append(color, that.color)
                .append(position, that.position)
                .append(pageRange, that.pageRange)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(text)
                .append(font)
                .append(fontSize)
                .append(color)
                .append(position)
                .append(pageRange)
                .toHashCode();
    }
}
