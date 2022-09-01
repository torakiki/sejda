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
 * Extract chapters to separate documents based on the bookmarks in the outline
 * <p>
 * Specify which outline level to use for selecting bookmarks and optionally a regex to filter them.
 */
public class ExtractByOutlineParameters extends MultiplePdfSourceMultipleOutputParameters
        implements OptimizableOutputTaskParameters, DiscardableOutlineTaskParameters {

    @NotNull
    private OptimizationPolicy optimizationPolicy = OptimizationPolicy.NO;
    @Min(1)
    private int level;
    private String matchingTitleRegEx;
    private boolean discardOutline = false;
    private boolean includePageAfter = false;

    public ExtractByOutlineParameters(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public String getMatchingTitleRegEx() {
        return matchingTitleRegEx;
    }

    public void setMatchingTitleRegEx(String matchingTitleRegEx) {
        this.matchingTitleRegEx = matchingTitleRegEx;
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

    public boolean isIncludePageAfter() {
        return includePageAfter;
    }

    public void setIncludePageAfter(boolean includePageAfter) {
        this.includePageAfter = includePageAfter;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append("level", level)
                .append("matchingTitleRegEx", matchingTitleRegEx)
                .append("includePageAfter", includePageAfter)
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(optimizationPolicy).append(discardOutline)
                .append(level).append(matchingTitleRegEx).append(includePageAfter)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ExtractByOutlineParameters parameter)) {
            return false;
        }
        return new EqualsBuilder().appendSuper(super.equals(other))
                .append(optimizationPolicy, parameter.optimizationPolicy)
                .append(discardOutline, parameter.discardOutline).append(level, parameter.getLevel())
                .append(matchingTitleRegEx, parameter.getMatchingTitleRegEx())
                .append(includePageAfter, parameter.isIncludePageAfter())
                .isEquals();
    }
}
