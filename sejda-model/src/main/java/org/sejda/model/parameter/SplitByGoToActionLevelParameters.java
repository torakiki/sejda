/*
 * Created on 06/ago/2011
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

import javax.validation.constraints.Min;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sejda.model.parameter.base.SinglePdfSourceMultipleOutputParameters;

/**
 * Parameter class for a split by GoTo Action level task.
 * 
 * @author Andrea Vacondio
 * 
 */
public class SplitByGoToActionLevelParameters extends SinglePdfSourceMultipleOutputParameters {

    @Min(1)
    private int levelToSplitAt;
    private String matchingTitleRegEx;

    public SplitByGoToActionLevelParameters(int levelToSplitAt) {
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
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(levelToSplitAt).append(matchingTitleRegEx)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SplitByGoToActionLevelParameters)) {
            return false;
        }
        SplitByGoToActionLevelParameters parameter = (SplitByGoToActionLevelParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other))
                .append(levelToSplitAt, parameter.getLevelToSplitAt())
                .append(matchingTitleRegEx, parameter.getMatchingTitleRegEx()).isEquals();
    }
}
