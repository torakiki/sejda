/*
 * Created on 28/jul/2011
 *
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
package org.sejda.core.manipulation.model.parameter;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Parameter class for a simple split task. Used to perform split types which are not requiring a collection of pages to split at.
 * 
 * @author Andrea Vacondio
 * 
 */
public class SimpleSplitParameters extends SinglePdfSourceParameters {

    private String outputPrefix = "";
    @NotNull
    private SimpleSplitType splitType;

    public SimpleSplitParameters(SimpleSplitType splitType) {
        super();
        this.splitType = splitType;
    }

    public String getOutputPrefix() {
        return outputPrefix;
    }

    public void setOutputPrefix(String outputPrefix) {
        this.outputPrefix = outputPrefix;
    }

    public SimpleSplitType getSplitType() {
        return splitType;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(outputPrefix).append(splitType).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RotateParameters)) {
            return false;
        }
        SimpleSplitParameters parameter = (SimpleSplitParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(outputPrefix, parameter.getOutputPrefix())
                .append(splitType, parameter.getSplitType()).isEquals();
    }
}
