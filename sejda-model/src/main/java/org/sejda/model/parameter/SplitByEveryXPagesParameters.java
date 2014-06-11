/*
 * Created on 11/giu/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import java.util.Set;

import javax.validation.constraints.Min;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sejda.common.collection.NullSafeSet;

/**
 * Parameter class for a split by every X pages task. Used to perform split where an input pdf document is divided into documents of X pages.
 * 
 * @author Andrea Vacondio
 * 
 */
public class SplitByEveryXPagesParameters extends AbstractSplitByPageParameters {

    @Min(value = 1)
    private int step = 1;

    public SplitByEveryXPagesParameters(int step) {
        this.step = step;
    }

    @Override
    public Set<Integer> getPages(int upperLimit) {
        Set<Integer> pages = new NullSafeSet<Integer>();
        for (int i = step; i <= upperLimit; i += step) {
            pages.add(i);
        }
        return pages;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append(step).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(step).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SplitByEveryXPagesParameters)) {
            return false;
        }
        SplitByEveryXPagesParameters parameter = (SplitByEveryXPagesParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(step, parameter.step).isEquals();
    }
}
