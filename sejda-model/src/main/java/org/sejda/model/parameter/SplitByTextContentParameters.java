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

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sejda.model.TopLeftRectangularBox;
import org.sejda.model.optimization.OptimizationPolicy;
import org.sejda.model.parameter.base.DiscardableOutlineTaskParameters;
import org.sejda.model.parameter.base.OptimizableOutputTaskParameters;
import org.sejda.model.parameter.base.SinglePdfSourceMultipleOutputParameters;

/**
 * Parameter class for a split by text content change task. It lets specify an area. The task will split the document when text content in that changes.
 *
 * Can define optional conditions so split is performed only if text starts with or ends with specific prefix/suffix. This is useful when some pages contain text in the specified
 * area but it's not the one that should be split by.
 * 
 * @author Eduard Weissmann
 * 
 */
public class SplitByTextContentParameters extends SinglePdfSourceMultipleOutputParameters
        implements OptimizableOutputTaskParameters, DiscardableOutlineTaskParameters {

    @NotNull
    private final TopLeftRectangularBox textArea;
    private String startsWith = "";
    private String endsWith = "";
    @NotNull
    private OptimizationPolicy optimizationPolicy = OptimizationPolicy.NO;
    private boolean discardOutline = false;

    public SplitByTextContentParameters(TopLeftRectangularBox textArea) {
        this.textArea = textArea;
    }

    public TopLeftRectangularBox getTextArea() {
        return textArea;
    }

    public String getStartsWith() {
        return startsWith;
    }

    public String getEndsWith() {
        return endsWith;
    }

    public void setStartsWith(String startsWith) {
        this.startsWith = startsWith;
    }

    public void setEndsWith(String endsWith) {
        this.endsWith = endsWith;
    }

    @Override
    public OptimizationPolicy getOptimizationPolicy() {
        return optimizationPolicy;
    }

    @Override
    public void setOptimizationPolicy(OptimizationPolicy optimizationPolicy) {
        this.optimizationPolicy = optimizationPolicy;
    }

    @Override
    public boolean discardOutline() {
        return discardOutline;
    }

    @Override
    public void discardOutline(boolean discardOutline) {
        this.discardOutline = discardOutline;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append(textArea).append(startsWith)
                .append(endsWith).toString();

    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(optimizationPolicy).append(discardOutline)
                .append(textArea)
                .toHashCode();
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
        return new EqualsBuilder().appendSuper(super.equals(other))
                .append(optimizationPolicy, parameter.optimizationPolicy)
                .append(discardOutline, parameter.discardOutline).append(textArea, parameter.textArea)
                .append(startsWith, parameter.startsWith).append(endsWith, parameter.endsWith).isEquals();
    }
}
