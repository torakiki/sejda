/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com)
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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sejda.model.TopLeftRectangularBox;
import org.sejda.model.parameter.base.SinglePdfSourceMultipleOutputParameters;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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
