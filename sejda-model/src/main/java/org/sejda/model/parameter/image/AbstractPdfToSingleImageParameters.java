/*
 * Created on 18/set/2011
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
package org.sejda.model.parameter.image;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.image.ImageColorType;
import org.sejda.model.output.SingleTaskOutput;
import org.sejda.model.parameter.base.SingleOutputTaskParameters;
import org.sejda.model.validation.constraint.ValidSingleOutput;

/**
 * Base class for a parameter meant to convert an existing pdf source to a single image of a specified type. The type must support multiple images into one image file.
 * 
 * @author Andrea Vacondio
 * 
 */
@ValidSingleOutput
public abstract class AbstractPdfToSingleImageParameters extends AbstractPdfToImageParameters
        implements SingleOutputTaskParameters {

    AbstractPdfToSingleImageParameters(ImageColorType outputImageColorType) {
        super(outputImageColorType);
    }

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

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(output).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AbstractPdfToSingleImageParameters)) {
            return false;
        }
        AbstractPdfToSingleImageParameters parameter = (AbstractPdfToSingleImageParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(output, parameter.getOutput()).isEquals();
    }

}
