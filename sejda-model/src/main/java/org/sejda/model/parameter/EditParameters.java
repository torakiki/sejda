package org.sejda.model.parameter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.parameter.base.MultiplePdfSourceMultipleOutputParameters;
import org.sejda.model.parameter.edit.*;

import java.util.ArrayList;
import java.util.List;

public class EditParameters extends MultiplePdfSourceMultipleOutputParameters {

    private List<AddTextOperation> textOperations = new ArrayList<>();
    private List<AddImageOperation> imageOperations = new ArrayList<>();
    private List<InsertPageOperation> insertPageOperations = new ArrayList<>();
    private List<DeletePageOperation> deletePageOperations = new ArrayList<>();
    private List<HighlightTextOperation> highlightTextOperations = new ArrayList<>();

    public void addTextOperation(AddTextOperation operation) {
        textOperations.add(operation);
    }

    public void addImageOperation(AddImageOperation operation) {
        imageOperations.add(operation);
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
                .append(insertPageOperations)
                .append(deletePageOperations)
                .append(highlightTextOperations)
                .toHashCode();
    }
}
