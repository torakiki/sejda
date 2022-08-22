/*
 * Created on 18/ago/2011
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
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.model.parameter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.input.PdfSource;
import org.sejda.model.output.MultipleTaskOutput;
import org.sejda.model.parameter.base.AbstractParameters;
import org.sejda.model.parameter.base.MultiplePdfSourceTaskParameters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Parameter class for the unpack manipulation. Accepts a list of {@link PdfSource} that will be unpacked.
 * 
 * @author Andrea Vacondio
 * 
 */
public class UnpackParameters extends AbstractParameters implements MultiplePdfSourceTaskParameters {

    @Valid
    @NotNull
    private final MultipleTaskOutput output;
    @NotEmpty
    @Valid
    private final List<PdfSource<?>> sourceList = new ArrayList<PdfSource<?>>();

    public UnpackParameters(MultipleTaskOutput output) {
        this.output = output;
    }

    @Override
    public MultipleTaskOutput getOutput() {
        return output;
    }

    /**
     * adds the input source to the source list.
     * 
     * @param input
     */
    @Override
    public void addSource(PdfSource<?> input) {
        sourceList.add(input);
    }

    /**
     * @return an unmodifiable view of the source list
     */
    @Override
    public List<PdfSource<?>> getSourceList() {
        return Collections.unmodifiableList(sourceList);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(output).append(sourceList).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UnpackParameters)) {
            return false;
        }
        UnpackParameters parameter = (UnpackParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(output, parameter.getOutput())
                .append(sourceList, parameter.getSourceList()).isEquals();
    }
}
