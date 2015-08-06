/*
 * Created on 11/ago/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import org.sejda.model.outline.OutlinePolicy;
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
    private boolean copyFormFields = false;
    private boolean blankPageIfOdd = false;
    @NotNull
    private OutlinePolicy outlinePolicy = OutlinePolicy.RETAIN;
    private String outputName;
    @Valid
    @NotNull
    private SingleTaskOutput<?> output;

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

    /**
     * Setting this true tells the task to try to merge form fields if any of the input document has forms.
     * 
     * @param copyFormFields
     */
    public void setCopyFormFields(boolean copyFormFields) {
        this.copyFormFields = copyFormFields;
    }

    public OutlinePolicy getOutlinePolicy() {
        return outlinePolicy;
    }

    /**
     * The policy that the merge task should use when handling the outline tree (bookmarks)
     * 
     * @param outlinePolicy
     */
    public void setOutlinePolicy(OutlinePolicy outlinePolicy) {
        this.outlinePolicy = outlinePolicy;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(inputList).append(copyFormFields)
                .append(blankPageIfOdd).append(outlinePolicy).append(outputName).toHashCode();
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
                .append(blankPageIfOdd, parameter.isBlankPageIfOdd())
                .append(outlinePolicy, parameter.getOutlinePolicy()).append(outputName, parameter.getOutputName())
                .isEquals();
    }
}
