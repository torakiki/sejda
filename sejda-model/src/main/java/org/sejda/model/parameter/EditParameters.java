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
package org.sejda.model.parameter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.parameter.base.MultiplePdfSourceMultipleOutputParameters;
import org.sejda.model.parameter.edit.*;

public class EditParameters extends MultiplePdfSourceMultipleOutputParameters {

    private List<AddTextOperation> textOperations = new ArrayList<>();
    private List<AddImageOperation> imageOperations = new ArrayList<>();
    private List<AddShapeOperation> shapeOperations = new ArrayList<>();
    private List<InsertPageOperation> insertPageOperations = new ArrayList<>();
    private List<DeletePageOperation> deletePageOperations = new ArrayList<>();
    private List<HighlightTextOperation> highlightTextOperations = new ArrayList<>();

    public void addTextOperation(AddTextOperation operation) {
        textOperations.add(operation);
    }

    public void addImageOperation(AddImageOperation operation) {
        imageOperations.add(operation);
    }

    public void addShapeOperation(AddShapeOperation operation) {
        shapeOperations.add(operation);
    }

    public void addInsertPageOperation(InsertPageOperation operation) {
        insertPageOperations.add(operation);
    }

    public void addDeletePageOperation(DeletePageOperation operation) {
        deletePageOperations.add(operation);
    }

    public void addHighlightTextOperation(HighlightTextOperation operation) {
        highlightTextOperations.add(operation);
    }

    public List<AddTextOperation> getTextOperations() {
        return textOperations;
    }

    public List<AddImageOperation> getImageOperations() {
        return imageOperations;
    }

    public List<AddShapeOperation> getShapeOperations() {
        return shapeOperations;
    }

    public List<InsertPageOperation> getInsertPageOperations() {
        return insertPageOperations;
    }

    public List<DeletePageOperation> getDeletePageOperations() {
        return deletePageOperations;
    }

    public List<HighlightTextOperation> getHighlightTextOperations() {
        return highlightTextOperations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        EditParameters that = (EditParameters) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(textOperations, that.textOperations)
                .append(imageOperations, that.imageOperations)
                .append(shapeOperations, that.shapeOperations)
                .append(insertPageOperations, that.insertPageOperations)
                .append(deletePageOperations, that.deletePageOperations)
                .append(highlightTextOperations, that.highlightTextOperations)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(textOperations)
                .append(imageOperations)
                .append(shapeOperations)
                .append(insertPageOperations)
                .append(deletePageOperations)
                .append(highlightTextOperations)
                .toHashCode();
    }
}
