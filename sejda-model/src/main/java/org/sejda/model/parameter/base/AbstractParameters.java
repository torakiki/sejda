/*
 * Created on 18/set/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.parameter.base;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * @author Andrea Vacondio
 * 
 */
public abstract class AbstractParameters implements TaskParameters {

    private boolean overwrite = false;

    public boolean isOverwrite() {
        return overwrite;
    }

    /**
     * Set if the output should be overwritten if already exists
     * 
     * @param overwrite
     */
    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(overwrite).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AbstractParameters)) {
            return false;
        }
        AbstractParameters parameter = (AbstractParameters) other;
        return new EqualsBuilder().append(overwrite, parameter.isOverwrite()).isEquals();
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}