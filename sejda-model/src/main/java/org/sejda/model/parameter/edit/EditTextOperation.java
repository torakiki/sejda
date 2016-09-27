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
import org.sejda.model.TopLeftRectangularBox;
import org.sejda.model.pdf.page.PageRange;

/**
 * Edits existing page text. Replaces any text found in the specified bounding box with the text provided.
 */
public class EditTextOperation {
    private String text;
    private TopLeftRectangularBox boundingBox;
    private PageRange pageRange;

    public EditTextOperation(String text, TopLeftRectangularBox boundingBox, PageRange pageRange) {
        this.text = text;
        this.boundingBox = boundingBox;
        this.pageRange = pageRange;
    }

    public String getText() {
        return text;
    }

    public TopLeftRectangularBox getBoundingBox() {
        return boundingBox;
    }

    public PageRange getPageRange() {
        return pageRange;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        EditTextOperation that = (EditTextOperation) o;

        return new EqualsBuilder()
                .append(text, that.text)
                .append(boundingBox, that.boundingBox)
                .append(pageRange, that.pageRange)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(text)
                .append(boundingBox)
                .append(pageRange)
                .toHashCode();
    }
}
