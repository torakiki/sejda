/*
 * Created on 26 ago 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.input.PdfMixInput;
import org.sejda.model.output.SingleTaskOutput;
import org.sejda.model.parameter.base.AbstractPdfOutputParameters;
import org.sejda.model.parameter.base.SingleOutputTaskParameters;
import org.sejda.model.validation.constraint.AtLeastTwo;
import org.sejda.model.validation.constraint.SingleOutputAllowedExtensions;

/**
 * Parameter class for a task mixing multiple input PDFs.
 * 
 * @author Andrea Vacondio
 */
@SingleOutputAllowedExtensions
public class AlternateMixMultipleInputParameters extends AbstractPdfOutputParameters
        implements SingleOutputTaskParameters {
    @Valid
    @NotNull
    private SingleTaskOutput output;

    @Valid
    @AtLeastTwo
    private List<PdfMixInput> inputList = new ArrayList<PdfMixInput>();

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
    public List<PdfMixInput> getInputList() {
        return Collections.unmodifiableList(inputList);
    }

    public void addInput(PdfMixInput input) {
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
        if (!(other instanceof AlternateMixMultipleInputParameters)) {
            return false;
        }
        AlternateMixMultipleInputParameters parameter = (AlternateMixMultipleInputParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(inputList, parameter.inputList)
                .append(output, parameter.getOutput()).isEquals();
    }
}
