/*
 * Created on 03/ago/2011
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

import javax.validation.constraints.Min;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Parameter class for a split by size task.
 * 
 * @author Andrea Vacondio
 * 
 */
public class SplitBySizeParameters extends SinglePdfSourceMultipleOutputParameters {

    @Min(1)
    private long sizeToSplitAt;

    public SplitBySizeParameters(long sizeToSplitAt) {
        this.sizeToSplitAt = sizeToSplitAt;
    }

    public long getSizeToSplitAt() {
        return sizeToSplitAt;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(sizeToSplitAt).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SplitBySizeParameters)) {
            return false;
        }
        SplitBySizeParameters parameter = (SplitBySizeParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(sizeToSplitAt, parameter.getSizeToSplitAt())
                .isEquals();
    }
}
