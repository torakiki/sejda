/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com)
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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sejda.model.TopLeftRectangularBox;
import org.sejda.model.parameter.base.SinglePdfSourceMultipleOutputParameters;

public class SplitByTextContentParameters extends SinglePdfSourceMultipleOutputParameters {

    @Valid
    @NotNull
    private final TopLeftRectangularBox textArea;

    public SplitByTextContentParameters(TopLeftRectangularBox textArea) {
        this.textArea = textArea;
    }

    public TopLeftRectangularBox getTextArea() {
        return textArea;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append(textArea).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(textArea).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SplitByTextContentParameters)) {
            return false;
        }
        SplitByTextContentParameters parameter = (SplitByTextContentParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(textArea, parameter.textArea).isEquals();
    }
}
