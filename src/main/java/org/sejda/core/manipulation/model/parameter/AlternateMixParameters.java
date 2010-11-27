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
package org.sejda.core.manipulation.model.parameter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.sejda.core.manipulation.model.input.PdfMixInput;

/**
 * Parameter class for the alternate mix manipulation. Accepts a list of two {@link org.sejda.core.manipulation.model.input.PdfSource} that will be mixed.
 * 
 * @author Andrea Vacondio
 * 
 */
public class AlternateMixParameters extends AbstractParameters {

    @NotNull
    @Valid
    private PdfMixInput firstInput;
    @NotNull
    @Valid
    private PdfMixInput secondInput;

    public PdfMixInput getFirstInput() {
        return firstInput;
    }

    public void setFirstInput(PdfMixInput firstInput) {
        this.firstInput = firstInput;
    }

    public PdfMixInput getSecondInput() {
        return secondInput;
    }

    public void setSecondInput(PdfMixInput secondInput) {
        this.secondInput = secondInput;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(firstInput).append(secondInput).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof AlternateMixParameters)) {
            return false;
        }
        AlternateMixParameters parameter = (AlternateMixParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(firstInput, parameter.getFirstInput())
                .append(firstInput, parameter.getSecondInput()).isEquals();
    }

}
