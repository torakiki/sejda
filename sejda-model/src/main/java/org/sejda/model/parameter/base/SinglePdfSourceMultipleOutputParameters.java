/*
 * Created on 03/ago/2011
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
package org.sejda.model.parameter.base;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.output.MultipleTaskOutput;

/**
 * Provides a skeletal implementation for the parameter classes having a single pdf source as input and generating multiple output.
 * 
 * @author Andrea Vacondio
 * 
 */
public abstract class SinglePdfSourceMultipleOutputParameters extends SinglePdfSourceParameters
        implements MultipleOutputTaskParameters {

    private String outputPrefix = "";
    @Valid
    @NotNull
    private MultipleTaskOutput output;

    @Override
    public String getOutputPrefix() {
        return outputPrefix;
    }

    @Override
    public void setOutputPrefix(String outputPrefix) {
        this.outputPrefix = outputPrefix;
    }

    @Override
    public MultipleTaskOutput getOutput() {
        return output;
    }

    @Override
    public void setOutput(MultipleTaskOutput output) {
        this.output = output;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(outputPrefix).append(output).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SinglePdfSourceMultipleOutputParameters parameter)) {
            return false;
        }
        return new EqualsBuilder().appendSuper(super.equals(other)).append(outputPrefix, parameter.outputPrefix)
                .append(output, parameter.output).isEquals();
    }
}
