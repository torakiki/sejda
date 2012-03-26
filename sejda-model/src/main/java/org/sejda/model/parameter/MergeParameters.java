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
package org.sejda.model.parameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.input.PdfMergeInput;
import org.sejda.model.output.SingleTaskOutput;
import org.sejda.model.parameter.base.AbstractPdfOutputParameters;
import org.sejda.model.parameter.base.SingleOutputTaskParameters;
import org.sejda.model.validation.constraint.NotEmpty;
import org.sejda.model.validation.constraint.SingleOutputAllowedExtensions;

/**
 * Parameter class for a merge task containing a collection of input to be merged.
 * 
 * @author Andrea Vacondio
 * 
 */
@SingleOutputAllowedExtensions
public class MergeParameters extends AbstractPdfOutputParameters implements SingleOutputTaskParameters {

    @NotEmpty
    @Valid
    private List<PdfMergeInput> inputList = new ArrayList<PdfMergeInput>();
    private boolean copyFormFields;
    private boolean blankPageIfOdd;
    private String outputName;
    @Valid
    @NotNull
    private SingleTaskOutput<?> output;

    public MergeParameters() {
        this.copyFormFields = false;
        this.blankPageIfOdd = false;
    }

    public MergeParameters(boolean copyFormFields, boolean blankPageIfOdd) {
        this.copyFormFields = copyFormFields;
        this.blankPageIfOdd = blankPageIfOdd;
    }

    public SingleTaskOutput<?> getOutput() {
        return output;
    }

    public void setOutput(SingleTaskOutput<?> output) {
        this.output = output;
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
     * @param outputName
     *            the outputName to be used when the output is not a file destination
     */
    public void setOutputName(String outputName) {
        this.outputName = outputName;
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

    public boolean isBlankPageIfOdd() {
        return blankPageIfOdd;
    }

    /**
     * Setting this true tells the task to add a blank page after each merged document if the number of pages is odd. It can be useful to print the document double-sided.
     * 
     * @param blankPageIfOdd
     */
    public void setBlankPageIfOdd(boolean blankPageIfOdd) {
        this.blankPageIfOdd = blankPageIfOdd;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(inputList).append(copyFormFields)
                .append(blankPageIfOdd).append(outputName).toHashCode();
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
        return new EqualsBuilder().appendSuper(super.equals(other)).append(inputList, parameter.getInputList())
                .append(copyFormFields, parameter.isCopyFormFields())
                .append(blankPageIfOdd, parameter.isBlankPageIfOdd()).append(outputName, parameter.getOutputName())
                .isEquals();
    }
}
