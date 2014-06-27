/*
 * Created on 27/nov/2010
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.input.PdfMixInput;
import org.sejda.model.output.SingleTaskOutput;
import org.sejda.model.parameter.base.AbstractPdfOutputParameters;
import org.sejda.model.parameter.base.SingleOutputTaskParameters;
import org.sejda.model.validation.constraint.SingleOutputAllowedExtensions;

/**
 * Parameter class for the alternate mix manipulation. Accepts two {@link PdfMixInput} that will be mixed.
 * 
 * @author Andrea Vacondio
 * 
 */
@SingleOutputAllowedExtensions
public class AlternateMixParameters extends AbstractPdfOutputParameters implements SingleOutputTaskParameters {

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

    public SingleTaskOutput<?> getOutput() {
        return output;
    }

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

    public String getOutputName() {
        return outputName;
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
