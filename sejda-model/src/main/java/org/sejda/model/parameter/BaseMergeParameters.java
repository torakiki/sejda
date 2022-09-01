/*
 * Copyright 2019 by Edi Weissmann (edi.weissmann@gmail.com).
 *
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.model.parameter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.input.MergeInput;
import org.sejda.model.input.PdfMergeInput;
import org.sejda.model.output.SingleTaskOutput;
import org.sejda.model.parameter.base.AbstractPdfOutputParameters;
import org.sejda.model.parameter.base.SingleOutputTaskParameters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseMergeParameters<T extends MergeInput> extends AbstractPdfOutputParameters
        implements SingleOutputTaskParameters {

    @NotEmpty
    @Valid
    private List<T> inputList = new ArrayList<>();

    @Valid
    @NotNull
    private SingleTaskOutput output;

    @Override
    public SingleTaskOutput getOutput() {
        return output;
    }

    @Override
    public void setOutput(SingleTaskOutput output) {
        this.output = output;
    }

    /**
     * @return an unmodifiable view of the inputList
     */
    public List<T> getInputList() {
        return Collections.unmodifiableList(inputList);
    }

    public void setInputList(List<T> inputList) {
        this.inputList = inputList;
    }

    public List<PdfMergeInput> getPdfInputList() {
        return inputList.stream().map(input -> (PdfMergeInput) input).collect(Collectors.toUnmodifiableList());
    }

    /**
     * adds the given input to the list of inputs for the merge task.
     *
     * @param input
     */
    public void addInput(T input) {
        this.inputList.add(input);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(inputList).append(output).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof BaseMergeParameters<?> params)) {
            return false;
        }
        return new EqualsBuilder().appendSuper(super.equals(other)).append(inputList, params.inputList)
                .append(output, params.getOutput()).isEquals();
    }
}
