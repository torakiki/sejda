/*
 * Created on 06/ago/2011
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

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sejda.model.optimization.OptimizationPolicy;
import org.sejda.model.parameter.base.DiscardableOutlineTaskParameters;
import org.sejda.model.parameter.base.MultiplePdfSourceMultipleOutputParameters;
import org.sejda.model.parameter.base.OptimizableOutputTaskParameters;

/**
 * Parameter class to split a PDF document at a given outline level.
 *
 * @author Andrea Vacondio
 */
public class SplitByOutlineLevelParameters extends MultiplePdfSourceMultipleOutputParameters
        implements OptimizableOutputTaskParameters, DiscardableOutlineTaskParameters {

    @Min(1)
    private int levelToSplitAt;
    private String matchingTitleRegEx;
    @NotNull
    private OptimizationPolicy optimizationPolicy = OptimizationPolicy.NO;
    private boolean discardOutline = false;

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

    public SplitByOutlineLevelParameters(int levelToSplitAt) {
        this.levelToSplitAt = levelToSplitAt;
    }

    public int getLevelToSplitAt() {
        return levelToSplitAt;
    }

    public String getMatchingTitleRegEx() {
        return matchingTitleRegEx;
    }

    public void setMatchingTitleRegEx(String matchingTitleRegEx) {
        this.matchingTitleRegEx = matchingTitleRegEx;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append("levelToSplitAt", levelToSplitAt)
                .append("matchingTitleRegEx", matchingTitleRegEx).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(optimizationPolicy).append(discardOutline)
                .append(levelToSplitAt).append(matchingTitleRegEx).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SplitByOutlineLevelParameters parameter)) {
            return false;
        }
        return new EqualsBuilder().appendSuper(super.equals(other))
                .append(optimizationPolicy, parameter.getOptimizationPolicy())
                .append(discardOutline, parameter.discardOutline())
                .append(levelToSplitAt, parameter.getLevelToSplitAt())
                .append(matchingTitleRegEx, parameter.getMatchingTitleRegEx()).isEquals();
    }

}
