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
package org.sejda.model.parameter.base;

import javax.validation.Valid;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.output.OutputType;
import org.sejda.model.output.TaskOutput;
import org.sejda.model.validation.constraint.TaskOutputAllowedTypes;

/**
 * Provides a skeletal implementation for the parameter classes having a single pdf source as input and generating multiple output.
 * 
 * @author Andrea Vacondio
 * 
 */
public abstract class SinglePdfSourceMultipleOutputParameters extends SinglePdfSourceParameters implements
        MultipleOutputTaskParameters {

    private String outputPrefix = "";
    @Valid
    @TaskOutputAllowedTypes(values = { OutputType.DIRECTORY_OUTPUT, OutputType.STREAM_OUTPUT })
    private TaskOutput output;

    public String getOutputPrefix() {
        return outputPrefix;
    }

    public void setOutputPrefix(String outputPrefix) {
        this.outputPrefix = outputPrefix;
    }

    @Override
    public TaskOutput getOutput() {
        return output;
    }

    @Override
    public void setOutput(TaskOutput output) {
        this.output = output;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(outputPrefix).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SinglePdfSourceMultipleOutputParameters)) {
            return false;
        }
        SinglePdfSourceMultipleOutputParameters parameter = (SinglePdfSourceMultipleOutputParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(outputPrefix, parameter.getOutputPrefix())
                .isEquals();
    }
}
