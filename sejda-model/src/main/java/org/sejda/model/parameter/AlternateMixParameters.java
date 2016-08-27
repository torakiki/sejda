/*
 * Created on 27/nov/2010
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.model.parameter;

import static java.util.Arrays.asList;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.input.PdfMixInput;
import org.sejda.model.output.SingleTaskOutput;
import org.sejda.model.validation.constraint.SingleOutputAllowedExtensions;

/**
 * Parameter class for the alternate mix manipulation. Accepts two {@link PdfMixInput} that will be mixed.
 * 
 * @author Andrea Vacondio
 * @deprecated use {@link AlternateMixMultipleInputParameters} instead
 */
@SingleOutputAllowedExtensions
@Deprecated
public class AlternateMixParameters extends AbstractAlternateMixParameters {

    @Valid
    @NotNull
    private SingleTaskOutput<?> output;
    @NotNull
    @Valid
    private PdfMixInput firstInput;
    @NotNull
    @Valid
    private PdfMixInput secondInput;
    private String outputName;

    /**
     * Constructor for an alternate mix parameter instance.
     * 
     * @param firstInput
     * @param secondInput
     */
    public AlternateMixParameters(PdfMixInput firstInput, PdfMixInput secondInput) {
        this.firstInput = firstInput;
        this.secondInput = secondInput;
    }

    @Override
    public SingleTaskOutput<?> getOutput() {
        return output;
    }

    @Override
    public void setOutput(SingleTaskOutput<?> output) {
        this.output = output;
    }

    public PdfMixInput getFirstInput() {
        return firstInput;
    }

    public PdfMixInput getSecondInput() {
        return secondInput;
    }

    /**
     * @param outputName
     *            the outputName to be used when the output is not a file destination
     */
    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

    @Override
    public String getOutputName() {
        return outputName;
    }

    @Override
    public List<PdfMixInput> getInputList() {
        return asList(getFirstInput(), getSecondInput());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(firstInput).append(secondInput)
                .append(outputName).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AlternateMixParameters)) {
            return false;
        }
        AlternateMixParameters parameter = (AlternateMixParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(firstInput, parameter.getFirstInput())
                .append(secondInput, parameter.getSecondInput()).append(outputName, parameter.getOutputName())
                .isEquals();
    }

}
