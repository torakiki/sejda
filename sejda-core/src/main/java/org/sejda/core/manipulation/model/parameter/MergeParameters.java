/*
 * Created on 11/ago/2011
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
package org.sejda.core.manipulation.model.parameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.sejda.core.manipulation.model.input.PdfMergeInput;
import org.sejda.core.validation.constraint.NotEmpty;
import org.sejda.core.validation.constraint.ValidSingleOutput;

/**
 * Parameter class for a merge task containing a collection of input to be merged.
 * 
 * @author Andrea Vacondio
 * 
 */
@ValidSingleOutput
public class MergeParameters extends AbstractParameters implements SingleOutputDocumentParameter {

    @NotEmpty
    @Valid
    private List<PdfMergeInput> inputList = new ArrayList<PdfMergeInput>();
    private boolean copyFormFields;
    private String outputName;

    public MergeParameters() {
        this.copyFormFields = false;
    }

    public MergeParameters(boolean copyFormFields, String outputName) {
        this.copyFormFields = copyFormFields;
        this.outputName = outputName;
    }

    /**
     * @return an unmodifiable view of the inputList
     */
    public List<PdfMergeInput> getInputList() {
        return Collections.unmodifiableList(inputList);
    }

    public String getOutputName() {
        return outputName;
    }

    /**
     * adds the given input to the list of inputs for the merge task.
     * 
     * @param input
     */
    public void addInput(PdfMergeInput input) {
        this.inputList.add(input);
    }

    public boolean isCopyFormFields() {
        return copyFormFields;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(inputList).append(copyFormFields)
                .append(outputName).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MergeParameters)) {
            return false;
        }
        MergeParameters parameter = (MergeParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other))
                .append(copyFormFields, parameter.isCopyFormFields()).append(inputList, parameter.getInputList())
                .append(outputName, parameter.getOutputName()).isEquals();
    }
}
