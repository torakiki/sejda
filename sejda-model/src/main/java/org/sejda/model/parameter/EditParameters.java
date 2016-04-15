package org.sejda.model.parameter;

import org.sejda.model.parameter.base.MultiplePdfSourceMultipleOutputParameters;
import org.sejda.model.parameter.edit.AddTextOperation;

import java.util.ArrayList;
import java.util.List;

public class EditParameters extends MultiplePdfSourceMultipleOutputParameters {

    private List<AddTextOperation> textOperations = new ArrayList<>();

    public void addTextOperation(AddTextOperation operation) {
        textOperations.add(operation);
    }

    public List<AddTextOperation> getTextOperations() {
        return textOperations;
    }
}
